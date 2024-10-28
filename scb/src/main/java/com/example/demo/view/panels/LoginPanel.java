package com.example.demo.view.panels;

import javax.swing.*;
import java.awt.*;
import com.example.demo.view.WindowManager;
import com.example.demo.view.utils.ColorScheme;

import lombok.NoArgsConstructor;

import com.example.demo.controller.CiclistaController;
import com.example.demo.dto.LoginDTO;
import com.example.demo.dto.LoginResponseDTO;

import org.springframework.http.ResponseEntity;

@NoArgsConstructor
public class LoginPanel extends JPanel {
    private WindowManager windowManager;
    private CiclistaController ciclistaController;
    private JTextField emailField;
    private JPasswordField senhaField;

    public LoginPanel(WindowManager windowManager, CiclistaController ciclistaController) {
        this.windowManager = windowManager;
        this.ciclistaController = ciclistaController;
        setupUI();
    }

    private void setupUI() {
        setLayout(new GridBagLayout());
        setBackground(ColorScheme.BACKGROUND);
        
        // Painel central com borda
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.PRIMARY, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        centerPanel.setBackground(ColorScheme.BACKGROUND);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Título
        JLabel titleLabel = new JLabel("Sistema de Controle de Bicicletário");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(ColorScheme.PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 5, 20, 5);
        centerPanel.add(titleLabel, gbc);

        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        centerPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        centerPanel.add(emailField, gbc);

        // Senha
        JLabel senhaLabel = new JLabel("Senha:");
        senhaField = new JPasswordField(20);
        gbc.gridx = 0;
        gbc.gridy = 2;
        centerPanel.add(senhaLabel, gbc);
        gbc.gridx = 1;
        centerPanel.add(senhaField, gbc);

        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(ColorScheme.BACKGROUND);

        JButton loginButton = new JButton("Entrar");
        loginButton.setBackground(ColorScheme.PRIMARY);
        loginButton.setForeground(Color.WHITE);
        loginButton.addActionListener(e -> handleLogin());

        JButton cadastroButton = new JButton("Criar Conta");
        cadastroButton.setBackground(ColorScheme.SECONDARY);
        cadastroButton.setForeground(Color.WHITE);
        cadastroButton.addActionListener(e -> windowManager.showCadastro());

        buttonPanel.add(loginButton);
        buttonPanel.add(cadastroButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        centerPanel.add(buttonPanel, gbc);

        add(centerPanel);
    }

    private void handleLogin() {
        String email = emailField.getText();
        String senha = new String(senhaField.getPassword());

        if (email.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor, preencha todos os campos",
                "Erro",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            LoginDTO loginDTO = new LoginDTO();
            loginDTO.setEmail(email);
            loginDTO.setSenha(senha);
            
            ResponseEntity<LoginResponseDTO> response = ciclistaController.login(loginDTO);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                LoginResponseDTO loginResponse = response.getBody();
                limparCampos();
                
                // Atualiza o estado do WindowManager e navega para o dashboard
                windowManager.handleLoginSuccess(
                    loginResponse.getEmail(),
                    loginResponse.getNome(),
                    loginResponse.isHasActiveEmprestimo(),
                    loginResponse.getCurrentBikeInfo()
                );
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Email ou senha inválidos",
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        emailField.setText("");
        senhaField.setText("");
    }
}