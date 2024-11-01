/**
 * Representa um cartão de crédito no sistema
 * Armazena informações necessárias para cobrança de empréstimos
 * 
 * @Entity Indica que é uma entidade JPA
 * @NoArgsConstructor Lombok: gera construtor padrão
 */
package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
public class CartaoCredito {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "ciclista_id")
	private Ciclista ciclista;

	@NotBlank(message = "Número do cartão é obrigatório")
	@Pattern(regexp = "^\\d{16}$", message = "Número do cartão deve ter 16 dígitos")
	private String numero;

	@NotBlank(message = "Nome do titular é obrigatório")
	private String nomeTitular;

	@NotBlank(message = "Validade é obrigatória")
	@Pattern(regexp = "^(0[1-9]|1[0-2])/([0-9]{2})$", message = "Validade deve estar no formato MM/YY")
	private String validade;

	@NotBlank(message = "CVV é obrigatório")
	@Pattern(regexp = "^[0-9]{3,4}$", message = "CVV deve ter 3 ou 4 dígitos")
	private String cvv;
	private boolean principal = false;

	// Getters e Setters
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

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getNomeTitular() {
		return nomeTitular;
	}

	public void setNomeTitular(String nomeTitular) {
		this.nomeTitular = nomeTitular;
	}

	public String getValidade() {
		return validade;
	}

	public void setValidade(String validade) {
		this.validade = validade;
	}

	public String getCvv() {
		return cvv;
	}

	public void setCvv(String cvv) {
		this.cvv = cvv;
	}

	public boolean isPrincipal() {
		return principal;
	}

	public void setPrincipal(boolean principal) {
		this.principal = principal;
	}
}