package com.example.demo.service;

import org.springframework.stereotype.Service;
import com.example.demo.model.Emprestimo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;

@Service
public class NotificacaoService {
    private static final Logger logger = LoggerFactory.getLogger(NotificacaoService.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

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
            """,
            emprestimo.getCiclista().getNome(),
            emprestimo.getCiclista().getEmail(),
            emprestimo.getHoraInicio().format(formatter),
            emprestimo.getBicicleta().getId(),
            emprestimo.getTrancaInicio().getId(),
            emprestimo.getTaxaInicial()
        );
        
        enviarNotificacao(emprestimo.getCiclista().getEmail(), mensagem);
    }

    public void notificarDevolucaoRealizada(Emprestimo emprestimo) {
        double valorTotal = emprestimo.getTaxaInicial() + (emprestimo.getTaxaExtra() != null ? emprestimo.getTaxaExtra() : 0.0);
        
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
            """,
            emprestimo.getCiclista().getNome(),
            emprestimo.getCiclista().getEmail(),
            emprestimo.getHoraInicio().format(formatter),
            emprestimo.getHoraFim().format(formatter),
            emprestimo.getBicicleta().getId(),
            emprestimo.getTrancaFim().getId(),
            emprestimo.getTaxaInicial(),
            emprestimo.getTaxaExtra() != null ? emprestimo.getTaxaExtra() : 0.0,
            valorTotal
        );
        
        enviarNotificacao(emprestimo.getCiclista().getEmail(), mensagem);
    }

    private void enviarNotificacao(String email, String mensagem) {
        // Log da notificação para demonstração
        logger.info("\nEnviando notificação para: {}\n{}", email, mensagem);
        
        // Se estiver usando interface gráfica, pode mostrar um popup
        // Exemplo de como chamar no SwingUtilities.invokeLater se necessário
    }
}