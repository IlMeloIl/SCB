package com.example.demo.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity 
@DiscriminatorValue ("BRASILEIRO")
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
