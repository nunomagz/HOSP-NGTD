package Menu;

import Configurações.Configurações;
import Gestao.GestorFicheiros;
import Gestao.GestãoHOSP;
import Modelo.Hospital;

import java.io.IOException;
import java.util.Scanner;

public class Menu {
    private GestãoHOSP gestao;
    private GestorFicheiros dataIo;
    private Hospital hospital;
    private Scanner scanner;


    public Menu() {
        this.gestao = new GestãoHOSP();
        this.dataIo = new GestorFicheiros();
        this.hospital = new Hospital();
        this.scanner = new Scanner(System.in);
    }

    public void Iniciar(){
        menuInicial();
    }

    public void menuInicial(){
        int opcao;
        do {
            pausar();
            limparEcra();
            System.out.println("╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║                    URGENCIAS DO HOSPITAL                     ║");
            System.out.println("╠══════════════════════════════════════════════════════════════╣");
            System.out.println("║  1. Gerir dados (Médicos, Especialidades, Sintomas)          ║");
            System.out.println("║  2. Funcionamento do Hospital (Triagem)                      ║");
            System.out.println("║  3. Estatísticas e Logs                                      ║");
            System.out.println("║  4. Configurações                                            ║");
            System.out.println("║  5. Sair e Guardar                                           ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");


            opcao = lerInteiro("Opcao: ");

            switch (opcao) {
                case 1:
                    menuGestaoDados();
                    break;
                case 2:
                    //menuHospital();
                    break;
                case 3:
                    //menuEstatisticas();
                    break;
                case 4:
                    menuConfiguracoes();
                    break;
                case 0:
                    menuSair();
                    break;
                default:
                    System.out.println("\nOpcao invalida!");
                    pausar();
            }
        } while (opcao != 0);
    }
    public void menuGestaoDados(){
        int opcao;
        do {
            limparEcra();
            System.out.println("Password:");
            String pass = scanner.next().trim();
            if (!pass.equals(Configurações.getPassword())) {
                System.out.println("Password incorreta!");
                return;
            }

            System.out.println("╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║                        GESTÃO DE DADOS                       ║");
            System.out.println("╠══════════════════════════════════════════════════════════════╣");
            System.out.println("║  1. Gerir Médicos                                            ║");
            System.out.println("║  2. Gerir Especíalidades                                     ║");
            System.out.println("║  3. Gerir Sintomas                                           ║");
            System.out.println("║  0. Voltar                                                   ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");


            opcao = lerInteiro("Opcao: ");

            switch (opcao) {
                case 1:
                    menuGerirMedicos();
                    break;
                case 2:
                    //menuGerirEspecialidades();
                    break;
                case 3:
                    //menuSintomas();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("\nOpcao invalida!");
                    pausar();
            }
        } while (opcao != 0);
    }

    private void menuGerirMedicos() {
        int opcao;
        do {
            limparEcra();
            System.out.println("╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║                      GESTÃO DE MEDICOS                       ║");
            System.out.println("╠══════════════════════════════════════════════════════════════╣");
            System.out.println("║  1. Adicionar médico                                         ║");
            System.out.println("║  2. Listar todos os médicos                                  ║");
            System.out.println("║  3. Alterar médicos                                          ║");
            System.out.println("║  4. Remover médicos                                          ║");
            System.out.println("║  0. Voltar                                                   ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");

            opcao = lerInteiro("Opcao: ");

            switch (opcao) {
                case 1:
                    adicionarMedico();
                    break;
                case 2:
                    listarMedicos();
                    break;
                case 3:
                    alterarMedico();
                    break;
                case 4:
                    removerMedico();
                case 0:
                    break;
                default:
                    System.out.println("\nOpcao invalida!");
                    pausar();
            }
        } while (opcao != 0);
    }

    private void adicionarMedico(){
        System.out.println("=== ADICIONAR MÉDICO ===\n");
        String nome = lerTextoValido("Nome do Médico: ");
        String codEsp = lerString("Códico da Especíalidade (ex: CARD): ");
        int entrada = lerInteiro("Hora de Entrada (0-23): ");
        int saida = lerInteiro("Hora de Saida (0-23): ");
        int salario = lerInteiro("Salário/Hora: ");

        boolean sucesso = gestao.adicionarMedico(nome, codEsp, entrada, saida, salario);

        if (sucesso) {
            System.out.println("Medico adicionado com sucesso!");
        } else {
            System.out.println("Erro: Verifique se a especialidade existe ou se o nome já está registado.");
        }
    }

    private void listarMedicos(){
        System.out.println("=== LISTAR MÉDICOS ===\n");
        if (gestao.getNMedicos() == 0) {
            System.out.println("Não existem médicos registados.");
            return;
        }
        for (int i = 0; i < gestao.getNMedicos(); i++) {
            System.out.println(gestao.getMedicoAt(i).toString());
        }
    }

