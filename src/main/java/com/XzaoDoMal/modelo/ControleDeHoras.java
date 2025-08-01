package com.XzaoDoMal.modelo;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.XzaoDoMal.repositorio.ControleGeralRepositorio;

public class ControleDeHoras {

	@Autowired
	private ControleGeralRepositorio controleGeralRepositorio;

	private List<ControleGeral> controlesGerais = new ArrayList<>();

	public ControleDeHoras(List<ControleGeral> controlesGerais) {
		this.controlesGerais = controlesGerais != null ? controlesGerais : new ArrayList<>();
	}

	public void carregarDadosGerais() {
		this.controlesGerais = controleGeralRepositorio.findAll();
		if (this.controlesGerais == null) {
			this.controlesGerais = new ArrayList<>();
		}
	}

	private LocalDate getDataMaisAntiga() {
		return controlesGerais.stream().map(controle -> controle.getInicioJornada().toLocalDate())
				.min(LocalDate::compareTo).orElse(LocalDate.now());
	}

	@SuppressWarnings("unused")
	private LocalDate getDataMaisRecente() {
		return controlesGerais.stream().map(controle -> controle.getFimJornada().toLocalDate())
				.max(LocalDate::compareTo).orElse(LocalDate.now());
	}

	private Duration calcularDuracaoPeriodo(LocalDate inicio, LocalDate fim) {
		if (controlesGerais == null || controlesGerais.isEmpty()) {
			return Duration.ZERO; // Retorna zero se a lista estiver vazia ou nula
		}
		Duration duracaoTotal = Duration.ZERO;

		for (ControleGeral controle : controlesGerais) {
			LocalDateTime inicioJornada = controle.getInicioJornada();
			LocalDateTime fimJornada = controle.getFimJornada();

			// Verifica se a jornada está dentro do período especificado
			if (!inicioJornada.toLocalDate().isBefore(inicio) && !fimJornada.toLocalDate().isAfter(fim)) {
				Duration duracaoJornada = Duration.between(inicioJornada, fimJornada);
				duracaoTotal = duracaoTotal.plus(duracaoJornada); // Acumula a duração corretamente
			}
		}
		return duracaoTotal;
	}

	public Duration calcularDuracaoSemana() {
		LocalDate dataMaisAntiga = getDataMaisAntiga();
		LocalDate inicioSemana = dataMaisAntiga; // Início da semana é a data mais antiga registrada
		LocalDate fimSemana = dataMaisAntiga.plusDays(6); // Considera os próximos 7 dias desde a data mais antiga
		return calcularDuracaoPeriodo(inicioSemana, fimSemana);
	}

	public Duration calcularDuracaoMes() {
		LocalDate dataMaisAntiga = getDataMaisAntiga();
		LocalDate inicioMes = dataMaisAntiga.withDayOfMonth(1); // Início do mês da data mais antiga registrada
		LocalDate fimMes = dataMaisAntiga.withDayOfMonth(dataMaisAntiga.lengthOfMonth()); // Fim do mês
		return calcularDuracaoPeriodo(inicioMes, fimMes);
	}

	public Duration calcularDuracaoAno() {
		LocalDate dataMaisAntiga = getDataMaisAntiga();
		LocalDate inicioAno = dataMaisAntiga.withDayOfYear(1); // Início do ano da data mais antiga registrada
		LocalDate fimAno = dataMaisAntiga.withDayOfYear(dataMaisAntiga.lengthOfYear()); // Fim do ano
		return calcularDuracaoPeriodo(inicioAno, fimAno);
	}

	public String formatarDuracao(Duration duracao) {
		long horas = duracao.toHours();
		long minutos = duracao.toMinutes() % 60;
		long segundos = duracao.getSeconds() % 60;

		// Retorne a duração no formato "HH:mm:ss"
		return String.format("%02d:%02d:%02d", horas, minutos, segundos);
	}
}
