package com.example.demo.controller;

import com.example.demo.model.Bicicleta;
import com.example.demo.model.StatusBicicleta;
import com.example.demo.service.BicicletaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bicicletas")
public class BicicletaController {

	@Autowired
	private BicicletaService bicicletaService;

	@GetMapping
	public List<Bicicleta> listarTodas() {
		return bicicletaService.listarTodas();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Bicicleta> buscarPorId(@PathVariable Long id) {
		return bicicletaService.buscarPorId(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public Bicicleta criar(@RequestBody Bicicleta bicicleta) {
		return bicicletaService.salvar(bicicleta);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Bicicleta> atualizar(@PathVariable Long id, @RequestBody Bicicleta bicicleta) {
		if (!bicicletaService.buscarPorId(id).isPresent()) {
			return ResponseEntity.notFound().build();
		}
		bicicleta.setId(id);
		return ResponseEntity.ok(bicicletaService.salvar(bicicleta));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletar(@PathVariable Long id) {
		if (!bicicletaService.buscarPorId(id).isPresent()) {
			return ResponseEntity.notFound().build();
		}
		bicicletaService.deletar(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/status/{status}")
	public List<Bicicleta> buscarPorStatus(@PathVariable StatusBicicleta status) {
		return bicicletaService.buscarPorStatus(status);
	}

	@PatchMapping("/{id}/status")
	public ResponseEntity<Void> atualizarStatus(@PathVariable Long id, @RequestBody StatusBicicleta novoStatus) {
		boolean atualizado = bicicletaService.atualizarStatus(id, novoStatus);
		return atualizado ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
	}
}