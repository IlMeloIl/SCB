package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.example.demo.view.WindowManager;

import javax.swing.*;

@SpringBootApplication
public class ScbApplication {

	public static void main(String[] args) {
		
		// Configura a aplicação para suportar interface gráfica
		System.setProperty("java.awt.headless", "false");
		SpringApplication application = new SpringApplication(ScbApplication.class);
		application.setHeadless(false);
		
		// Inicializa o contexto Spring
		final ApplicationContext springContext = application.run(args);

		// Inicializa a interface gráfica na Event Dispatch Thread
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				// Passa o contexto para o WindowManager
				new WindowManager(springContext);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}