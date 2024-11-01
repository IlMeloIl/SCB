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

	/**
     * Construtor do painel dashboard
     * 
     * @param windowManager gerenciador de janelas do sistema
     * @param ciclistaController controlador de operações do ciclista
     */
	public DashboardPanel(WindowManager windowManager, CiclistaController ciclistaController) {
		this.windowManager = windowManager;
		this.ciclistaController = ciclistaController;
		setupUI();
	}

	/**
     * Configura a interface gráfica principal do dashboard
     * Inicializa todos os componentes e define o layout base
     */
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

	/**
     * Configura o painel superior com informações do usuário e título
     * Inclui botão de logout e nome do usuário
     */
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

	/**
     * Configura o menu lateral com as opções de navegação
     * Inclui status de empréstimo atual e botões de acesso às funcionalidades
     */
	private void setupMenuPanel() {
		menuPanel = new JPanel();
		menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
		menuPanel.setBackground(ColorScheme.BACKGROUND_LIGHT);
		menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		menuPanel.setPreferredSize(new Dimension(200, getHeight()));

		addMenuButton("Início", e -> showHome());
		addMenuButton("Totens", e -> windowManager.showTotemList()); // Aqui está a chamada correta
		addMenuButton("Novo Empréstimo", e -> windowManager.showEmprestimo());
		addMenuButton("Devolver Bicicleta", e -> windowManager.showDevolucao());
		addMenuButton("Meu Perfil", e -> {
			if (windowManager.getCurrentUserDocument() != null) {
				windowManager.showPerfil();
			} else {
				windowManager.showError("Por favor, faça login novamente");
				windowManager.showLogin();
			}
		});

		// Status do empréstimo atual
		statusLabel = new JLabel("Nenhuma bicicleta em uso");
		statusLabel.setForeground(ColorScheme.TEXT);
		statusLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
		menuPanel.add(statusLabel);

		add(menuPanel, BorderLayout.WEST);
	}

	/**
     * Configura o painel de conteúdo principal
     * Utiliza CardLayout para gerenciar diferentes visões
     */
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

	/**
     * Adiciona um botão ao menu lateral
     * 
     * @param text texto do botão
     * @param listener ação a ser executada ao clicar
     */
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

	/**
     * Exibe o painel inicial do dashboard
     */
	private void showHome() {
		contentCardLayout.show(contentPanel, "HOME");
	}

	/**
     * Exibe o perfil do usuário
     */
	private void showProfile() {
		// TODO: Implementar tela de perfil
	}

	/**
     * Processa o logout do usuário
     * Solicita confirmação antes de encerrar a sessão
     */
	private void handleLogout() {
		if (windowManager.showConfirmDialog("Deseja realmente sair do sistema?", "Confirmação de Saída")) {
			windowManager.showLogin();
		}
	}

	/**
     * Atualiza o nome do usuário exibido no dashboard
     * 
     * @param name nome do usuário a ser exibido
     */
	public void setUserName(String name) {
		welcomeLabel.setText("Bem-vindo(a), " + name);
		revalidate();
		repaint();
	}

	/**
     * Atualiza o status de empréstimo no dashboard
     * 
     * @param hasEmprestimo indica se há empréstimo ativo
     * @param bikeInfo informações da bicicleta em uso
     */
	public void updateEmprestimoStatus(boolean hasEmprestimo, String bikeInfo) {
		if (hasEmprestimo) {
			statusLabel.setText("Em uso: " + bikeInfo);
			statusLabel.setForeground(ColorScheme.WARNING);
		} else {
			statusLabel.setText("Nenhuma bicicleta em uso");
			statusLabel.setForeground(ColorScheme.TEXT);
		}
		revalidate();
		repaint();
	}
}