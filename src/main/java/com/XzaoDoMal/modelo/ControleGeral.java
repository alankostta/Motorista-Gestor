package com.XzaoDoMal.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "tb_controle")
public class ControleGeral implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "data")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date data;

	@Column(name = "valor_faturado_app", precision = 18, scale =2)
	private BigDecimal valorFaturadoApp;

	@Column(name = "inicio_jornada")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime inicioJornada;

	@Column(name = "fim_jornada")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime fimJornada;

	@Column(name = "qtd_km")
	private Integer quantidadeKm;

	@Transient // Para que este campo não seja persistido no banco de dados
	private String duracaoFormatada;

	@ManyToOne
	@JoinColumn(name = "motorista_fk")
	private Motorista motorista;

	public ControleGeral() {
		super();
	}

	public ControleGeral(Long id, Date data, BigDecimal valorFaturadoApp, LocalDateTime inicioJornada,
			LocalDateTime fimJornada, Integer quantidadeKm,
			Motorista motorista) {
		super();
		this.id = id;
		this.data = data;
		this.valorFaturadoApp = valorFaturadoApp;
		this.inicioJornada = inicioJornada;
		this.fimJornada = fimJornada;
		this.quantidadeKm = quantidadeKm;
		this.motorista = motorista;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public BigDecimal getValorFaturadoApp() {
		return valorFaturadoApp;
	}

	public void setValorFaturadoApp(BigDecimal valorFaturadoApp) {
		this.valorFaturadoApp = valorFaturadoApp;
	}

	public LocalDateTime getInicioJornada() {
		return inicioJornada;
	}

	public void setInicioJornada(LocalDateTime inicioJornada) {
		this.inicioJornada = inicioJornada;
	}

	public LocalDateTime getFimJornada() {
		return fimJornada;
	}

	public void setFimJornada(LocalDateTime fimJornada) {
		this.fimJornada = fimJornada;
	}

	public Integer getQuantidadeKm() {
		return quantidadeKm;
	}

	public void setQuantidadeKm(Integer quantidadeKm) {
		this.quantidadeKm = quantidadeKm;
	}

	public Motorista getMotorista() {
		return motorista;
	}

	public void setMotorista(Motorista motorista) {
		this.motorista = motorista;
	}

	public String getDuracaoFormatada() {
		return formatDuration(calcularDuracao());
	}

	// Método auxiliar para formatar Duration
	private String formatDuration(Duration duration) {
		long hours = duration.toHours();
		long minutes = duration.toMinutes() % 60;
		return String.format("%02d:%02d", hours, minutes); // Formato "hh:mm"
	}

	public Duration calcularDuracao() {
		if (this.inicioJornada != null && fimJornada != null) {
			return Duration.between(inicioJornada, fimJornada);
		}
		return Duration.ZERO;
	}
}
