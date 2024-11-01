/**
 * DTO para requisição de login
 * Encapsula as credenciais do usuário para autenticação
 */
package com.example.demo.dto;

public class LoginDTO {
	private String email;
	private String senha; // Validada contra o hash

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

}