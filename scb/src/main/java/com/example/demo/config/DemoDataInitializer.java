/**
 * Inicializador de dados de demonstração
 * Esta classe é responsável por popular o banco de dados com dados iniciais para teste
 * e demonstração do sistema
 *
 * @Component Marca esta classe como um componente gerenciado pelo Spring
 */
package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.example.demo.model.*;
import com.example.demo.service.*;

@Component
public class DemoDataInitializer implements CommandLineRunner {

	@Autowired
	private TotemService totemService;

	@Autowired
	private TrancaService trancaService;

	@Autowired
	private BicicletaService bicicletaService;

	/**
	 * Método executado automaticamente durante a inicialização Verifica se já
	 * existem dados no sistema antes de criar novos.
	 * 
	 * @param args argumentos da linha de comando (não utilizados)
	 */
	@Override
	public void run(String... args) {
		if (totemService.listarTodos().isEmpty()) {
			initializeDemoData();
		}
	}

	/**
	 * Inicializa o sistema com dados de demonstração. Cria totens em diferentes
	 * localizações, cada um com suas trancas e bicicletas associadas.
	 */
	private void initializeDemoData() {
		// Arrays com dados de demonstração
		String[] localizacoes = { "Praça da Liberdade - Memorial Vale", "Shopping Diamond Mall - Entrada Principal",
				"UFMG - Praça de Serviços", "Parque Municipal - Portaria 1" };

		String[] descricoes = { "Próximo à entrada do Memorial Vale", "Ao lado do estacionamento VIP",
				"Em frente à Biblioteca Central", "Próximo aos quiosques" };

		String[][] modelosBicicletas = { { "Caloi Elite", "Mountain Bike" }, { "Scott Scale", "Trail Bike" },
				{ "Trek Marlin", "Cross Country" }, { "Specialized Rockhopper", "Trail Bike" } };

		String[] marcas = { "Caloi", "Scott", "Trek", "Specialized" };

		// Criação dos totens e seus componentes
		for (int t = 0; t < localizacoes.length; t++) {
			// Criar totem com capacidade padrão de 20 trancas
			Totem totem = new Totem();
			totem.setLocalizacao(localizacoes[t]);
			totem.setDescricao(descricoes[t]);
			totem.setCapacidadeMaxima(20);
			totem = totemService.salvar(totem);

			// Criar trancas e bicicletas para cada totem
			for (int i = 1; i <= 20; i++) {
				Tranca tranca = new Tranca();
				tranca.setNumero(String.format("T%d-%02d", t + 1, i));
				tranca.setStatus(StatusTranca.LIVRE);
				tranca.setTotem(totem);

				// Criar bicicletas apenas para as primeiras 14 trancas
				if (i <= 14) {
					// Criar bicicleta
					Bicicleta bicicleta = new Bicicleta();
					bicicleta.setNumero(String.format("B%d-%02d", t + 1, i));
					bicicleta.setModelo(modelosBicicletas[t % modelosBicicletas.length][0]);
					bicicleta.setMarca(marcas[t % marcas.length]);
					bicicleta.setAno(2024);
					bicicleta.setStatus(StatusBicicleta.DISPONIVEL);

					// Salvar bicicleta e associar à tranca
					bicicleta = bicicletaService.salvar(bicicleta);
					tranca.setBicicleta(bicicleta);
					tranca.setStatus(StatusTranca.OCUPADA);
				}

				// Salvar tranca
				trancaService.salvar(tranca);
			}
		}

		System.out.println("Dados de demonstração inicializados com sucesso!");
	}
}