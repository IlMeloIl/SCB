package com.example.demo.view.frames;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

	public MainFrame() {
		setTitle("Sistema de Controle de Bicicletário");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		setMinimumSize(new Dimension(800, 600));
		setLocationRelativeTo(null);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}