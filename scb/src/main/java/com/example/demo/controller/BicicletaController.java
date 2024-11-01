/**
 * Controller REST responsável por gerenciar operações relacionadas às bicicletas
 * Endpoints para criar, consultar, atualizar e deletar bicicletas no sistema
 *
 * @RestController Indica que esta classe é um controller REST
 * @RequestMapping Define o path base para todos os endpoints dessa classe
 */
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

	/**
     * Lista todas as bicicletas cadastradas no sistema
     *
     * @return List<Bicicleta> lista de todas as bicicletas
     */
	@GetMapping
	public List<Bicicleta> listarTodas() {
		return bicicletaService.listarTodas();
	}

	/**
     * Busca uma bicicleta específica por seu ID
     *
     * @param id identificador único da bicicleta
     * @return ResponseEntity<Bicicleta> bicicleta encontrada ou notFound
     */
	@GetMapping("/{id}")
	public ResponseEntity<Bicicleta> buscarPorId(@PathVariable Long id) {
		return bicicletaService.buscarPorId(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	/**
     * Cadastra uma nova bicicleta no sistema
     *
     * @param bicicleta dados da nova bicicleta a ser cadastrada
     * @return Bicicleta bicicleta cadastrada com ID gerado
     */
	@PostMapping
	public Bicicleta criar(@RequestBody Bicicleta bicicleta) {
		return bicicletaService.salvar(bicicleta);
	}

	/**
     * Atualiza os dados de uma bicicleta existente
     * Requer que a bicicleta exista no sistema
     *
     * @param id ID da bicicleta a ser atualizada
     * @param bicicleta novos dados da bicicleta
     * @return ResponseEntity<Bicicleta> bicicleta atualizada ou notFound
     */
	@PutMapping("/{id}")
	public ResponseEntity<Bicicleta> atualizar(@PathVariable Long id, @RequestBody Bicicleta bicicleta) {
		if (!bicicletaService.buscarPorId(id).isPresent()) {
			return ResponseEntity.notFound().build();
		}
		bicicleta.setId(id);
		return ResponseEntity.ok(bicicletaService.salvar(bicicleta));
	}

	/**
     * Remove uma bicicleta do sistema
     * A bicicleta não deve estar em uso ou associada a uma tranca
     *
     * @param id ID da bicicleta a ser removida
     * @return ResponseEntity<Void> sucesso ou notFound
     */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletar(@PathVariable Long id) {
		if (!bicicletaService.buscarPorId(id).isPresent()) {
			return ResponseEntity.notFound().build();
		}
		bicicletaService.deletar(id);
		return ResponseEntity.noContent().build();
	}

	/**
     * Busca bicicletas por seu status atual
     *
     * @param status status desejado das bicicletas
     * @return List<Bicicleta> lista de bicicletas com o status especificado
     */
	@GetMapping("/status/{status}")
	public List<Bicicleta> buscarPorStatus(@PathVariable StatusBicicleta status) {
		return bicicletaService.buscarPorStatus(status);
	}

	/**
     * Atualiza o status de uma bicicleta
     *
     * @param id ID da bicicleta
     * @param novoStatus novo status a ser definido
     * @return ResponseEntity<Void> sucesso ou notFound
     */
	@PatchMapping("/{id}/status")
	public ResponseEntity<Void> atualizarStatus(@PathVariable Long id, @RequestBody StatusBicicleta novoStatus) {
		boolean atualizado = bicicletaService.atualizarStatus(id, novoStatus);
		return atualizado ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
	}
}