    private void alterarMedico(){
        System.out.println("=== AlTERAR MÉDICO ===\n");
        String nome = lerTextoValido("Nome do médico a alterar: ");

        if (gestao.procurarMedicoPorNome(nome) == null) {
            System.out.println("Médico não encontrado.");
            return;
        }

        System.out.println("Deixe vazio os campos que não quer alterar...");
        String novaEsp = lerString("Nova Especialidade (Enter para manter): ");
        int novaEntrada = lerInteiro("Nova Entrada (Enter para manter): ");
        int novaSaida = lerInteiro("Nova Saída (Enter para manter): ");
        int novoSalario = lerInteiro("Novo Salário (Enter para manter): ");

        boolean ok = gestao.atualizarMedico(nome, novaEsp, novaEntrada, novaSaida, novoSalario);
        if (ok){
            System.out.println("Dados atualizados.");
        } else {
            System.out.println("Erro ao atualizar!");
        }
    }

    private void removerMedico(){

        System.out.println("=== REMOVER MÉDICO ===\n");
        String nome = lerTextoValido("Nome do medico a remover: ");

        String confirmacao = lerString("Tem a certeza? (S/N):");
        if (!confirmacao.equalsIgnoreCase("S")) {
            return;
        }

        boolean ok = gestao.removerMedico(nome);

        if (ok) {
            System.out.println("Médico removido.");
        } else {
            System.out.println("Médico não encontrado.");
        }
    }


    private void menuConfiguracoes(){
        System.out.println("Password:");
        String pass = scanner.next().trim();
        if (!pass.equals(Configurações.getPassword())){
            System.out.println("Password incorreta!");
            return;
        }
        int opcao;
        do {
            limparEcra();
            System.out.println("╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║                         CONFIGURAÇÕES                        ║");
            System.out.println("╠══════════════════════════════════════════════════════════════╣");
            System.out.println("║  1. Alterar Caminho dos Ficheiros                            ║");
            System.out.println("║  2. Alterar Tempos de consulta                               ║");
            System.out.println("║  3. Alterar Limites de Espera                                ║");
            System.out.println("║  4. Alterar Password                                         ║");
            System.out.println("║  0. Voltar                                                   ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");

            opcao = lerInteiro("Opcao: ");

            switch (opcao) {
                case 1:
                    System.out.println("Novo Caminho: ");
                    String novoCaminho = scanner.next().trim();
                    Configurações.setCaminhoficheiro(novoCaminho);
                    System.out.println("Caminho atualizado!");
                    break;
                case 2:
                    System.out.println("Definir unidades de tempo para consultas:");
                    Configurações.setTempoConsultaBaixa(lerInteiro("Baixa (Atual " + Configurações.getTempoConsultaBaixa() + "): "));
                    Configurações.setTempoConsultaMedia(lerInteiro("Media (Atual " + Configurações.getTempoConsultaMedia() + "): "));
                    Configurações.setTempoConsultaUrgente(lerInteiro("Urgente (Atual " + Configurações.getTempoConsultaUrgente() + "): "));
                    System.out.println("Tempos atualizados!");
                    break;
                case 3:
                    System.out.println("Falta implementar esta funcionalidade.");
                    break;
                case 4:
                    System.out.println("Nova Password:");
                    String passe = scanner.next().trim();
                    Configurações.setPassword(passe);
                    System.out.println("Password Atualizada!");
                    break;
                case 0:
                    break;
                default:
                    System.out.println("\nOpcao invalida!");
                    pausar();
            }
        } while (opcao != 0);
    }

    private void menuSair () {
        String resposta = lerString("Deseja guardar as alterações antes de sair? (S/N): ");

        // Verifica se a resposta é "S" ou "s" (ignora maiúsculas/minúsculas)
        if (resposta.equalsIgnoreCase("S")) {
            try {
                System.out.println("A guardar dados...");
                dataIo.guardarTudo(gestao);
                System.out.println("Dados guardados com sucesso!");
            } catch (IOException e) {
                System.out.println("ERRO: Não foi possível guardar os dados: " + e.getMessage());
            }
        } else if (resposta.equalsIgnoreCase("N")){
            System.out.println("A sair sem guardar alterações...");
        }
    }

    // ==================== METODOS AUXILIARES ====================

    /**
     * Le um numero inteiro do utilizador.
     * @param mensagem Mensagem a apresentar
     * @return Numero inteiro lido
     */
    private int lerInteiro(String mensagem) {
        System.out.print(mensagem);
        while (!scanner.hasNextInt()) {
            System.out.print("Valor invalido. " + mensagem);
            scanner.next();
        }
        int valor = scanner.nextInt();
        scanner.nextLine(); // Limpar buffer
        return valor;
    }

    /**
     * Le uma string do utilizador.
     * @param mensagem Mensagem a apresentar
     * @return String lida
     */
    private String lerString(String mensagem) {
        String valor;
        do {
            System.out.print(mensagem);
            valor = scanner.nextLine().trim();
            if (valor.isEmpty()) {
                System.out.println("Este campo e obrigatorio.");
            }
        } while (valor.isEmpty());
        return valor;
    }

    private String lerTextoValido(String mensagem) {
        String texto;
        while (true) {
            System.out.println(mensagem);
            texto = scanner.nextLine().trim();

            if (!texto.isEmpty()) {
                return texto;
            }
            System.out.println("Erro: este campo não pode estar vazio. Tente novamente.");
        }
    }


    /**
     * Pausa a execucao ate o utilizador pressionar Enter.
     */
    private void pausar() {
        System.out.print("\nPressione Enter para continuar...");
        scanner.nextLine();
    }

    /**
     * Limpa o ecra (simula limpeza com linhas em branco).
     */
    private void limparEcra() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
}
