/**
 * Representa um ciclista estrangeiro no sistema
 * Estende a classe Ciclista adicionando passaporte e nacionalidade
 * 
 * @Entity Indica que é uma entidade JPA
 * @DiscriminatorValue Define o valor que identifica um estrangeiro na tabela
 */
package com.example.demo.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@DiscriminatorValue("ESTRANGEIRO")
public class Estrangeiro extends Ciclista {

	@NotBlank(message = "O passaporte não pode estar em branco")
	private String passaporte;

	@NotBlank(message = "A nacionalidade não pode estar em branco")
	private String nacionalidade;

	public String getPassaporte() {
		return passaporte;
	}

	public void setPassaporte(String passaporte) {
		this.passaporte = passaporte;
	}

	public String getNacionalidade() {
		return nacionalidade;
	}

	public void setNacionalidade(String nacionalidade) {
		this.nacionalidade = nacionalidade;
	}
}