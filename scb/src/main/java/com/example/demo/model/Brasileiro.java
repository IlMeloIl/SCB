/**
 * Representa um ciclista brasileiro no sistema
 * Estende a classe Ciclista adicionando CPF
 * 
 * @Entity Indica que é uma entidade JPA
 * @DiscriminatorValue Define o valor que identifica um brasileiro na tabela
 */
package com.example.demo.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@DiscriminatorValue("BRASILEIRO")
public class Brasileiro extends Ciclista {

	@NotBlank(message = "O CPF não pode estar em branco")
	@Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos numéricos")
	private String cpf;

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
}
