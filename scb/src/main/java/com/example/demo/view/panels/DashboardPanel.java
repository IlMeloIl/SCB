package com.example.demo.view.panels;

import javax.swing.*;
import java.awt.*;
import com.example.demo.view.WindowManager;
import com.example.demo.view.utils.ColorScheme;
import com.example.demo.controller.CiclistaController;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DashboardPanel extends JPanel {
    private WindowManager windowManager;
    private CiclistaController ciclistaController;
    
    // Painéis de conteúdo
    private JPanel menuPanel;
    private JPanel contentPanel;
    private CardLayout contentCardLayout;
    
    // Labels de informação
    private JLabel welcomeLabel;
    private JLabel statusLabel;
    
    public DashboardPanel(WindowManager windowManager, CiclistaController ciclistaController) {
        this.windowManager = windowManager;
        this.ciclistaController = ciclistaController;
        setupUI();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(ColorScheme.BACKGROUND);
        
        // Painel superior
        setupTopPanel();
        
        // Painel de menu lateral
        setupMenuPanel();
        
        // Painel de conteúdo principal
        setupContentPanel();
    }
    
    private void setupTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(ColorScheme.PRIMARY);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Título do sistema
        JLabel titleLabel = new JLabel("Sistema de Controle de Bicicletário");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        
        // Painel de informações do usuário
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        
        welcomeLabel = new JLabel("Bem-vindo(a), Usuário");
        welcomeLabel.setForeground(Color.WHITE);
        
        JButton logoutButton = new JButton("Sair");
        logoutButton.setBackground(ColorScheme.BACKGROUND);
        logoutButton.setForeground(ColorScheme.PRIMARY);
        logoutButton.addActionListener(e -> handleLogout());
        
        userPanel.add(welcomeLabel);
        userPanel.add(logoutButton);
        
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(userPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
    }
    
    private void setupMenuPanel() {
        menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(ColorScheme.BACKGROUND_LIGHT);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        menuPanel.setPreferredSize(new Dimension(200, getHeight()));
        
        addMenuButton("Início", e -> showHome());
        addMenuButton("Totens", e -> showTotems());
        addMenuButton("Novo Empréstimo", e -> showEmprestimo());
        addMenuButton("Devolver Bicicleta", e -> showDevolucao());
        addMenuButton("Meu Perfil", e -> showProfile());
        
        // Status do empréstimo atual
        statusLabel = new JLabel("Nenhuma bicicleta em uso");
        statusLabel.setForeground(ColorScheme.TEXT);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        menuPanel.add(statusLabel);
        
        add(menuPanel, BorderLayout.WEST);
    }
    
    private void setupContentPanel() {
        contentPanel = new JPanel();
        contentCardLayout = new CardLayout();
        contentPanel.setLayout(contentCardLayout);
        contentPanel.setBackground(ColorScheme.BACKGROUND);
        
        // Adiciona um painel inicial de boas-vindas
        JPanel welcomePanel = new JPanel(new GridBagLayout());
        welcomePanel.setBackground(ColorScheme.BACKGROUND);
        
        JLabel welcomeText = new JLabel("Bem-vindo ao Sistema de Controle de Bicicletário");
        welcomeText.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeText.setForeground(ColorScheme.PRIMARY);
        
        welcomePanel.add(welcomeText);
        contentPanel.add(welcomePanel, "HOME");
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void addMenuButton(String text, java.awt.event.ActionListener listener) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 30));
        button.setBackground(ColorScheme.BACKGROUND);
        button.setForeground(ColorScheme.TEXT);
        button.setFocusPainted(false);
        button.addActionListener(listener);
        
        menuPanel.add(button);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    }
    
    // Métodos de navegação
    private void showHome() {
        contentCardLayout.show(contentPanel, "HOME");
    }
    
    private void showTotems() {
        // TODO: Implementar navegação para TotemListPanel
    }
    
    private void showEmprestimo() {
        // TODO: Implementar navegação para EmprestimoPanel
    }
    
    private void showDevolucao() {
        // TODO: Implementar navegação para DevolucaoPanel
    }
    
    private void showProfile() {
        // TODO: Implementar navegação para perfil do usuário
    }
    
    private void handleLogout() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Deseja realmente sair do sistema?",
            "Confirmação",
            JOptionPane.YES_NO_OPTION
        );
        
        if (option == JOptionPane.YES_OPTION) {
            windowManager.showLogin();
        }
    }
    
    // Métodos públicos para atualização de estado
    public void setUserName(String name) {
        welcomeLabel.setText("Bem-vindo(a), " + name);
    }
    
    public void updateEmprestimoStatus(boolean hasEmprestimo, String bikeInfo) {
        if (hasEmprestimo) {
            statusLabel.setText("Em uso: " + bikeInfo);
            statusLabel.setForeground(ColorScheme.WARNING);
        } else {
            statusLabel.setText("Nenhuma bicicleta em uso");
            statusLabel.setForeground(ColorScheme.TEXT);
        }
    }
}