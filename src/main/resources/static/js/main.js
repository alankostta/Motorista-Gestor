// JavaScript principal do DriveManager

function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    sidebar.classList.toggle('collapsed');
}

document.addEventListener('DOMContentLoaded', function () {
    // Inicializar tooltips do Bootstrap
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Auto-dismiss de alertas após 5 segundos
    setTimeout(function () {
        const alerts = document.querySelectorAll('.alert-dismissible');
        alerts.forEach(function (alert) {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 5000);

    // Formatação de valores monetários
    const moneyInputs = document.querySelectorAll('input[type="number"][step="0.01"]');
    moneyInputs.forEach(function (input) {
        input.addEventListener('blur', function () {
            if (this.value) {
                this.value = parseFloat(this.value).toFixed(2);
            }
        });
    });

    // Confirmação de exclusão
    const deleteButtons = document.querySelectorAll('button[type="submit"]');
    deleteButtons.forEach(function (button) {
        if (button.innerHTML.includes('trash') || button.innerHTML.includes('Excluir')) {
            button.addEventListener('click', function (e) {
                if (!confirm('Tem certeza que deseja excluir este item?')) {
                    e.preventDefault();
                }
            });
        }
    });

    // Aplicar animação fadeIn aos cards
    const cards = document.querySelectorAll('.card');
    cards.forEach(function (card) {
        card.classList.add('fade-in');
    });

    // Carregar dados dinâmicos de análise
    fetch('/analytics-data')
        .then(response => response.json())
        .then(data => {
            // Atualizar cards
            document.getElementById('ganhoHora').textContent = `R$ ${parseFloat(data.ganhoHora).toFixed(2)}`;
            document.getElementById('ganhoKm').textContent = `R$ ${parseFloat(data.ganhoKm).toFixed(2)}`;
            document.getElementById('custoKm').textContent = `R$ ${parseFloat(data.custoKm).toFixed(2)}`;
            document.getElementById('custoDia').textContent = `R$ ${parseFloat(data.custoDia).toFixed(2)}`;

            // Doughnut - Divisão de custos
            const costDivisionCanvas = document.getElementById('costDivisionChart');
            if (costDivisionCanvas) {
                new Chart(costDivisionCanvas.getContext('2d'), {
                    type: 'doughnut',
                    data: {
                        labels: ['Combustível', 'Refeição', 'Manutenção', 'Outros'],
                        datasets: [{
                            data: data.custos,
                            backgroundColor: ['#4285F4', '#EA4335', '#FBBC04', '#34A853'],
                            hoverOffset: 4
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: { display: false },
                            tooltip: {
                                callbacks: {
                                    label: function (context) {
                                        return `${context.label}: R$ ${context.parsed.toFixed(2)}`;
                                    }
                                }
                            }
                        }
                    }
                });
            }

            // Bar Chart - Ganhos por plataforma
            const platformEarningsCanvas = document.getElementById('platformEarningsChart');
            if (platformEarningsCanvas) {
                const plataformas = Object.keys(data.ganhosPlataforma);
                const valores = Object.values(data.ganhosPlataforma);

                new Chart(platformEarningsCanvas.getContext('2d'), {
                    type: 'bar',
                    data: {
                        labels: plataformas,
                        datasets: [{
                            label: 'Ganhos',
                            data: valores,
                            backgroundColor: ['#FBBC04', '#708090', '#10b981'],
                            barThickness: 50
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: { display: false }
                        },
                        scales: {
                            y: {
                                beginAtZero: true,
                                title: {
                                    display: true,
                                    text: 'Valor (R$)'
                                }
                            },
                            x: {
                                grid: { display: false }
                            }
                        }
                    }
                });
            }
        })
        .catch(error => {
            console.error("Erro ao carregar dados de análise:", error);
        });

    // Gráfico de linha mensal (já existente)
    const monthlyChartCanvas = document.getElementById('monthlyChart');
    if (monthlyChartCanvas) {
        fetch('/grafico')
            .then(response => response.json())
            .then(records => {
                function formatDate(date) {
                    return new Date(date).toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit' });
                }

                function getDaysInMonth(year, month) {
                    const date = new Date(year, month, 1);
                    const days = [];
                    while (date.getMonth() === month) {
                        days.push(new Date(date));
                        date.setDate(date.getDate() + 1);
                    }
                    return days;
                }

                function generateChartData(records) {
                    const now = new Date();
                    const days = getDaysInMonth(now.getFullYear(), now.getMonth());

                    return days.map(day => {
                        const formattedDay = day.toISOString().split("T")[0];
                        const record = records.find(r => r.date === formattedDay);

                        const ganhos = record?.gross_earnings || 0;
                        const custos = (record?.fuel_cost || 0) + (record?.food_cost || 0) +
                            (record?.maintenance_cost || 0) + (record?.other_costs || 0);

                        return {
                            date: formatDate(day),
                            ganhos,
                            custos,
                            lucro: ganhos - custos
                        };
                    });
                }

                const data = generateChartData(records);

                new Chart(monthlyChartCanvas.getContext('2d'), {
                    type: 'line',
                    data: {
                        labels: data.map(d => d.date),
                        datasets: [
                            {
                                label: 'Ganhos',
                                data: data.map(d => d.ganhos),
                                borderColor: '#10b981',
                                tension: 0.3
                            },
                            {
                                label: 'Custos',
                                data: data.map(d => d.custos),
                                borderColor: '#ef4444',
                                tension: 0.3
                            },
                            {
                                label: 'Lucro',
                                data: data.map(d => d.lucro),
                                borderColor: '#3b82f6',
                                tension: 0.3
                            }
                        ]
                    },
                    options: {
                        responsive: true,
                        plugins: {
                            tooltip: {
                                callbacks: {
                                    label: (tooltipItem) => {
                                        return `${tooltipItem.dataset.label}: R$ ${tooltipItem.raw.toFixed(2)}`;
                                    }
                                }
                            }
                        },
                        scales: {
                            y: {
                                beginAtZero: true
                            }
                        }
                    }
                });
            })
            .catch(error => {
                console.error("Erro ao carregar dados do gráfico:", error);
            });
    }
});
