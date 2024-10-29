package com.example.demo.controller;

import com.example.demo.model.Tranca;
import com.example.demo.model.StatusTranca;
import com.example.demo.service.TrancaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trancas")
public class TrancaController {

	@Autowired
	private TrancaService trancaService;

	@GetMapping
	public List<Tranca> listarTodas() {
		return trancaService.listarTodas();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Tranca> buscarPorId(@PathVariable Long id) {
		return trancaService.buscarPorId(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public Tranca criar(@RequestBody Tranca tranca) {
		return trancaService.salvar(tranca);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Tranca> atualizar(@PathVariable Long id, @RequestBody Tranca tranca) {
		if (!trancaService.buscarPorId(id).isPresent()) {
			return ResponseEntity.notFound().build();
		}
		tranca.setId(id);
		return ResponseEntity.ok(trancaService.salvar(tranca));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<Tranca> atualizarParcial(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
		try {
			Tranca trancaAtualizada = trancaService.atualizarParcial(id, updates);
			return ResponseEntity.ok(trancaAtualizada);
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping("/{trancaId}/bicicletas/{bicicletaId}")
	public ResponseEntity<Tranca> associarBicicleta(@PathVariable Long trancaId, @PathVariable Long bicicletaId) {
		try {
			Tranca trancaAtualizada = trancaService.associarBicicleta(trancaId, bicicletaId);
			return ResponseEntity.ok(trancaAtualizada);
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletar(@PathVariable Long id) {
		if (!trancaService.buscarPorId(id).isPresent()) {
			return ResponseEntity.notFound().build();
		}
		trancaService.deletar(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/status/{status}")
	public List<Tranca> buscarPorStatus(@PathVariable StatusTranca status) {
		return trancaService.buscarPorStatus(status);
	}

	@GetMapping("/totem/{totemId}")
	public List<Tranca> buscarPorTotem(@PathVariable Long totemId) {
		return trancaService.buscarPorTotem(totemId);
	}

	@PatchMapping("/{id}/status")
	public ResponseEntity<Void> atualizarStatus(@PathVariable Long id, @RequestBody StatusTranca novoStatus) {
		boolean atualizado = trancaService.atualizarStatus(id, novoStatus);
		return atualizado ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
	}
}