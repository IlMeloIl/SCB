package com.example.demo.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
	private Long id;
	private String email;
	private String nome;
	private String tipo;
	private String documento;
	private boolean hasActiveEmprestimo;
	private String currentBikeInfo;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getDocumento() {
		return documento;
	}

	public void setDocumento(String documento) {
		this.documento = documento;
	}

	public boolean isHasActiveEmprestimo() {
		return hasActiveEmprestimo;
	}

	public void setHasActiveEmprestimo(boolean hasActiveEmprestimo) {
		this.hasActiveEmprestimo = hasActiveEmprestimo;
	}

	public String getCurrentBikeInfo() {
		return currentBikeInfo;
	}

	public void setCurrentBikeInfo(String currentBikeInfo) {
		this.currentBikeInfo = currentBikeInfo;
	}
}