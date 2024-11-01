/**
 * DTO para atualização dos dados do ciclista
 * Contém apenas os campos que podem ser atualizados pelo usuário
 */
package com.example.demo.dto;

public class CiclistaAtualizacaoDTO {

	private String nome;
	private String email;
	private String telefone;

	// Getters e setters

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

}
