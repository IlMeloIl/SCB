package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Tranca {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String numero;

	@Enumerated(EnumType.STRING)
	private StatusTranca status;

	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "totem_id")
	private Totem totem;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "bicicleta_id")
	private Bicicleta bicicleta;

	// Getters e Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public StatusTranca getStatus() {
		return status;
	}

	public void setStatus(StatusTranca status) {
		this.status = status;
	}

	public Totem getTotem() {
		return totem;
	}

	public void setTotem(Totem totem) {
		this.totem = totem;
	}

	public Bicicleta getBicicleta() {
		return bicicleta;
	}

	public void setBicicleta(Bicicleta bicicleta) {
		this.bicicleta = bicicleta;
	}

}