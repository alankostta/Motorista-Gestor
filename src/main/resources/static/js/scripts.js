const dadosHoras = {
	labels: ['Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho', 'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'],
	datasets: [{
		label: 'Horas Trabalhadas',
		data: [160, 170, 180, 175, 160, 155, 165, 170, 180, 185, 190, 200],
		backgroundColor: 'rgba(54, 162, 235, 0.2)',
		borderColor: 'rgba(54, 162, 235, 1)',
		borderWidth: 1
	}]
};

// Configuração do gráfico
const configHoras = {
	type: 'line',
	data: dadosHoras,
	options: {
		responsive: true,
		plugins: {
			legend: {
				position: 'top'
			},
			title: {
				display: true,
				text: 'Horas Trabalhadas por Mês'
			}
		}
	}
};
function pesquisar(tipo) {
	const pesquisa = document.getElementById('pesquisa').value;
	let url = `/motorista/pesquisar?tipo=${tipo}&valor=${encodeURIComponent(pesquisa)}`;

	window.location.href = url; // Redireciona para a URL construída
}

// Renderização do gráfico
window.onload = function() {
	const ctx = document.getElementById('horasTrabalhadasChart').getContext('2d');
	new Chart(ctx, configHoras);
};