/**
 * Controller REST responsável pelo gerenciamento dos totens do sistema
 * Endpoints para gerenciar os pontos físicos onde as bicicletas
 * ficam disponíveis para empréstimo, incluindo suas trancas
 *
 * @RestController Indica que esta classe é um controller REST
 * @RequestMapping Define o path base "/api/totens" para todos os endpoints
 */
package com.example.demo.controller;

import com.example.demo.model.Totem;
import com.example.demo.model.Tranca;
import com.example.demo.service.TotemService;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/totens")
public class TotemController {

	@Autowired
	private TotemService totemService;

	/**
     * Lista todos os totens cadastrados no sistema
     *
     * @return List<Totem> lista de todos os totens disponíveis
     */
	@GetMapping
	public List<Totem> listarTodos() {
		return totemService.listarTodos();
	}

	/**
     * Busca um totem específico por seu ID
     * Inclui informações sobre suas trancas e bicicletas
     *
     * @param id identificador único do totem
     * @return ResponseEntity<Totem> totem encontrado ou notFound
     */
	@GetMapping("/{id}")
	public ResponseEntity<Totem> buscarPorId(@PathVariable Long id) {
		try {
			Totem totem = totemService.buscarPorId(id);
			return ResponseEntity.ok(totem);
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
     * Cadastra um novo totem no sistema
     * Valida a capacidade e localização do novo totem
     *
     * @param totem dados do novo totem a ser cadastrado
     * @return ResponseEntity<Totem> totem criado com ID gerado
     */
	@PostMapping
	public ResponseEntity<Totem> criar(@RequestBody Totem totem) {
		Totem novoTotem = totemService.salvar(totem);
		return ResponseEntity.ok(novoTotem);
	}

	/**
     * Atualiza as informações de um totem existente
     * Permite modificar localização, descrição e capacidade
     *
     * @param id ID do totem a ser atualizado
     * @param totem novos dados do totem
     * @return ResponseEntity<Totem> totem atualizado ou erro
     */
	@PutMapping("/{id}")
	public ResponseEntity<Totem> atualizar(@PathVariable Long id, @RequestBody Totem totem) {
		try {
			System.out.println("Recebida requisição para atualizar Totem com ID: " + id);
			Totem totemAtualizado = totemService.atualizar(id, totem);
			System.out.println("Totem atualizado com sucesso");
			return ResponseEntity.ok(totemAtualizado);
		} catch (Exception e) {
			System.out.println("Erro ao atualizar Totem: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.notFound().build();
		}
	}

	/**
     * Atualiza parcialmente um totem
     * Permite modificar apenas alguns campos específicos
     *
     * @param id ID do totem a ser atualizado
     * @param totemParcial objeto contendo apenas os campos a serem atualizados
     * @return ResponseEntity<Totem> totem atualizado ou erro
     */
	@PatchMapping("/{id}")
	public ResponseEntity<Totem> atualizarParcial(@PathVariable Long id, @RequestBody Totem totemParcial) {
		try {
			System.out.println("Recebida requisição PATCH para Totem ID: " + id);
			Totem totemAtualizado = totemService.atualizarParcial(id, totemParcial);
			return ResponseEntity.ok(totemAtualizado);
		} catch (RuntimeException e) {
			System.out.println("Erro ao atualizar Totem: " + e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}

	/**
     * Remove um totem do sistema
     * O totem não deve ter trancas ou bicicletas associadas
     *
     * @param id ID do totem a ser removido
     * @return ResponseEntity<Void> sucesso ou erro
     */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletar(@PathVariable Long id) {
		try {
			totemService.deletar(id);
			return ResponseEntity.noContent().build();
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
     * Busca totens por localização
     *
     * @param localizacao texto contendo a localização desejada
     * @return List<Totem> lista de totens na localização especificada
     */
	@GetMapping("/localizacao/{localizacao}")
	public List<Totem> buscarPorLocalizacao(@PathVariable String localizacao) {
		return totemService.buscarPorLocalizacao(localizacao);
	}

	@PatchMapping("/{id}/capacidade")
	public ResponseEntity<Void> atualizarCapacidade(@PathVariable Long id, @RequestBody JsonNode body) {
		try {
			Integer novaCapacidade;
			if (body.isInt()) {
				novaCapacidade = body.asInt();
			} else if (body.has("capacidade")) {
				novaCapacidade = body.get("capacidade").asInt();
			} else {
				return ResponseEntity.badRequest().build();
			}

			totemService.atualizarCapacidade(id, novaCapacidade);
			return ResponseEntity.ok().build();
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
     * Adiciona uma nova tranca a um totem
     * Valida a capacidade disponível antes da adição
     *
     * @param id ID do totem que receberá a nova tranca
     * @param tranca dados da tranca a ser adicionada
     * @return ResponseEntity<Totem> totem atualizado com a nova tranca
     */
	@PostMapping("/{id}/trancas")
	public ResponseEntity<Totem> adicionarTranca(@PathVariable Long id, @RequestBody Tranca tranca) {
		try {
			Totem totemAtualizado = totemService.adicionarTranca(id, tranca);
			return ResponseEntity.ok(totemAtualizado);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(null);
		}
	}
}