/**
 * DTO para transferência de dados de empréstimo
 * Usado tanto para novos empréstimos quanto para consultas
 */
package com.example.demo.dto;

import java.time.LocalDateTime;

public class EmprestimoDTO {
	// Identificadores principais
	private Long id;
	private Long ciclistaId;
	private Long bicicletaId;

	// Dados da retirada
	private Long trancaInicioId;
	private Long totemInicioId;
	private LocalDateTime horaInicio;
	private Double taxaInicial;

	// Dados da devolução
	private Long trancaFimId;
	private Long totemFimId;
	private LocalDateTime horaFim;
	private Double taxaExtra;
	private String status;

	// Getters e setters

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCiclistaId() {
		return ciclistaId;
	}

	public void setCiclistaId(Long ciclistaId) {
		this.ciclistaId = ciclistaId;
	}

	public Long getBicicletaId() {
		return bicicletaId;
	}

	public void setBicicletaId(Long bicicletaId) {
		this.bicicletaId = bicicletaId;
	}

	public Long getTrancaInicioId() {
		return trancaInicioId;
	}

	public void setTrancaInicioId(Long trancaInicioId) {
		this.trancaInicioId = trancaInicioId;
	}

	public Long getTrancaFimId() {
		return trancaFimId;
	}

	public void setTrancaFimId(Long trancaFimId) {
		this.trancaFimId = trancaFimId;
	}

	public Long getTotemInicioId() {
		return totemInicioId;
	}

	public void setTotemInicioId(Long totemInicioId) {
		this.totemInicioId = totemInicioId;
	}

	public Long getTotemFimId() {
		return totemFimId;
	}

	public void setTotemFimId(Long totemFimId) {
		this.totemFimId = totemFimId;
	}

	public LocalDateTime getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(LocalDateTime horaInicio) {
		this.horaInicio = horaInicio;
	}

	public LocalDateTime getHoraFim() {
		return horaFim;
	}

	public void setHoraFim(LocalDateTime horaFim) {
		this.horaFim = horaFim;
	}

	public Double getTaxaInicial() {
		return taxaInicial;
	}

	public void setTaxaInicial(Double taxaInicial) {
		this.taxaInicial = taxaInicial;
	}

	public Double getTaxaExtra() {
		return taxaExtra;
	}

	public void setTaxaExtra(Double taxaExtra) {
		this.taxaExtra = taxaExtra;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}