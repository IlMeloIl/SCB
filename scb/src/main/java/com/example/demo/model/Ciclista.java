/**
 * Classe abstrata que representa um ciclista no sistema
 * Serve como base para Brasileiro e Estrangeiro
 * 
 * @Entity Indica que é uma entidade JPA
 * @Inheritance Define estratégia de herança de tabela única
 * @DiscriminatorColumn Define coluna que diferencia os tipos de ciclista
 */
package com.example.demo.model;

import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_ciclista")
public abstract class Ciclista {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "O nome não pode estar em branco")
	private String nome;

	@NotBlank(message = "A data de nascimento não pode estar em branco")
	@Pattern(regexp = "\\d{2}/\\d{2}/\\d{4}", message = "Data de nascimento deve estar no formato dd/mm/aaaa")
	private String nascimento;

	@Email(message = "Email deve ser válido")
	@NotBlank(message = "Email não pode estar em branco")
	private String email;

	@NotBlank(message = "A senha não pode estar em branco")
	@Size(min = 1, message = "A senha deve ter pelo menos 1 caractere")
	private String senha;

	@NotBlank(message = "O telefone não pode estar em branco")
	@Pattern(regexp = "\\d{10,11}", message = "Telefone deve conter 10 ou 11 dígitos")
	private String telefone;

	private LocalDateTime dataCadastro;

	@Valid
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CartaoCredito> cartoes = new ArrayList<>();

	@NotNull(message = "Status do ciclista não pode ser nulo")
	@Enumerated(EnumType.STRING)
	private StatusCiclista status = StatusCiclista.ATIVO;

	// Getters e Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNascimento() {
		return nascimento;
	}

	public void setNascimento(String nascimento) {
		this.nascimento = nascimento;
	}

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

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public LocalDateTime getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(LocalDateTime dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public List<CartaoCredito> getCartoes() {
		return cartoes;
	}

	public void setCartoes(List<CartaoCredito> cartoes) {
		this.cartoes = cartoes;
	}

	public StatusCiclista getStatus() {
		return status;
	}

	public void setStatus(StatusCiclista status) {
		this.status = status;
	}

}