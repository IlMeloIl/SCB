/*
 Gerenciador central do front-end
 Responsável pelo gerenciamento de navegação, estado da sessão e coordenação entre
 views e controllers
 */
package com.example.demo.view;

import javax.swing.*;
import java.awt.*;
import org.springframework.context.ApplicationContext;
import com.example.demo.controller.CiclistaController;
import com.example.demo.controller.TotemController;
import com.example.demo.controller.EmprestimoController;
import com.example.demo.view.panels.*;
import com.example.demo.view.frames.MainFrame;

public class WindowManager {
	// Constantes de navegação - Identificadores para o CardLayout
	public static final String LOGIN_PANEL = "LOGIN";
	public static final String CADASTRO_PANEL = "CADASTRO";
	public static final String DASHBOARD_PANEL = "DASHBOARD";
	public static final String TOTEM_LIST_PANEL = "TOTEM_LIST";
	public static final String EMPRESTIMO_PANEL = "EMPRESTIMO";
	public static final String DEVOLUCAO_PANEL = "DEVOLUCAO";
	public static final String PERFIL_PANEL = "PERFIL";

	// Componentes principais da interface
	private MainFrame mainFrame;
	private CardLayout cardLayout;
	private JPanel contentPanel;
	private ApplicationContext springContext;

	// Controllers
	private CiclistaController ciclistaController;
	private TotemController totemController;
	private EmprestimoController emprestimoController;

	// Dados da sessão atual
	private String currentUserEmail;
	private String currentUserName;
	private boolean hasActiveEmprestimo;
	private String currentBikeInfo;
	private String currentUserDocument;
	private Long currentEmprestimoId;

	// Painéis da interface
	private LoginPanel loginPanel;
	private CadastroPanel cadastroPanel;
	private DashboardPanel dashboardPanel;
	private TotemListPanel totemListPanel;
	private EmprestimoPanel emprestimoPanel;
	private DevolucaoPanel devolucaoPanel;
	private PerfilPanel perfilPanel;

	/**
	 * Construtor que inicializa o gerenciador com o contexto do Spring
	 * 
	 * @param springContext Contexto do Spring para injeção de dependências
	 */
	public WindowManager(ApplicationContext springContext) {
		this.springContext = springContext;
		initializeControllers();
		initializeWindow();
	}

	// Inicializa os controllers necessários através do contexto do Spring
	private void initializeControllers() {
		ciclistaController = springContext.getBean(CiclistaController.class);
		totemController = springContext.getBean(TotemController.class);
		emprestimoController = springContext.getBean(EmprestimoController.class);
	}

	// Configura a janela principal e inicializa todos os componentes da interface
	private void initializeWindow() {
		mainFrame = new MainFrame();
		cardLayout = new CardLayout();
		contentPanel = new JPanel(cardLayout);
		mainFrame.setContentPane(contentPanel);

		initializePanels();
		addPanelsToContent();

		showLogin();
		mainFrame.setVisible(true);
	}

	// Inicializa todos os painéis da aplicação com suas dependências
	private void initializePanels() {
		loginPanel = new LoginPanel(this, ciclistaController);
		cadastroPanel = new CadastroPanel(this, ciclistaController);
		dashboardPanel = new DashboardPanel(this, ciclistaController);
		totemListPanel = new TotemListPanel(this, totemController);
		emprestimoPanel = new EmprestimoPanel(this, emprestimoController, totemController, ciclistaController);
		devolucaoPanel = new DevolucaoPanel(this, emprestimoController, totemController);
		perfilPanel = new PerfilPanel(this, ciclistaController);
	}

	// Adiciona todos os painéis ao ContentPanel
	private void addPanelsToContent() {
		contentPanel.add(loginPanel, LOGIN_PANEL);
		contentPanel.add(cadastroPanel, CADASTRO_PANEL);
		contentPanel.add(dashboardPanel, DASHBOARD_PANEL);
		contentPanel.add(totemListPanel, TOTEM_LIST_PANEL);
		contentPanel.add(emprestimoPanel, EMPRESTIMO_PANEL);
		contentPanel.add(devolucaoPanel, DEVOLUCAO_PANEL);
		contentPanel.add(perfilPanel, PERFIL_PANEL);
	}

	// Navega para a tela de login e reseta os dados da sessão
	public void showLogin() {
		resetSessionData();
		cardLayout.show(contentPanel, LOGIN_PANEL);
		mainFrame.setTitle("SCB - Login");
	}

	// Navega para a tela de cadastro
	public void showCadastro() {
		cardLayout.show(contentPanel, CADASTRO_PANEL);
		mainFrame.setTitle("SCB - Cadastro");
	}

