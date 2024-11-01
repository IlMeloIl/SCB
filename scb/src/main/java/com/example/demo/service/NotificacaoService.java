/**
 * Serviço responsável pelo envio de notificações do sistema
 * Gerencia todas as comunicações com os ciclistas sobre eventos
 * de empréstimo, devolução e cobranças.
 * 
 * @Service Marca como um componente de serviço do Spring
 */
package com.example.demo.service;

import org.springframework.stereotype.Service;
import com.example.demo.model.Emprestimo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;

@Service
public class NotificacaoService {
	
	/**
     * Logger para registro de notificações enviadas
     */
	private static final Logger logger = LoggerFactory.getLogger(NotificacaoService.class);
	
	/**
     * Formatador de data/hora para mensagens
     * Formato: dd/MM/yyyy HH:mm:ss
     */
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	/**
     * Envia notificação sobre empréstimo realizado
     * Inclui informações sobre:
     * - Dados do ciclista
     * - Data e hora
     * - Bicicleta emprestada
     * - Local de retirada
     * - Valor cobrado
     *
     * @param emprestimo empréstimo realizado
     */
	public void notificarEmprestimoRealizado(Emprestimo emprestimo) {
		String mensagem = String.format("""
				========= EMPRÉSTIMO REALIZADO =========
				Ciclista: %s
				Email: %s
				Data/Hora: %s
				Bicicleta: %d
				Tranca: %d
				Valor cobrado: R$ %.2f
				=======================================
				""", emprestimo.getCiclista().getNome(), emprestimo.getCiclista().getEmail(),
				emprestimo.getHoraInicio().format(formatter), emprestimo.getBicicleta().getId(),
				emprestimo.getTrancaInicio().getId(), emprestimo.getTaxaInicial());

		enviarNotificacao(emprestimo.getCiclista().getEmail(), mensagem);
	}

	/**
     * Envia notificação sobre devolução realizada
     * Inclui informações sobre:
     * - Dados do ciclista
     * - Data/hora início e fim
     * - Local de devolução
     * - Valores cobrados (inicial e extra)
     * - Valor total
     *
     * @param emprestimo empréstimo finalizado
     */
	public void notificarDevolucaoRealizada(Emprestimo emprestimo) {
		double valorTotal = emprestimo.getTaxaInicial()
				+ (emprestimo.getTaxaExtra() != null ? emprestimo.getTaxaExtra() : 0.0);

		String mensagem = String.format("""
				========= DEVOLUÇÃO REALIZADA =========
				Ciclista: %s
				Email: %s
				Data/Hora Início: %s
				Data/Hora Fim: %s
				Bicicleta: %d
				Tranca: %d
				Taxa inicial: R$ %.2f
				Taxa extra: R$ %.2f
				Valor total: R$ %.2f
				======================================
				""", emprestimo.getCiclista().getNome(), emprestimo.getCiclista().getEmail(),
				emprestimo.getHoraInicio().format(formatter), emprestimo.getHoraFim().format(formatter),
				emprestimo.getBicicleta().getId(), emprestimo.getTrancaFim().getId(), emprestimo.getTaxaInicial(),
				emprestimo.getTaxaExtra() != null ? emprestimo.getTaxaExtra() : 0.0, valorTotal);

		enviarNotificacao(emprestimo.getCiclista().getEmail(), mensagem);
	}

	/**
     * "Envia" uma notificação para o email especificado, apenas loga a mensagem.
     *
     * @param email endereço de email do destinatário
     * @param mensagem conteúdo da notificação
     */
	private void enviarNotificacao(String email, String mensagem) {
		// Log da notificação para demonstração
		logger.info("\nEnviando notificação para: {}\n{}", email, mensagem);
	}
}