// JavaScript principal do DriveManager
function calcularHoras() {
    // Pegando os valores dos inputs
    const startTime = document.getElementById('startTime').value;
    const endTime = document.getElementById('endTime').value;

    if (!startTime || !endTime) {
        alert("Por favor, preencha os horários inicial e final.");
        return;
    }

    // Converter para objetos Date
    const start = new Date(`1970-01-01T${startTime}:00`);
    const end = new Date(`1970-01-01T${endTime}:00`);

    // Calcular diferença em horas
    const diffMs = end - start;
    const horasTrabalhadas = diffMs / (1000 * 60 * 60);

    // Preencher o campo hoursWorked
    document.getElementById('hoursWorked').value = horasTrabalhadas.toFixed(2);
}

function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    const isMobile = window.innerWidth <= 768;
    
    if (isMobile) {
        sidebar.classList.toggle('show');
        toggleSidebarOverlay();
    } else {
        sidebar.classList.toggle('collapsed');
    }
}

function toggleSidebarOverlay() {
    let overlay = document.querySelector('.sidebar-overlay');
    if (!overlay) {
        overlay = document.createElement('div');
        overlay.className = 'sidebar-overlay';
        overlay.addEventListener('click', closeMobileSidebar);
        document.body.appendChild(overlay);
    }
    overlay.classList.toggle('show');
}

function closeMobileSidebar() {
    const sidebar = document.getElementById('sidebar');
    const overlay = document.querySelector('.sidebar-overlay');
    
    sidebar.classList.remove('show');
    if (overlay) {
        overlay.classList.remove('show');
    }
}

