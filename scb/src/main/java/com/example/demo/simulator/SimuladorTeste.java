package com.example.demo.simulator;

public class SimuladorTeste {
    public static void main(String[] args) {
        HardwareSimulatorService simulador = new HardwareSimulatorService();
        simulador.init();

        System.out.println("\n=== TESTE COMPLETO DO SIMULADOR ===\n");

        // Teste 1: Fluxo normal de empréstimo/devolução
        testarFluxoNormal(simulador);

        // Teste 2: Tentativas de operações inválidas
        testarOperacoesInvalidas(simulador);

        // Teste 3: Múltiplas operações no mesmo totem
        testarMultiplasOperacoes(simulador);

        // Teste 4: Operações entre totens diferentes
        testarOperacoesEntreTotens(simulador);
    }

    private static void testarFluxoNormal(HardwareSimulatorService simulador) {
        try {
            System.out.println("\n=== Teste 1: Fluxo Normal ===\n");
            TotemSimulator totem = simulador.getTotem(1);
            TrancaSimulator tranca = totem.getTrancaPorNumero(1);

            totem.exibirMensagem("Iniciando fluxo normal");
            
            // 1. Prepara a tranca para receber bicicleta
            System.out.println("1.1. Preparando tranca");
            tranca.prepararParaBicicleta();
            totem.mostrarStatus();

            // 2. Trava com uma bicicleta
            System.out.println("1.2. Travando com bicicleta");
            tranca.travar(101L);
            totem.mostrarStatus();

            // 3. Libera a bicicleta
            System.out.println("1.3. Liberando bicicleta");
            tranca.liberar();
            totem.mostrarStatus();

            System.out.println("✅ Teste 1 concluído com sucesso!");
        } catch (Exception e) {
            System.out.println("❌ Erro no Teste 1: " + e.getMessage());
        }
    }

    private static void testarOperacoesInvalidas(HardwareSimulatorService simulador) {
        try {
            System.out.println("\n=== Teste 2: Operações Inválidas ===\n");
            TotemSimulator totem = simulador.getTotem(1);
            TrancaSimulator tranca = totem.getTrancaPorNumero(2);

            totem.exibirMensagem("Testando operações inválidas");

            // 1. Tentar liberar tranca vazia
            System.out.println("2.1. Tentando liberar tranca vazia");
            try {
                tranca.liberar();
            } catch (Exception e) {
                System.out.println("Erro esperado: " + e.getMessage());
            }

            // 2. Tentar travar tranca já travada
            System.out.println("\n2.2. Tentando travar tranca já travada");
            try {
                tranca.travar(102L);
            } catch (Exception e) {
                System.out.println("Erro esperado: " + e.getMessage());
            }

            // 3. Tentar acessar totem inexistente
            System.out.println("\n2.3. Tentando acessar totem inexistente");
            try {
                simulador.getTotem(99);
            } catch (Exception e) {
                System.out.println("Erro esperado: " + e.getMessage());
            }

            System.out.println("✅ Teste 2 concluído com sucesso!");

        } catch (Exception e) {
            System.out.println("❌ Erro inesperado no Teste 2: " + e.getMessage());
        }
    }

    private static void testarMultiplasOperacoes(HardwareSimulatorService simulador) {
        try {
            System.out.println("\n=== Teste 3: Múltiplas Operações ===\n");
            TotemSimulator totem = simulador.getTotem(2);
            TrancaSimulator tranca1 = totem.getTrancaPorNumero(1);
            TrancaSimulator tranca2 = totem.getTrancaPorNumero(2);

            totem.exibirMensagem("Testando múltiplas operações");

            // 1. Prepara as trancas
            System.out.println("3.1. Preparando trancas");
            tranca1.prepararParaBicicleta();
            tranca2.prepararParaBicicleta();
            totem.mostrarStatus();

            // 2. Trava bicicletas nas duas trancas
            System.out.println("\n3.2. Travando bicicletas");
            tranca1.travar(103L);
            tranca2.travar(104L);
            totem.mostrarStatus();

            // 3. Libera apenas uma bicicleta
            System.out.println("\n3.3. Liberando uma bicicleta");
            tranca1.liberar();
            totem.mostrarStatus();

            System.out.println("✅ Teste 3 concluído com sucesso!");

        } catch (Exception e) {
            System.out.println("❌ Erro no Teste 3: " + e.getMessage());
        }
    }

    private static void testarOperacoesEntreTotens(HardwareSimulatorService simulador) {
        try {
            System.out.println("\n=== Teste 4: Operações Entre Totens ===\n");
            TotemSimulator totem1 = simulador.getTotem(1);
            TotemSimulator totem2 = simulador.getTotem(2);

            // 1. Prepara bicicleta no primeiro totem
            System.out.println("4.1. Preparando bicicleta no Totem 1");
            TrancaSimulator trancaOrigem = totem1.getTrancaPorNumero(3);
            trancaOrigem.prepararParaBicicleta();
            trancaOrigem.travar(105L);
            totem1.mostrarStatus();

            // 2. Libera do primeiro totem
            System.out.println("\n4.2. Liberando do Totem 1");
            trancaOrigem.liberar();
            totem1.mostrarStatus();

            // 3. Trava no segundo totem
            System.out.println("\n4.3. Travando no Totem 2");
            TrancaSimulator trancaDestino = totem2.getTrancaPorNumero(3);
            trancaDestino.prepararParaBicicleta();
            trancaDestino.travar(105L);
            totem2.mostrarStatus();

            System.out.println("✅ Teste 4 concluído com sucesso!");

        } catch (Exception e) {
            System.out.println("❌ Erro no Teste 4: " + e.getMessage());
        }
    }
}
