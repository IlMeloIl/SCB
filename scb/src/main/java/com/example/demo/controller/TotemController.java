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

	@GetMapping
	public List<Totem> listarTodos() {
		return totemService.listarTodos();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Totem> buscarPorId(@PathVariable Long id) {
		try {
			Totem totem = totemService.buscarPorId(id);
			return ResponseEntity.ok(totem);
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping
	public ResponseEntity<Totem> criar(@RequestBody Totem totem) {
		Totem novoTotem = totemService.salvar(totem);
		return ResponseEntity.ok(novoTotem);
	}

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

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletar(@PathVariable Long id) {
		try {
			totemService.deletar(id);
			return ResponseEntity.noContent().build();
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

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