// Fechar sidebar mobile ao redimensionar para desktop
window.addEventListener('resize', function() {
    const sidebar = document.getElementById('sidebar');
    const overlay = document.querySelector('.sidebar-overlay');
    
    if (window.innerWidth > 768) {
        sidebar.classList.remove('show');
        if (overlay) {
            overlay.classList.remove('show');
        }
    }
});

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

    // Carregar dados dinâmicos de análise (apenas na página analytics)
    const ganhoHoraElement = document.getElementById('ganhoHora');
    if (ganhoHoraElement) {
        fetch('/analytics-data')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Erro ao carregar dados de analytics');
                }
                return response.json();
            })
            .then(data => {
                // Atualizar cards com formatação monetária brasileira
                const formatCurrency = (value) => {
                    return new Intl.NumberFormat('pt-BR', {
                        style: 'currency',
                        currency: 'BRL'
                    }).format(parseFloat(value) || 0);
                };

                document.getElementById('ganhoHora').textContent = formatCurrency(data.ganhoHora);
                document.getElementById('ganhoKm').textContent = formatCurrency(data.ganhoKm);
                document.getElementById('custoKm').textContent = formatCurrency(data.custoKm);
                document.getElementById('custoDia').textContent = formatCurrency(data.custoDia);

                // Carregar dados de Km por mês
                function carregarDadosKm() {
                    console.log('=== INICIANDO CARREGAMENTO DE DADOS DE KM ===');
                    console.log('Timestamp:', new Date().toISOString());
                    console.log('URL atual:', window.location.href);
                    
                    const kmMesElement = document.getElementById('kmMes');
                    const kmDiaElement = document.getElementById('kmDia');
                    const diasComRegistroElement = document.getElementById('diasComRegistro');
                    const mesAnoElement = document.getElementById('mesAno');
                    
                    console.log('Elementos encontrados:', {
                        kmMes: !!kmMesElement,
                        kmDia: !!kmDiaElement,
                        diasComRegistro: !!diasComRegistroElement,
                        mesAno: !!mesAnoElement
                    });
                    
                    // Verificar se já existe um indicador de carregamento ativo
                    if (window.kmLoadingInProgress) {
                        console.log('AVISO: Carregamento de Km já em progresso, ignorando nova chamada');
                        return;
                    }
                    window.kmLoadingInProgress = true;
                    
                    if (kmMesElement && kmDiaElement && diasComRegistroElement) {
                        fetch('/km-by-month')
                            .then(response => {
                                console.log('Response status:', response.status);
                                if (!response.ok) {
                                    throw new Error('Erro ao carregar dados de Km: ' + response.status);
                                }
                                return response.json();
                            })
                            .then(kmData => {
                                console.log('Dados de Km recebidos:', kmData);
                                
                                // Os dados já vêm formatados do backend
                                kmMesElement.textContent = kmData.kmMes || '0 KM';
                                kmDiaElement.textContent = kmData.kmDia || '0 KM';
                                diasComRegistroElement.textContent = kmData.diasComRegistro || '0';
                                
                                // Atualizar o mês/ano se o elemento existir
                                if (mesAnoElement && kmData.mesAno) {
                                    mesAnoElement.textContent = kmData.mesAno;
                                }
                                
                                console.log('Dados de Km atualizados com sucesso');
                                console.log('=== CARREGAMENTO DE KM CONCLUÍDO COM SUCESSO ===');
                            })
                            .catch(error => {
                                console.error('Erro ao carregar dados de Km:', error);
                                kmMesElement.textContent = 'Erro';
                                kmDiaElement.textContent = 'Erro';
                                diasComRegistroElement.textContent = 'Erro';
                                if (mesAnoElement) {
                                    mesAnoElement.textContent = 'Erro ao carregar';
                                }
                                console.log('=== CARREGAMENTO DE KM CONCLUÍDO COM ERRO ===');
                            })
                            .finally(() => {
                                window.kmLoadingInProgress = false;
                                console.log('Flag de carregamento resetada');
                            });
                    } else {
                        console.error('Elementos de Km não encontrados no DOM');
                        window.kmLoadingInProgress = false;
                        console.log('=== CARREGAMENTO DE KM CANCELADO - ELEMENTOS NÃO ENCONTRADOS ===');
                    }
                }
                
                // Aguardar um pouco antes de carregar os dados
                setTimeout(carregarDadosKm, 1000);

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
            // Exibir valores padrão em caso de erro
            document.getElementById('ganhoHora').textContent = 'R$ 0,00';
            document.getElementById('ganhoKm').textContent = 'R$ 0,00';
            document.getElementById('custoKm').textContent = 'R$ 0,00';
            document.getElementById('custoDia').textContent = 'R$ 0,00';
        });
    }

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

    // Gráfico de despesas por categoria por semana
    const expenseCategoryChartCanvas = document.getElementById('expenseCategoryChart');
    if (expenseCategoryChartCanvas) {
        fetch('/analytics-data')
            .then(response => response.json())
            .then(data => {
                if (data.custosPorSemana && Object.keys(data.custosPorSemana).length > 0) {
                    const custosPorSemana = data.custosPorSemana;
                    const semanas = Object.keys(custosPorSemana).sort();
                    const categorias = ['Combustível', 'Alimentação', 'Manutenção', 'Outros'];
                    
                    // Cores para cada categoria
                    const coresCategorias = {
                        'Combustível': '#FF6384',
                        'Alimentação': '#36A2EB', 
                        'Manutenção': '#FFCE56',
                        'Outros': '#4BC0C0'
                    };
                    
                    // Criar datasets para cada categoria
                    const datasets = categorias.map(categoria => {
                        const dadosCategoria = semanas.map(semana => {
                            const custosSemanais = custosPorSemana[semana];
                            return custosSemanais[categoria] || 0;
                        });
                        
                        // Verificar se a categoria tem dados
                        const temDados = dadosCategoria.some(valor => valor > 0);
                        
                        if (temDados) {
                            return {
                                label: categoria,
                                data: dadosCategoria,
                                borderColor: coresCategorias[categoria],
                                backgroundColor: coresCategorias[categoria] + '20',
                                borderWidth: 3,
                                fill: false,
                                tension: 0.4,
                                pointBackgroundColor: coresCategorias[categoria],
                                pointBorderColor: '#ffffff',
                                pointBorderWidth: 2,
                                pointRadius: 6,
                                pointHoverRadius: 8
                            };
                        }
                        return null;
                    }).filter(dataset => dataset !== null);
                    
                    if (datasets.length > 0) {
                        new Chart(expenseCategoryChartCanvas.getContext('2d'), {
                            type: 'line',
                            data: {
                                labels: semanas,
                                datasets: datasets
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
                    } else {
                        // Exibir mensagem quando não há dados
                        const ctx = expenseCategoryChartCanvas.getContext('2d');
                        ctx.font = '16px Arial';
                        ctx.fillStyle = '#6b7280';
                        ctx.textAlign = 'center';
                        ctx.fillText('Nenhuma despesa registrada neste mês', 
                                   expenseCategoryChartCanvas.width / 2, 
                                   expenseCategoryChartCanvas.height / 2);
                    }
                } else {
                    // Exibir mensagem quando não há dados semanais
                    const ctx = expenseCategoryChartCanvas.getContext('2d');
                    ctx.font = '16px Arial';
                    ctx.fillStyle = '#6b7280';
                    ctx.textAlign = 'center';
                    ctx.fillText('Nenhuma despesa registrada neste mês', 
                               expenseCategoryChartCanvas.width / 2, 
                               expenseCategoryChartCanvas.height / 2);
                }
            })
            .catch(error => {
                console.error("Erro ao carregar dados de despesas por categoria:", error);
                // Exibir mensagem de erro no canvas
                const ctx = expenseCategoryChartCanvas.getContext('2d');
                ctx.font = '16px Arial';
                ctx.fillStyle = '#ef4444';
                ctx.textAlign = 'center';
                ctx.fillText('Erro ao carregar dados', 
                           expenseCategoryChartCanvas.width / 2, 
                           expenseCategoryChartCanvas.height / 2);
            });
    }

    // Gráfico de Ganhos vs Despesas (Dashboard)
    const earningsExpensesCanvas = document.getElementById('earningsExpensesChart');
    if (earningsExpensesCanvas) {
        fetch('/analytics-data')
            .then(response => response.json())
            .then(data => {
                new Chart(earningsExpensesCanvas.getContext('2d'), {
                    type: 'bar',
                    data: {
                        labels: ['Ganhos', 'Despesas'],
                        datasets: [{
                            label: 'Valores',
                            data: [data.totalEarnings || 0, data.totalCosts || 0],
                            backgroundColor: ['#10b981', '#ef4444'],
                            borderColor: ['#059669', '#dc2626'],
                            borderWidth: 2,
                            borderRadius: 8,
                            borderSkipped: false
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: { display: false },
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        return `${context.label}: R$ ${context.parsed.y.toFixed(2)}`;
                                    }
                                }
                            }
                        },
                        scales: {
                            y: {
                                beginAtZero: true,
                                title: {
                                    display: true,
                                    text: 'Valor (R$)'
                                },
                                grid: {
                                    color: '#e5e7eb'
                                }
                            },
                            x: {
                                grid: { display: false }
                            }
                        }
                    }
                });
            })
            .catch(error => {
                console.error("Erro ao carregar dados do gráfico Ganhos vs Despesas:", error);
                // Exibir mensagem de erro no canvas
                const ctx = earningsExpensesCanvas.getContext('2d');
                ctx.font = '16px Arial';
                ctx.fillStyle = '#ef4444';
                ctx.textAlign = 'center';
                ctx.fillText('Erro ao carregar dados', 
                           earningsExpensesCanvas.width / 2, 
                           earningsExpensesCanvas.height / 2);
            });
    }

    // Gráfico de Ganhos por Dia (Dashboard)
    const dailyEarningsCanvas = document.getElementById('dailyEarningsChart');
    if (dailyEarningsCanvas) {
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

                function generateDailyEarningsData(records) {
                    const now = new Date();
                    const days = getDaysInMonth(now.getFullYear(), now.getMonth());

                    return days.map(day => {
                        const formattedDay = day.toISOString().split("T")[0];
                        const record = records.find(r => r.date === formattedDay);
                        
                        return {
                            date: formatDate(day),
                            ganhos: record?.gross_earnings || 0
                        };
                    });
                }

                const data = generateDailyEarningsData(records);

                new Chart(dailyEarningsCanvas.getContext('2d'), {
                    type: 'line',
                    data: {
                        labels: data.map(d => d.date),
                        datasets: [{
                            label: 'Ganhos Diários',
                            data: data.map(d => d.ganhos),
                            borderColor: '#10b981',
                            backgroundColor: 'rgba(16, 185, 129, 0.1)',
                            borderWidth: 3,
                            fill: true,
                            tension: 0.4,
                            pointBackgroundColor: '#10b981',
                            pointBorderColor: '#ffffff',
                            pointBorderWidth: 2,
                            pointRadius: 5,
                            pointHoverRadius: 8
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: { display: false },
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        return `Ganhos: R$ ${context.parsed.y.toFixed(2)}`;
                                    }
                                }
                            }
                        },
                        scales: {
                            y: {
                                beginAtZero: true,
                                title: {
                                    display: true,
                                    text: 'Ganhos (R$)'
                                },
                                grid: {
                                    color: '#e5e7eb'
                                }
                            },
                            x: {
                                grid: { display: false }
                            }
                        }
                    }
                });
            })
            .catch(error => {
                console.error("Erro ao carregar dados do gráfico Ganhos por Dia:", error);
                // Exibir mensagem de erro no canvas
                const ctx = dailyEarningsCanvas.getContext('2d');
                ctx.font = '16px Arial';
                ctx.fillStyle = '#ef4444';
                ctx.textAlign = 'center';
                ctx.fillText('Erro ao carregar dados', 
                           dailyEarningsCanvas.width / 2, 
                           dailyEarningsCanvas.height / 2);
            });
    }

    // Carregar gráfico de quilometragem diária
    console.log('Iniciando carregamento do gráfico de quilometragem...');
    console.log('Chart.js disponível:', typeof Chart !== 'undefined');
    
    const kmWeeklyCanvas = document.getElementById('kmWeeklyChart');
    console.log('Canvas encontrado:', kmWeeklyCanvas);
    
    if (kmWeeklyCanvas && typeof Chart !== 'undefined') {
        console.log('Fazendo requisição para /km-chart-data...');
        fetch('/km-chart-data')
            .then(response => {
                console.log('Resposta recebida:', response.status, response.statusText);
                if (!response.ok) {
                    throw new Error(`Erro ao carregar dados de quilometragem: ${response.status} ${response.statusText}`);
                }
                return response.json();
            })
            .then(data => {
                console.log('Dados do gráfico de KM recebidos:', data);
                
                new Chart(kmWeeklyCanvas, {
                    type: 'line',
                    data: {
                        labels: data.labels,
                        datasets: [{
                            label: 'Quilômetros',
                            data: data.data,
                            borderColor: '#3b82f6',
                            backgroundColor: 'rgba(59, 130, 246, 0.1)',
                            borderWidth: 3,
                            fill: true,
                            tension: 0.4,
                            pointBackgroundColor: '#3b82f6',
                            pointBorderColor: '#ffffff',
                            pointBorderWidth: 2,
                            pointRadius: 6,
                            pointHoverRadius: 8
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: {
                                display: false
                            },
                            tooltip: {
                                backgroundColor: 'rgba(0, 0, 0, 0.8)',
                                titleColor: '#ffffff',
                                bodyColor: '#ffffff',
                                borderColor: '#3b82f6',
                                borderWidth: 1,
                                cornerRadius: 8,
                                displayColors: false,
                                callbacks: {
                                    label: function(context) {
                                        return `${context.parsed.y} km`;
                                    }
                                }
                            }
                        },
                        scales: {
                            y: {
                                beginAtZero: true,
                                title: {
                                    display: true,
                                    text: 'Quilômetros (km)',
                                    color: '#6b7280'
                                },
                                grid: {
                                    color: '#e5e7eb'
                                },
                                ticks: {
                                    color: '#6b7280',
                                    callback: function(value) {
                                        return value + ' km';
                                    }
                                }
                            },
                            x: {
                                title: {
                                    display: true,
                                    text: 'Dias do Mês',
                                    color: '#6b7280'
                                },
                                grid: { 
                                    display: false 
                                },
                                ticks: {
                                    color: '#6b7280'
                                }
                            }
                        }
                    }
                });
            })
            .catch(error => {
                console.error("Erro ao carregar dados do gráfico de quilometragem:", error);
                // Exibir mensagem de erro no canvas
                const ctx = kmWeeklyCanvas.getContext('2d');
                ctx.font = '16px Arial';
                ctx.fillStyle = '#ef4444';
                ctx.textAlign = 'center';
                ctx.fillText('Erro ao carregar dados de quilometragem', 
                           kmWeeklyCanvas.width / 2, 
                           kmWeeklyCanvas.height / 2);
            });
    } else {
        console.error('Gráfico de quilometragem não pode ser carregado:', {
            canvas: !!kmWeeklyCanvas,
            chartJs: typeof Chart !== 'undefined'
        });
    }
});