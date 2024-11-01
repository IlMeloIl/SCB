/**
 * DTO para operações com cartão de crédito
 * Transfere dados de cartão entre camadas sem expor detalhes sensíveis
 */
package com.example.demo.dto;

public class CartaoCreditoDTO {
	private Long id;
	private String numero;
	private String nomeTitular;
	private String validade;
	private String cvv;
	private boolean principal;

	// Getters e setters
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
