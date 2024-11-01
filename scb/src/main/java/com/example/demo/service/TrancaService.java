/**
 * Serviço responsável pelo gerenciamento das trancas do sistema
 * Controla todas as operações relacionadas às trancas, incluindo:
 * - Associação com bicicletas
 * - Integração com totens
 * - Controle de status
 * - Validações de estado
 * 
 * @Service Marca como um componente de serviço do Spring
 */
package com.example.demo.service;

import com.example.demo.model.Tranca;
import com.example.demo.model.Bicicleta;
import com.example.demo.model.StatusBicicleta;
import com.example.demo.model.StatusTranca;
import com.example.demo.model.Totem;
import com.example.demo.repository.BicicletaRepository;
import com.example.demo.repository.TotemRepository;
import com.example.demo.repository.TrancaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TrancaService {

	@Autowired
	private TrancaRepository trancaRepository;

	@Autowired
	private TotemRepository totemRepository;

	@Autowired
	private BicicletaRepository bicicletaRepository;

	/**
     * Lista todas as trancas cadastradas no sistema
     *
     * @return List<Tranca> lista de todas as trancas
     */
	public List<Tranca> listarTodas() {
		List<Tranca> trancas = trancaRepository.findAll();
		System.out.println("Número de trancas encontradas: " + trancas.size());
		return trancas;
	}

	/**
     * Busca uma tranca específica por seu ID
     * Inclui informações sobre bicicleta associada, se houver
     *
     * @param id identificador único da tranca
     * @return Optional<Tranca> tranca encontrada ou empty
     */
	public Optional<Tranca> buscarPorId(Long id) {
		Optional<Tranca> trancaOpt = trancaRepository.findById(id);
		trancaOpt.ifPresent(tranca -> {
			System.out.println("Tranca encontrada: " + tranca);
			if (tranca.getBicicleta() != null) {
				System.out.println("Bicicleta associada: " + tranca.getBicicleta());
			} else {
				System.out.println("Nenhuma bicicleta associada a esta tranca");
			}
		});
		return trancaOpt;
	}

	/**
     * Busca uma tranca pelo seu número único
     *
     * @param numero número identificador da tranca
     * @return Optional<Tranca> tranca encontrada ou empty
     */
	public Optional<Tranca> buscarPorNumero(String numero) {
		return trancaRepository.findByNumero(numero);
	}

	/**
     * Salva uma nova tranca ou atualiza uma existente
     * Realiza validações de unicidade e associações
     *
     * @param tranca tranca a ser salva/atualizada
     * @return Tranca tranca salva com ID gerado
     * @throws IllegalArgumentException se houver violação de unicidade
     */
	@Transactional
	public Tranca salvar(Tranca tranca) {
		System.out.println("Salvando tranca: " + tranca);

		// Verificação prévia
		if (trancaRepository.findByNumero(tranca.getNumero()).isPresent()) {
			throw new IllegalArgumentException("Já existe uma Tranca com o número: " + tranca.getNumero());
		}

		if (tranca.getTotem() != null && tranca.getTotem().getId() != null) {
			Totem totem = totemRepository.findById(tranca.getTotem().getId())
					.orElseThrow(() -> new RuntimeException("Totem não encontrado"));
			tranca.setTotem(totem);
			System.out.println("Totem associado: " + totem);
		}

		try {
			Tranca trancaSalva = trancaRepository.save(tranca);
			System.out.println("Tranca salva: " + trancaSalva);
			return trancaSalva;
		} catch (DataIntegrityViolationException e) {
			// Captura violações de unicidade que possam ter passado pela verificação prévia
			throw new IllegalArgumentException(
					"Erro ao salvar: Já existe uma Tranca com o número " + tranca.getNumero());
		}
	}

	/**
     * Remove uma tranca do sistema
     * Realiza desassociação segura do totem
     *
     * @param id ID da tranca a ser removida
     * @throws RuntimeException se a tranca não for encontrada
     */
	@Transactional
	public void deletar(Long id) {
		Tranca tranca = trancaRepository.findById(id).orElseThrow(() -> new RuntimeException("Tranca não encontrada"));

		if (tranca.getTotem() != null) {
			Totem totem = tranca.getTotem();
			totem.getTrancas().remove(tranca);
			totemRepository.save(totem);
		}

		trancaRepository.delete(tranca);
	}

	/**
     * Lista trancas por seu status atual
     *
     * @param status status desejado (LIVRE, OCUPADA, etc.)
     * @return List<Tranca> lista de trancas no status especificado
     */
	public List<Tranca> buscarPorStatus(StatusTranca status) {
		return trancaRepository.findByStatus(status);
	}

	/**
     * Lista trancas de um totem específico
     *
     * @param totemId ID do totem
     * @return List<Tranca> lista de trancas do totem
     */
	public List<Tranca> buscarPorTotem(Long totemId) {
		return trancaRepository.findByTotemId(totemId);
	}

	/**
     * Atualiza o status de uma tranca
     * Usado durante empréstimos, devoluções e manutenções
     *
     * @param id ID da tranca
     * @param novoStatus novo status a ser definido
     * @return boolean true se atualizado com sucesso
     */
	@Transactional
	public boolean atualizarStatus(Long id, StatusTranca novoStatus) {
		Optional<Tranca> trancaOpt = trancaRepository.findById(id);
		if (trancaOpt.isPresent()) {
			Tranca tranca = trancaOpt.get();
			tranca.setStatus(novoStatus);
			trancaRepository.save(tranca);
			return true;
		}
		return false;
	}

	/**
     * Atualiza parcialmente uma tranca
     * Permite modificar apenas alguns campos específicos
     *
     * @param id ID da tranca
     * @param updates mapa com campos a serem atualizados
     * @return Tranca tranca atualizada
     * @throws RuntimeException se a tranca não for encontrada
     */
	@Transactional
	public Tranca atualizarParcial(Long id, Map<String, Object> updates) {
		Tranca tranca = trancaRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Tranca não encontrada com ID: " + id));

		if (updates.containsKey("numero")) {
			tranca.setNumero((String) updates.get("numero"));
		}
		if (updates.containsKey("status")) {
			tranca.setStatus(StatusTranca.valueOf((String) updates.get("status")));
		}
		if (updates.containsKey("totemId")) {
			Long totemId = Long.valueOf(updates.get("totemId").toString());
			Totem totem = totemRepository.findById(totemId)
					.orElseThrow(() -> new RuntimeException("Totem não encontrado com ID: " + totemId));
			tranca.setTotem(totem);
		}

		return trancaRepository.save(tranca);
	}

	/**
     * Associa uma bicicleta a uma tranca
     * Atualiza o status tanto da tranca quanto da bicicleta
     *
     * @param trancaId ID da tranca
     * @param bicicletaId ID da bicicleta
     * @return Tranca tranca atualizada com a bicicleta associada
     * @throws RuntimeException se tranca ou bicicleta não forem encontradas
     */
	@Transactional
	public Tranca associarBicicleta(Long trancaId, Long bicicletaId) {
		Tranca tranca = trancaRepository.findById(trancaId)
				.orElseThrow(() -> new RuntimeException("Tranca não encontrada com ID: " + trancaId));

		Bicicleta bicicleta = bicicletaRepository.findById(bicicletaId)
				.orElseThrow(() -> new RuntimeException("Bicicleta não encontrada com ID: " + bicicletaId));

		tranca.setBicicleta(bicicleta);
		tranca.setStatus(StatusTranca.OCUPADA);
		bicicleta.setStatus(StatusBicicleta.DISPONIVEL);

		System.out.println("Associando bicicleta " + bicicletaId + " à tranca " + trancaId);

		Tranca trancaAtualizada = trancaRepository.save(tranca);
		bicicletaRepository.save(bicicleta);

		System.out.println("Tranca após associação: " + trancaAtualizada);
		System.out.println("Bicicleta associada: " + trancaAtualizada.getBicicleta());

		return trancaAtualizada;
	}

}