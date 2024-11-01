/**
 * Representa um totem físico do sistema
 * Um totem é um ponto de retirada/devolução que contém múltiplas trancas
 * 
 * @Entity Indica que é uma entidade JPA
 * @NoArgsConstructor Lombok: gera construtor padrão
 */
package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@NoArgsConstructor
@Entity
public class Totem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "A localização não pode ser vazia")
	private String localizacao;
	private String descricao;

	@Positive(message = "A capacidade máxima deve ser um número positivo")
	@Column(name = "capacidade_maxima")
	private Integer capacidadeMaxima = 20;

	@JsonManagedReference
	@OneToMany(mappedBy = "totem", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Tranca> trancas = new ArrayList<>();;

	// Getters e Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getCapacidadeMaxima() {
		return capacidadeMaxima;
	}

	public void setCapacidadeMaxima(Integer capacidadeMaxima) {
		this.capacidadeMaxima = capacidadeMaxima;
	}

	public List<Tranca> getTrancas() {
		return trancas;
	}

	public void setTrancas(List<Tranca> trancas) {
		this.trancas = trancas;
	}

	// Métodos de gerenciamento de tranca
	
	/**
     * Adiciona uma nova tranca ao totem
     * @param tranca Tranca a ser adicionada
     */
	public void addTranca(Tranca tranca) {
		trancas.add(tranca);
		tranca.setTotem(this);
	}
	
	/**
     * Remove uma tranca do totem
     * @param tranca Tranca a ser removida
     */
	public void removeTranca(Tranca tranca) {
		trancas.remove(tranca);
		tranca.setTotem(null);
	}

}