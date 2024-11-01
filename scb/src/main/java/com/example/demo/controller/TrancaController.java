/**
 * Controller REST responsável pelo gerenciamento das trancas do sistema
 * Gerencia todas as operações relacionadas às trancas dos totens, incluindo:
 * - Cadastro e remoção de trancas
 * - Associação com bicicletas
 * - Controle de estado (livre/ocupada)
 * - Manutenção
 *
 * @RestController Indica que esta classe é um controller REST
 * @RequestMapping Define o path base "/api/trancas" para todos os endpoints
 */
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

	/**
     * Lista todas as trancas cadastradas no sistema
     *
     * @return List<Tranca> lista de todas as trancas
     */
	@GetMapping
	public List<Tranca> listarTodas() {
		return trancaService.listarTodas();
	}

	/**
     * Busca uma tranca específica por seu ID
     * Inclui informações sobre bicicleta associada, se houver
     *
     * @param id identificador único da tranca
     * @return ResponseEntity<Tranca> tranca encontrada ou notFound
     */
	@GetMapping("/{id}")
	public ResponseEntity<Tranca> buscarPorId(@PathVariable Long id) {
		return trancaService.buscarPorId(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	/**
     * Cadastra uma nova tranca no sistema
     *
     * @param tranca dados da nova tranca a ser cadastrada
     * @return Tranca tranca cadastrada com ID gerado
     */
	@PostMapping
	public Tranca criar(@RequestBody Tranca tranca) {
		return trancaService.salvar(tranca);
	}

	/**
     * Atualiza os dados de uma tranca existente
     * Permite modificar número, status e associações
     *
     * @param id ID da tranca a ser atualizada
     * @param tranca novos dados da tranca
     * @return ResponseEntity<Tranca> tranca atualizada ou notFound
     */
	@PutMapping("/{id}")
	public ResponseEntity<Tranca> atualizar(@PathVariable Long id, @RequestBody Tranca tranca) {
		if (!trancaService.buscarPorId(id).isPresent()) {
			return ResponseEntity.notFound().build();
		}
		tranca.setId(id);
		return ResponseEntity.ok(trancaService.salvar(tranca));
	}

	/**
     * Associa uma bicicleta a uma tranca
     * Atualiza o status da tranca e da bicicleta apropriadamente
     *
     * @param trancaId ID da tranca que receberá a bicicleta
     * @param bicicletaId ID da bicicleta a ser associada
     * @return ResponseEntity<Tranca> tranca atualizada com a bicicleta associada
     */
	@PutMapping("/{trancaId}/bicicletas/{bicicletaId}")
	public ResponseEntity<Tranca> associarBicicleta(@PathVariable Long trancaId, @PathVariable Long bicicletaId) {
		try {
			Tranca trancaAtualizada = trancaService.associarBicicleta(trancaId, bicicletaId);
			return ResponseEntity.ok(trancaAtualizada);
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
     * Remove uma tranca do sistema
     *
     * @param id ID da tranca a ser removida
     * @return ResponseEntity<Void> sucesso ou notFound
     */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletar(@PathVariable Long id) {
		if (!trancaService.buscarPorId(id).isPresent()) {
			return ResponseEntity.notFound().build();
		}
		trancaService.deletar(id);
		return ResponseEntity.noContent().build();
	}

	/**
     * Busca trancas por seu status atual
     *
     * @param status status desejado das trancas
     * @return List<Tranca> lista de trancas com o status especificado
     */
	@GetMapping("/status/{status}")
	public List<Tranca> buscarPorStatus(@PathVariable StatusTranca status) {
		return trancaService.buscarPorStatus(status);
	}

	/**
     * Lista todas as trancas de um totem específico
     * Inclui informações sobre ocupação e status
     *
     * @param totemId ID do totem
     * @return List<Tranca> lista de trancas do totem
     */
	@GetMapping("/totem/{totemId}")
	public List<Tranca> buscarPorTotem(@PathVariable Long totemId) {
		return trancaService.buscarPorTotem(totemId);
	}

	/**
     * Atualiza o status de uma tranca
     * Usado para marcar trancas como livres, ocupadas ou em manutenção
     *
     * @param id ID da tranca
     * @param novoStatus novo status a ser definido
     * @return ResponseEntity<Void> sucesso ou notFound
     */
	@PatchMapping("/{id}/status")
	public ResponseEntity<Void> atualizarStatus(@PathVariable Long id, @RequestBody StatusTranca novoStatus) {
		boolean atualizado = trancaService.atualizarStatus(id, novoStatus);
		return atualizado ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
	}
}