package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Emprestimo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ciclista_id", nullable = false)
    private Ciclista ciclista;

    @ManyToOne
    @JoinColumn(name = "bicicleta_id", nullable = false)
    private Bicicleta bicicleta;

    @ManyToOne
    @JoinColumn(name = "tranca_inicio_id", nullable = false)
    private Tranca trancaInicio;

    @ManyToOne
    @JoinColumn(name = "totem_inicio_id", nullable = false)
    private Totem totemInicio;

    private LocalDateTime horaInicio;

    @ManyToOne
    @JoinColumn(name = "tranca_fim_id")
    private Tranca trancaFim;

    @ManyToOne
    @JoinColumn(name = "totem_fim_id")
    private Totem totemFim;

    private LocalDateTime horaFim;

    private Double taxaInicial;
    private Double taxaExtra;

    @Enumerated(EnumType.STRING)
    private StatusEmprestimo status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Ciclista getCiclista() {
		return ciclista;
	}

	public void setCiclista(Ciclista ciclista) {
		this.ciclista = ciclista;
	}

	public Bicicleta getBicicleta() {
		return bicicleta;
	}

	public void setBicicleta(Bicicleta bicicleta) {
		this.bicicleta = bicicleta;
	}

	public Tranca getTrancaInicio() {
		return trancaInicio;
	}

	public void setTrancaInicio(Tranca trancaInicio) {
		this.trancaInicio = trancaInicio;
	}

	public Totem getTotemInicio() {
		return totemInicio;
	}

	public void setTotemInicio(Totem totemInicio) {
		this.totemInicio = totemInicio;
	}

	public LocalDateTime getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(LocalDateTime horaInicio) {
		this.horaInicio = horaInicio;
	}

	public Tranca getTrancaFim() {
		return trancaFim;
	}

	public void setTrancaFim(Tranca trancaFim) {
		this.trancaFim = trancaFim;
	}

	public Totem getTotemFim() {
		return totemFim;
	}

	public void setTotemFim(Totem totemFim) {
		this.totemFim = totemFim;
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

	public StatusEmprestimo getStatus() {
		return status;
	}

	public void setStatus(StatusEmprestimo status) {
		this.status = status;
	}
}