	// Navega para o dashboard após validar a sessão do usuário
	public void showDashboard() {
		if (currentUserDocument == null) {
			showError("Por favor, faça login novamente");
			showLogin();
			return;
		}
		cardLayout.show(contentPanel, DASHBOARD_PANEL);
		mainFrame.setTitle("SCB - Painel Principal");
		updateDashboardInfo(); // Garante que as informações estão atualizadas
	}

	// Navega para a lista de totens e atualiza os dados
	public void showTotemList() {
		cardLayout.show(contentPanel, TOTEM_LIST_PANEL);
		mainFrame.setTitle("SCB - Lista de Totens");
		totemListPanel.refreshTotemList();
	}

	// Navega para a tela de empréstimo após validar que não há empréstimo ativo
	public void showEmprestimo() {
		if (hasActiveEmprestimo) {
			showError("Você já possui uma bicicleta em uso.");
			return;
		}
		cardLayout.show(contentPanel, EMPRESTIMO_PANEL);
		mainFrame.setTitle("SCB - Novo Empréstimo");
	}

	// Navega para a tela de devolução após validar que há um empréstimo ativo
	public void showDevolucao() {
		if (!hasActiveEmprestimo) {
			showError("Você não possui nenhuma bicicleta em uso.");
			return;
		}
		cardLayout.show(contentPanel, DEVOLUCAO_PANEL);
		mainFrame.setTitle("SCB - Devolução");
		devolucaoPanel.carregarDados();
	}

	// Navega para a tela de perfil após validar a sessão
	public void showPerfil() {
		if (currentUserDocument == null) {
			showError("Por favor, faça login novamente");
			showLogin();
			return;
		}
		cardLayout.show(contentPanel, PERFIL_PANEL);
		mainFrame.setTitle("SCB - Meu Perfil");
		perfilPanel.carregarDados();
	}

	// Processa o login e atualiza o estado da sessão
	public void handleLoginSuccess(String userEmail, String userName, String userDocument, boolean hasEmprestimo,
			String bikeInfo) {
		this.currentUserEmail = userEmail;
		this.currentUserName = userName;
		this.currentUserDocument = userDocument;
		this.hasActiveEmprestimo = hasEmprestimo;
		this.currentBikeInfo = bikeInfo;
		showDashboard();
	}

	// Limpa todos os dados da sessão atual
	private void resetSessionData() {
		currentUserEmail = null;
		currentUserName = null;
		currentUserDocument = null;
		hasActiveEmprestimo = false;
		currentBikeInfo = null;
	}

	// Atualiza as informações exibidas no dashboard
	private void updateDashboardInfo() {
		if (currentUserEmail != null) {
			dashboardPanel.setUserName(currentUserName);
			dashboardPanel.updateEmprestimoStatus(hasActiveEmprestimo, currentBikeInfo);
		}
	}

	// Getters para acesso aos dados da sessão
	public String getCurrentUserEmail() {
		return currentUserEmail;
	}

	public String getCurrentUserName() {
		return currentUserName;
	}

	public String getCurrentUserDocument() {
		return currentUserDocument;
	}

	public boolean hasActiveEmprestimo() {
		return hasActiveEmprestimo;
	}

	public String getCurrentBikeInfo() {
		return currentBikeInfo;
	}

	// Define o ID do empréstimo atual
	public void setCurrentEmprestimoId(Long emprestimoId) {
		this.currentEmprestimoId = emprestimoId;
	}

	public Long getCurrentEmprestimoId() {
		return currentEmprestimoId;
	}

	// Exibe mensagem de erro
	public void showError(String message) {
		JOptionPane.showMessageDialog(mainFrame, message, "Erro", JOptionPane.ERROR_MESSAGE);
	}

	// Exibe mensagem de sucesso
	public void showSuccess(String message) {
		JOptionPane.showMessageDialog(mainFrame, message, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Exibe diálogo de confirmação com opções Sim/Não.
	 * 
	 * @return boolean true se o usuário confirmar, false caso contrário
	 */
	public boolean showConfirmDialog(String message, String title) {
		int result = JOptionPane.showConfirmDialog(mainFrame, message, title, JOptionPane.YES_NO_OPTION);
		return result == JOptionPane.YES_OPTION;
	}

	/**
	 * Retorna a referência para a janela principal
	 * 
	 * @return MainFrame A janela principal da aplicação
	 */
	public MainFrame getMainFrame() {
		return mainFrame;
	}

	// Atualiza as informações do usuário e reflete as mudanças na interface
	public void updateUserInfo(String email, String name) {
		this.currentUserEmail = email;
		this.currentUserName = name;
		dashboardPanel.setUserName(name);
	}

	// Atualiza o status do empréstimo e reflete as mudanças na interface
	public void updateEmprestimoStatus(boolean hasEmprestimo, String bikeInfo) {
		this.hasActiveEmprestimo = hasEmprestimo;
		this.currentBikeInfo = bikeInfo;
		dashboardPanel.updateEmprestimoStatus(hasEmprestimo, bikeInfo);
	}

}