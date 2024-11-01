/**
 * Serviço responsável pelo gerenciamento de bicicletas no sistema
 * Implementa a lógica de negócio relacionada às bicicletas
 * 
 * @Service Marca como um componente de serviço do Spring
 */
package com.example.demo.service;

import com.example.demo.model.Bicicleta;
import com.example.demo.model.StatusBicicleta;
import com.example.demo.repository.BicicletaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BicicletaService {

	@Autowired
	private BicicletaRepository bicicletaRepository;

	/**
     * Lista todas as bicicletas cadastradas no sistema
     * 
     * @return List<Bicicleta> lista de todas as bicicletas
     */
	public List<Bicicleta> listarTodas() {
		return bicicletaRepository.findAll();
	}

	/**
     * Busca uma bicicleta específica por seu ID
     * 
     * @param id identificador único da bicicleta
     * @return Optional<Bicicleta> bicicleta encontrada ou empty
     */
	public Optional<Bicicleta> buscarPorId(Long id) {
		return bicicletaRepository.findById(id);
	}

	/**
     * Busca uma bicicleta pelo seu número único
     * 
     * @param numero número identificador da bicicleta
     * @return Optional<Bicicleta> bicicleta encontrada ou empty
     */
	public Optional<Bicicleta> buscarPorNumero(String numero) {
		return bicicletaRepository.findByNumero(numero);
	}

	/**
     * Salva ou atualiza uma bicicleta no sistema
     * 
     * @param bicicleta bicicleta a ser salva/atualizada
     * @return Bicicleta bicicleta salva com ID gerado
     */
	@Transactional
	public Bicicleta salvar(Bicicleta bicicleta) {
		return bicicletaRepository.save(bicicleta);
	}
	
	/**
     * Remove uma bicicleta do sistema
     * A bicicleta não deve estar em uso ou com empréstimos pendentes
     * 
     * @param id ID da bicicleta a ser removida
     */
	@Transactional
	public void deletar(Long id) {
		bicicletaRepository.deleteById(id);
	}

	/**
     * Lista bicicletas por seu status atual
     * 
     * @param status status desejado (DISPONIVEL, EM_USO, etc.)
     * @return List<Bicicleta> lista de bicicletas no status especificado
     */
	public List<Bicicleta> buscarPorStatus(StatusBicicleta status) {
		return bicicletaRepository.findByStatus(status);
	}

	/**
     * Atualiza o status de uma bicicleta
     * Usado durante empréstimos, devoluções e manutenções
     * 
     * @param id ID da bicicleta
     * @param novoStatus novo status a ser definido
     * @return boolean true se atualizado com sucesso, false se bicicleta não encontrada
     */
	@Transactional
	public boolean atualizarStatus(Long id, StatusBicicleta novoStatus) {
		Optional<Bicicleta> bicicletaOpt = bicicletaRepository.findById(id);
		if (bicicletaOpt.isPresent()) {
			Bicicleta bicicleta = bicicletaOpt.get();
			bicicleta.setStatus(novoStatus);
			bicicletaRepository.save(bicicleta);
			return true;
		}
		return false;
	}
}