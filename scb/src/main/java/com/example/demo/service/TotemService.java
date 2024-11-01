/**
 * Serviço responsável pelo gerenciamento dos totens do sistema
 * Controla os pontos físicos onde as bicicletas são disponibilizadas,
 * incluindo suas trancas e capacidade.
 * 
 * @Service Marca como um componente de serviço do Spring
 */
package com.example.demo.service;

import com.example.demo.model.Totem;
import com.example.demo.model.Tranca;
import com.example.demo.repository.TotemRepository;
import com.example.demo.repository.TrancaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TotemService {

	@Autowired
	private TotemRepository totemRepository;

	@Autowired
	private TrancaRepository trancaRepository;

	/**
	 * Lista todos os totens cadastrados no sistema
	 *
	 * @return List<Totem> lista de todos os totens
	 */
	public List<Totem> listarTodos() {
		return totemRepository.findAll();
	}

	/**
	 * Busca um totem específico por seu ID
	 *
	 * @param id identificador único do totem
	 * @return Totem totem encontrado
	 * @throws RuntimeException se o totem não for encontrado
	 */
	@Transactional
	public Totem buscarPorId(Long id) {
		return totemRepository.findById(id).orElseThrow(() -> new RuntimeException("Totem não encontrado"));
	}

	/**
	 * Salva um novo totem ou atualiza um existente
	 *
	 * @param totem totem a ser salvo/atualizado
	 * @return Totem totem salvo com ID gerado
	 */
	@Transactional
	public Totem salvar(Totem totem) {
		return totemRepository.save(totem);
	}

	/**
	 * Remove um totem do sistema O totem não deve ter trancas associadas
	 *
	 * @param id ID do totem a ser removido
	 * @throws RuntimeException se o totem tiver trancas
	 */
	@Transactional
	public void deletar(Long id) {
		totemRepository.deleteById(id);
	}

	/**
	 * Busca totens por localização
	 *
	 * @param localizacao localização a ser buscada
	 * @return List<Totem> totens encontrados na localização
	 */
	public List<Totem> buscarPorLocalizacao(String localizacao) {
		return totemRepository.findByLocalizacao(localizacao);
	}

	/**
	 * Adiciona uma nova tranca ao totem Valida a capacidade máxima antes da adição
	 *
	 * @param totemId ID do totem
	 * @param tranca  tranca a ser adicionada
	 * @return Totem totem atualizado com nova tranca
	 * @throws IllegalStateException se capacidade máxima for excedida
	 */
	@Transactional
	public Totem adicionarTranca(Long totemId, Tranca tranca) {
		Totem totem = totemRepository.findById(totemId).orElseThrow(() -> new RuntimeException("Totem não encontrado"));

		if (totem.getTrancas().size() >= totem.getCapacidadeMaxima()) {
			throw new IllegalStateException("Capacidade máxima do Totem atingida");
		}

		tranca.setTotem(totem);
		tranca = trancaRepository.save(tranca);

		totem.addTranca(tranca);
		return totemRepository.save(totem);
	}

	/**
	 * Atualiza os dados de um totem existente Valida os dados antes da atualização
	 *
	 * @param id              ID do totem
	 * @param totemAtualizado novos dados do totem
	 * @return Totem totem atualizado
	 * @throws RuntimeException         se o totem não existir
	 * @throws IllegalArgumentException se dados forem inválidos
	 */
	@Transactional
	public Totem atualizar(Long id, Totem totemAtualizado) {
		Totem totemExistente = totemRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Totem não encontrado com ID: " + id));

		if (totemAtualizado.getLocalizacao() == null || totemAtualizado.getLocalizacao().trim().isEmpty()) {
			throw new IllegalArgumentException("A localização do Totem não pode ser vazia");
		}

		totemExistente.setLocalizacao(totemAtualizado.getLocalizacao());
		totemExistente.setDescricao(totemAtualizado.getDescricao());
		totemExistente.setCapacidadeMaxima(totemAtualizado.getCapacidadeMaxima());

		if (totemAtualizado.getTrancas() != null
				&& totemAtualizado.getTrancas().size() > totemExistente.getCapacidadeMaxima()) {
			throw new IllegalArgumentException("Número de trancas excede a capacidade máxima do Totem");
		}

		atualizarTrancas(totemExistente, totemAtualizado.getTrancas());

		return totemRepository.save(totemExistente);
	}

	/**
     * Atualiza as trancas de um totem
     * Gerencia adição, remoção e atualização de trancas
     *
     * @param totemExistente totem a ser atualizado
     * @param trancasAtualizadas novas trancas ou trancas atualizadas
     */
	private void atualizarTrancas(Totem totemExistente, List<Tranca> trancasAtualizadas) {
		if (trancasAtualizadas == null) {
			return;
		}

		// Remove trancas que não estão mais na lista
		totemExistente.getTrancas().removeIf(tranca -> trancasAtualizadas.stream()
				.noneMatch(t -> t.getId() != null && t.getId().equals(tranca.getId())));

		for (Tranca trancaAtualizada : trancasAtualizadas) {
			if (trancaAtualizada.getId() != null) {
				// Tenta encontrar a tranca existente
				Optional<Tranca> trancaExistenteOpt = totemExistente.getTrancas().stream()
						.filter(t -> t.getId().equals(trancaAtualizada.getId())).findFirst();

				if (trancaExistenteOpt.isPresent()) {
					// Atualiza tranca existente
					Tranca trancaExistente = trancaExistenteOpt.get();
					trancaExistente.setNumero(trancaAtualizada.getNumero());
					trancaExistente.setStatus(trancaAtualizada.getStatus());
				} else {
					// Tranca não encontrada, cria uma nova
					Tranca novaTranca = new Tranca();
					novaTranca.setNumero(trancaAtualizada.getNumero());
					novaTranca.setStatus(trancaAtualizada.getStatus());
					totemExistente.addTranca(novaTranca);
				}
			} else {
				// Adiciona nova tranca
				Tranca novaTranca = new Tranca();
				novaTranca.setNumero(trancaAtualizada.getNumero());
				novaTranca.setStatus(trancaAtualizada.getStatus());
				totemExistente.addTranca(novaTranca);
			}
		}
	}

	/**
	 * Atualiza parcialmente os dados de um totem Permite atualizar apenas campos
	 * específicos
	 *
	 * @param id ID do totem
	 * @param totemParcial objeto com campos a serem atualizados
	 * @return Totem totem atualizado
	 */
	@Transactional
	public Totem atualizarParcial(Long id, Totem totemParcial) {
		System.out.println("Iniciando atualização parcial para Totem ID: " + id);
		Totem totemExistente = totemRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Totem não encontrado com ID: " + id));

		System.out.println("Totem encontrado. Atualizando campos...");

		// Manter o ID original
		totemParcial.setId(id);

		// Atualiza campos simples
		if (totemParcial.getLocalizacao() != null) {
			totemExistente.setLocalizacao(totemParcial.getLocalizacao());
		}
		if (totemParcial.getDescricao() != null) {
			totemExistente.setDescricao(totemParcial.getDescricao());
		}
		if (totemParcial.getCapacidadeMaxima() != null) {
			totemExistente.setCapacidadeMaxima(totemParcial.getCapacidadeMaxima());
		}

		// Atualiza trancas
		if (totemParcial.getTrancas() != null) {
			atualizarTrancas(totemExistente, totemParcial.getTrancas());
		}

		Totem totemAtualizado = totemRepository.save(totemExistente);
		System.out.println("Totem atualizado. ID: " + totemAtualizado.getId());
		return totemAtualizado;
	}

	/**
     * Atualiza a capacidade máxima de um totem
     * Valida se a nova capacidade comporta as trancas existentes
     *
     * @param id ID do totem
     * @param novaCapacidade nova capacidade máxima
     * @return boolean true se atualizado com sucesso
     * @throws IllegalArgumentException se a capacidade for inválida
     */
	@Transactional
	public boolean atualizarCapacidade(Long id, Integer novaCapacidade) {
		if (novaCapacidade <= 0) {
			throw new IllegalArgumentException("A capacidade do Totem deve ser um número positivo");
		}

		Optional<Totem> totemOpt = totemRepository.findById(id);
		if (totemOpt.isPresent()) {
			Totem totem = totemOpt.get();
			totem.setCapacidadeMaxima(novaCapacidade);
			totemRepository.save(totem);
			return true;
		}
		return false;
	}
}