package com.example.demo.view.panels;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import javax.swing.text.MaskFormatter;
import com.example.demo.view.WindowManager;
import com.example.demo.view.utils.ColorScheme;

import lombok.NoArgsConstructor;

import com.example.demo.controller.CiclistaController;
import com.example.demo.model.Brasileiro;
import com.example.demo.model.Estrangeiro;
import org.springframework.http.ResponseEntity;

@NoArgsConstructor
public class CadastroPanel extends JPanel {
    private WindowManager windowManager;
    private CiclistaController ciclistaController;
    
    // Campos do formulário
    private JTextField nomeField;
    private JFormattedTextField nascimentoField;
    private JTextField emailField;
    private JTextField telefoneField;
    private JPasswordField senhaField;
    private JPasswordField confirmarSenhaField;
    private JTextField documentoField;
    private JComboBox<String> tipoUsuarioCombo;
    private JLabel documentoLabel;

    public CadastroPanel(WindowManager windowManager, CiclistaController ciclistaController) {
        this.windowManager = windowManager;
        this.ciclistaController = ciclistaController;
        setupUI();
    }

    private void setupUI() {
        setLayout(new GridBagLayout());
        setBackground(ColorScheme.BACKGROUND);
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.PRIMARY, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        centerPanel.setBackground(ColorScheme.BACKGROUND);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel titleLabel = new JLabel("Cadastro de Usuário");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(ColorScheme.PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        centerPanel.add(titleLabel, gbc);

        // Tipo de Usuário
        JLabel tipoLabel = new JLabel("Tipo de Usuário:");
        tipoUsuarioCombo = new JComboBox<>(new String[]{"Brasileiro", "Estrangeiro"});
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        centerPanel.add(tipoLabel, gbc);
        gbc.gridx = 1;
        centerPanel.add(tipoUsuarioCombo, gbc);

        // Nome
        gbc.gridy = 2;
        gbc.gridx = 0;
        centerPanel.add(new JLabel("Nome:"), gbc);
        nomeField = new JTextField(20);
        gbc.gridx = 1;
        centerPanel.add(nomeField, gbc);

        // Data de Nascimento
        try {
            MaskFormatter nascimentoMask = new MaskFormatter("##/##/####");
            nascimentoField = new JFormattedTextField(nascimentoMask);
        } catch (ParseException e) {
            nascimentoField = new JFormattedTextField();
        }
        gbc.gridy = 3;
        gbc.gridx = 0;
        centerPanel.add(new JLabel("Nascimento:"), gbc);
        gbc.gridx = 1;
        centerPanel.add(nascimentoField, gbc);

        // Email
        gbc.gridy = 4;
        gbc.gridx = 0;
        centerPanel.add(new JLabel("Email:"), gbc);
        emailField = new JTextField(20);
        gbc.gridx = 1;
        centerPanel.add(emailField, gbc);

        // Telefone
        gbc.gridy = 5;
        gbc.gridx = 0;
        centerPanel.add(new JLabel("Telefone:"), gbc);
        telefoneField = new JTextField(20);
        gbc.gridx = 1;
        centerPanel.add(telefoneField, gbc);

        // Documento (CPF/Passaporte)
        documentoLabel = new JLabel("CPF:");
        documentoField = new JTextField(20);
        gbc.gridy = 6;
        gbc.gridx = 0;
        centerPanel.add(documentoLabel, gbc);
        gbc.gridx = 1;
        centerPanel.add(documentoField, gbc);

        // Senha
        gbc.gridy = 7;
        gbc.gridx = 0;
        centerPanel.add(new JLabel("Senha:"), gbc);
        senhaField = new JPasswordField(20);
        gbc.gridx = 1;
        centerPanel.add(senhaField, gbc);

        // Confirmar Senha
        gbc.gridy = 8;
        gbc.gridx = 0;
        centerPanel.add(new JLabel("Confirmar Senha:"), gbc);
        confirmarSenhaField = new JPasswordField(20);
        gbc.gridx = 1;
        centerPanel.add(confirmarSenhaField, gbc);

        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(ColorScheme.BACKGROUND);

        JButton voltarButton = new JButton("Voltar");
        voltarButton.setBackground(ColorScheme.BACKGROUND);
        voltarButton.setForeground(ColorScheme.PRIMARY);

        JButton cadastrarButton = new JButton("Cadastrar");
        cadastrarButton.setBackground(ColorScheme.PRIMARY);
        cadastrarButton.setForeground(Color.WHITE);

        buttonPanel.add(voltarButton);
        buttonPanel.add(cadastrarButton);

        gbc.gridy = 9;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        centerPanel.add(buttonPanel, gbc);

        // Adiciona o painel central
        add(centerPanel);

        // Listeners
        tipoUsuarioCombo.addActionListener(e -> updateDocumentoLabel());
        cadastrarButton.addActionListener(e -> handleCadastro());
        voltarButton.addActionListener(e -> windowManager.showLogin());
    }

    private void updateDocumentoLabel() {
        String tipoSelecionado = (String) tipoUsuarioCombo.getSelectedItem();
        documentoLabel.setText(tipoSelecionado.equals("Brasileiro") ? "CPF:" : "Passaporte:");
        revalidate();
        repaint();
    }

    private void handleCadastro() {
        // Validação básica
        if (nomeField.getText().isEmpty() || 
            nascimentoField.getText().isEmpty() ||
            emailField.getText().isEmpty() ||
            telefoneField.getText().isEmpty() ||
            documentoField.getText().isEmpty() ||
            new String(senhaField.getPassword()).isEmpty()) {
            
            JOptionPane.showMessageDialog(this,
                "Por favor, preencha todos os campos",
                "Erro",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validação das senhas
        String senha = new String(senhaField.getPassword());
        String confirmaSenha = new String(confirmarSenhaField.getPassword());
        
        if (!senha.equals(confirmaSenha)) {
            JOptionPane.showMessageDialog(this,
                "As senhas não coincidem",
                "Erro",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            ResponseEntity<?> response;
            if (tipoUsuarioCombo.getSelectedItem().equals("Brasileiro")) {
                Brasileiro brasileiro = new Brasileiro();
                brasileiro.setNome(nomeField.getText());
                brasileiro.setNascimento(nascimentoField.getText());
                brasileiro.setEmail(emailField.getText());
                brasileiro.setTelefone(telefoneField.getText());
                brasileiro.setSenha(senha);
                brasileiro.setCpf(documentoField.getText());
                
                response = ciclistaController.cadastrarBrasileiro(brasileiro);
            } else {
                Estrangeiro estrangeiro = new Estrangeiro();
                estrangeiro.setNome(nomeField.getText());
                estrangeiro.setNascimento(nascimentoField.getText());
                estrangeiro.setEmail(emailField.getText());
                estrangeiro.setTelefone(telefoneField.getText());
                estrangeiro.setSenha(senha);
                estrangeiro.setPassaporte(documentoField.getText());
                
                response = ciclistaController.cadastrarEstrangeiro(estrangeiro);
            }

            if (response.getStatusCode().is2xxSuccessful()) {
                JOptionPane.showMessageDialog(this,
                    "Cadastro realizado com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
                limparCampos();
                windowManager.showLogin();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao realizar cadastro: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        nomeField.setText("");
        nascimentoField.setText("");
        emailField.setText("");
        telefoneField.setText("");
        documentoField.setText("");
        senhaField.setText("");
        confirmarSenhaField.setText("");
        tipoUsuarioCombo.setSelectedIndex(0);
    }
}