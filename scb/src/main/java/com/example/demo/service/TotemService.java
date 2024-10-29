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

	public List<Totem> listarTodos() {
		return totemRepository.findAll();
	}

	@Transactional
	public Totem buscarPorId(Long id) {
		return totemRepository.findById(id).orElseThrow(() -> new RuntimeException("Totem não encontrado"));
	}

	@Transactional
	public Totem salvar(Totem totem) {
		return totemRepository.save(totem);
	}

	@Transactional
	public void deletar(Long id) {
		totemRepository.deleteById(id);
	}

	public List<Totem> buscarPorLocalizacao(String localizacao) {
		return totemRepository.findByLocalizacao(localizacao);
	}

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

	private void atualizarTrancas(Totem totemExistente, List<Tranca> trancasAtualizadas) {
		if (trancasAtualizadas == null) {
			return;
		}

		// Remover trancas que não estão mais na lista
		totemExistente.getTrancas().removeIf(tranca -> trancasAtualizadas.stream()
				.noneMatch(t -> t.getId() != null && t.getId().equals(tranca.getId())));

		for (Tranca trancaAtualizada : trancasAtualizadas) {
			if (trancaAtualizada.getId() != null) {
				// Tentar encontrar a tranca existente
				Optional<Tranca> trancaExistenteOpt = totemExistente.getTrancas().stream()
						.filter(t -> t.getId().equals(trancaAtualizada.getId())).findFirst();

				if (trancaExistenteOpt.isPresent()) {
					// Atualizar tranca existente
					Tranca trancaExistente = trancaExistenteOpt.get();
					trancaExistente.setNumero(trancaAtualizada.getNumero());
					trancaExistente.setStatus(trancaAtualizada.getStatus());
				} else {
					// Tranca não encontrada, criar uma nova
					Tranca novaTranca = new Tranca();
					novaTranca.setNumero(trancaAtualizada.getNumero());
					novaTranca.setStatus(trancaAtualizada.getStatus());
					totemExistente.addTranca(novaTranca);
				}
			} else {
				// Adicionar nova tranca
				Tranca novaTranca = new Tranca();
				novaTranca.setNumero(trancaAtualizada.getNumero());
				novaTranca.setStatus(trancaAtualizada.getStatus());
				totemExistente.addTranca(novaTranca);
			}
		}
	}

	@Transactional
	public Totem atualizarParcial(Long id, Totem totemParcial) {
		System.out.println("Iniciando atualização parcial para Totem ID: " + id);
		Totem totemExistente = totemRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Totem não encontrado com ID: " + id));

		System.out.println("Totem encontrado. Atualizando campos...");

		// Manter o ID original
		totemParcial.setId(id);

		// Atualizar campos simples
		if (totemParcial.getLocalizacao() != null) {
			totemExistente.setLocalizacao(totemParcial.getLocalizacao());
		}
		if (totemParcial.getDescricao() != null) {
			totemExistente.setDescricao(totemParcial.getDescricao());
		}
		if (totemParcial.getCapacidadeMaxima() != null) {
			totemExistente.setCapacidadeMaxima(totemParcial.getCapacidadeMaxima());
		}

		// Atualizar trancas
		if (totemParcial.getTrancas() != null) {
			atualizarTrancas(totemExistente, totemParcial.getTrancas());
		}

		Totem totemAtualizado = totemRepository.save(totemExistente);
		System.out.println("Totem atualizado. ID: " + totemAtualizado.getId());
		return totemAtualizado;
	}

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