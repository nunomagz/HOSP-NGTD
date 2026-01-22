package Menu;

import Configuracoes.Configuracoes;
import Gestao.GestorFicheiros;
import Gestao.GestaoHOSP;
import Modelo.Hospital;
import Modelo.RelogioHospital;
import Modelo.Utente;

import java.io.IOException;
import java.util.Scanner;

public class Menu {
    private GestaoHOSP gestao;
    private GestorFicheiros ficheiros;
    private Hospital hospital;
    private Scanner scanner;
    private RelogioHospital relogio;


    public Menu() {
        this.gestao = new GestaoHOSP();
        this.ficheiros = new GestorFicheiros();
        this.hospital = new Hospital();
        this.scanner = new Scanner(System.in);
        this.relogio = new RelogioHospital();
    }

    public void iniciar(){
        System.out.println("A carregar os dados do sistema...");
        try {
            ficheiros.carregarTudo(gestao);
            System.out.println("Dados carregados com sucesso!");
        }catch (IOException e) {
            System.out.println("Não foi possivel carregar os dados iniciais (" + e.getMessage() + ")");
        }
        menuInicial();
    }
    // --- MENU INICIAL ---
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
                    menuHospital();
                    break;
                case 3:
                    menuEstatisticas();
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

    // --- MENU GESTÃO DE DADOS ---
    public void menuGestaoDados(){

        if(!autenticarAdmin()) {
            return;
        }

        int opcao;

        do {
            limparEcra();
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
                    menuGerirEspecialidades();
                    break;
                case 3:
                    menuSintomas();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("\nOpcao invalida!");
                    pausar();
            }
        } while (opcao != 0);
    }

    // --- MENU GESTÃO DE MÉDICOS ---
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
                    pausar();
                    break;
                case 2:
                    listarMedicos();
                    pausar();
                    break;
                case 3:
                    alterarMedico();
                    pausar();
                    break;
                case 4:
                    removerMedico();
                    pausar();
                    break;
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
        String codEsp = lerTextoValido("Código da Especíalidade (ex: CARD): ").toUpperCase();
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
        String nome = lerTextoValido("Nome do Médico: ");

        if (gestao.procurarMedicoPorNome(nome) == null) {
            System.out.println("Medico não encontrado.");
            return;
        }

        System.out.println("Deixe vazio (Enter) para manter os dados. ");

        String novaEsp = lerStringAlterar("Códico da Especíalidade (ex: CARD): ").toUpperCase();
        int novaEntrada = 0;
        String inputEntrada = lerStringAlterar("Nova Entrada: ");
        if (!inputEntrada.isEmpty()) {
            try {
                novaEntrada = Integer.parseInt(inputEntrada);
                if (novaEntrada < 0 || novaEntrada > 23) {
                    System.out.println("Hora inválida (0-23). Valor mantido.");
                    novaEntrada = 0;
                }
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido (tem que ser um número).Valor mantido");
            }
        }

        int novaSaida = 0;
        String inputSaida = lerStringAlterar("Nova Saida: ");
        if (!inputSaida.isEmpty()) {
            try {
                novaSaida = Integer.parseInt(inputSaida);
                if (novaSaida < 0 || novaSaida > 23) {
                    System.out.println("Hora inválida (0-23). Valor mantido.");
                    novaSaida = 0;
                }
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido (tem que ser um número).Valor mantido");
            }
        }

        int novoSalario = 0;
        String inputSalario = lerStringAlterar("Novo Salário: ");
        if (!inputSalario.isEmpty()) {
            try {
                novoSalario = Integer.parseInt(inputSalario);
            }catch (NumberFormatException e) {
                System.out.println("Salário inválido. Valor mantido.");
            }
        }



        boolean sucesso = gestao.atualizarMedico(nome, novaEsp, novaEntrada, novaSaida, novoSalario);
        if (sucesso){
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

    // --- MENU GESTÃO DE ESPECIALIDADES ---
    private void menuGerirEspecialidades() {
        int opcao;
        do {
            limparEcra();
            System.out.println("╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║                    GESTÃO DE ESPECIALIDADES                  ║");
            System.out.println("╠══════════════════════════════════════════════════════════════╣");
            System.out.println("║  1. Adicionar Especialidade                                  ║");
            System.out.println("║  2. Listar Especialidades                                    ║");
            System.out.println("║  3. Alterar Especialidade                                    ║");
            System.out.println("║  4. Remover Especialidade                                    ║");
            System.out.println("║  0. Voltar                                                   ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");

            opcao = lerInteiro("Opção: ");

            switch (opcao) {
                case 1:
                    adicionarEspecialidade();
                    pausar();
                    break;
                case 2:
                    listarEspecialidades();
                    pausar();
                    break;
                case 3:
                    alterarEspecialidade();
                    pausar();
                    break;
                case 4:
                    removerEspecialidade();
                    pausar();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
                    pausar();
            }
        } while (opcao != 0);
    }

    private void adicionarEspecialidade() {
        System.out.println("\n=== ADICIONAR ESPECIALIDADE ===");
        String codigo = lerTextoValido("Código (ex: CARD): ").toUpperCase();
        String nome = lerTextoValido("Nome (ex: Cardiologia): ");

        boolean sucesso = gestao.adicionarEspecialidade(codigo, nome);

        if (sucesso) {
            System.out.println("Especialidade adicionada.");
        } else {
            System.out.println("Erro: Código duplicado ou inválido.");
        }
    }

    private void listarEspecialidades() {
        System.out.println("\n=== LISTA DE ESPECIALIDADES ===");
        if (gestao.getNEspecialidades() == 0) {
            System.out.println("Nenhuma especialidade registada.");
            return;
        }
        for (int i = 0; i < gestao.getNEspecialidades(); i++) {
            System.out.println(gestao.getEspecialidadeAt(i).toString());
        }
    }

    private void alterarEspecialidade() {
        System.out.println("\n=== ALTERAR ESPECIALIDADE ===");
        String codAtual = lerTextoValido("Código da especialidade a alterar: ").toUpperCase();

        if (gestao.procurarEspecialidadePorCodigo(codAtual) == null) {
            System.out.println("Especialidade não encontrada.");
            return;
        }

        System.out.println("Deixe vazio (Enter) para manter os dados.");

        String novoCod = lerStringAlterar("Novo Código: ").toUpperCase();
        String novoNome = lerStringAlterar("Novo Nome: ");

        if (novoCod.isEmpty()) {
            novoCod = null;
        }
        if (novoNome.isEmpty()) {
            novoNome = null;
        }

        boolean ok = gestao.atualizarEspecialidade(codAtual, novoCod, novoNome);
        if (ok) {
            System.out.println("Especialidade atualizada.");
        } else {
            System.out.println("Erro ao atualizar (Verifique duplicados).");
        }
    }

    private void removerEspecialidade() {
        System.out.println("\n=== REMOVER ESPECIALIDADE ===");
        String codigo = lerTextoValido("Código da especialidade a remover: ").toUpperCase();

        String cond = lerString("Tem a certeza? (S/N): ");
        if (!cond.equalsIgnoreCase("S")) return;

        boolean ok = gestao.removerEspecialidade(codigo);
        if (ok) {
            System.out.println("Especialidade removida.");
        } else {
            System.out.println("Erro: Especialidade não encontrada.");
        }
    }

    // --- MENU GESTÃO DE SINTOMAS ---
    private void menuSintomas() {
        int opcao;
        do {
            limparEcra();
            System.out.println("╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║                       GESTÃO DE SINTOMAS                     ║");
            System.out.println("╠══════════════════════════════════════════════════════════════╣");
            System.out.println("║  1. Adicionar Sintoma                                        ║");
            System.out.println("║  2. Listar Sintomas                                          ║");
            System.out.println("║  3. Alterar Sintoma (Nível ou Especialidades)                ║");
            System.out.println("║  4. Remover Sintoma                                          ║");
            System.out.println("║  0. Voltar                                                   ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");

            opcao = lerInteiro("Opção: ");

            switch (opcao) {
                case 1:
                    adicionarSintoma();
                    pausar();
                    break;
                case 2:
                    listarSintomas();
                    pausar();
                    break;
                case 3:
                    alterarSintoma();
                    pausar();
                    break;
                case 4:
                    removerSintoma();
                    pausar();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
                    pausar();
            }
        } while (opcao != 0);
    }

    private void adicionarSintoma() {
        System.out.println("\n=== ADICIONAR SINTOMA ===");
        String nome = lerTextoValido("Nome do Sintoma: ");

        // Validação simples do nível
        String nivel;
        while (true) {
            System.out.print("Nível de Urgência (Verde/Amarelo/Vermelho): ");
            nivel = scanner.nextLine().trim();
            if (nivel.equalsIgnoreCase("Verde") ||
                    nivel.equalsIgnoreCase("Amarelo") ||
                    nivel.equalsIgnoreCase("Vermelho")) {
                break;
            }
            System.out.println("Nível inválido. Tente novamente.");
        }

        // Lê múltiplas especialidades
        String[] codigos = lerListaEspecialidades();

        boolean sucesso = gestao.adicionarSintoma(nome, nivel, codigos);
        if (sucesso) {
            System.out.println("Sintoma registado.");
        } else {
            System.out.println("Erro: Verifique se as especialidades existem.");
        }
    }

    private void listarSintomas() {
        System.out.println("\n=== LISTA DE SINTOMAS ===");

        if (gestao.getNSintomas() == 0) {
            System.out.println("Nenhum sintoma registado.");
            return;
        }
        for (int i = 0; i < gestao.getNSintomas(); i++) {

            Modelo.Sintoma s = gestao.getSintomaAt(i);
            System.out.print("Sintoma: " + s.getNome() + " | Nível: " + s.getNivelUrgencia() + " | Esp: ");
            if (s.getCodigoEspecialidade() != null) {
                for (String c : s.getCodigoEspecialidade()) System.out.print(c + " ");
            }
            System.out.println();
        }
    }

    private void alterarSintoma() {
        System.out.println("\n=== ALTERAR SINTOMA ===");
        String nome = lerTextoValido("Nome do Sintoma a alterar: ");

        if (gestao.procurarSintomaPorNome(nome) == null) {
            System.out.println("Sintoma não encontrado.");
            return;
        }

        System.out.println("1. Alterar Nível de Urgência");
        System.out.println("2. Alterar Especialidades Associadas");
        System.out.println("0. Cancelar");
        int op = lerInteiro("Escolha: ");

        if (op == 1) {
            String novoNivel = lerTextoValido("Novo Nível (Verde/Amarelo/Vermelho): ");
            boolean ok = gestao.atualizarNivelSintoma(nome, novoNivel);
            if(ok) System.out.println("Nível atualizado.");
            else System.out.println("Erro ao atualizar nível.");
        } else if (op == 2) {
            System.out.println("Isto irá substituir todas as especialidades anteriores.");
            String[] novosCodigos = lerListaEspecialidades();
            boolean ok = gestao.atualizarEspecialidadesSintoma(nome, novosCodigos);
            if(ok) System.out.println("Especialidades atualizadas.");
            else System.out.println("Erro: Alguma especialidade não existe.");
        }
    }

    private void removerSintoma() {
        System.out.println("\n=== REMOVER SINTOMA ===");
        String nome = lerTextoValido("Nome do Sintoma: ");

        String conf = lerString("Tem a certeza? (S/N): ");
        if (!conf.equalsIgnoreCase("S")) return;

        if (gestao.removerSintoma(nome)) {
            System.out.println("Sintoma removido.");
        } else {
            System.out.println("Erro: Sintoma não encontrado.");
        }
    }

    // --- MENU FUNCIONAMENTO DO HOSPITAL ---
    //mudar menu, modificar parte de utentes
    private void menuHospital() {
        int opcao;
        do {
            limparEcra();
            System.out.println("╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║                  FUNCIONAMENTO DO HOSPITAL                   ║");
            System.out.println("╠══════════════════════════════════════════════════════════════╣");
            System.out.println("║  1. Admitir Utente                                           ║");
            System.out.println("║  2. Avançar Tempo                                            ║");
            System.out.println("║  3. Listar Utentes em espera                                 ║");
            System.out.println("║  0. Voltar                                                   ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");

            System.out.println("Dia " + relogio.getDiaAtual() + " | Hora " + relogio.getHoraAtual());

            opcao = lerInteiro("Opção: ");

            switch (opcao) {
                case 1:
                    admitirUtente();
                    pausar();
                    break;
                case 2:
                    avancarTempo();
                    pausar();
                    break;
                case 3:
                    listarUtentes();
                    pausar();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
                    pausar();
            }
        } while (opcao != 0);
    }

    private void admitirUtente() {
        System.out.println("\n=== ADMITIR UTENTE ===");

        String nome = lerTextoValido("Nome do Utente: ");
        int idade = lerInteiro("Idade do Utente: ");


        Utente u = gestao.admitirUtenteSimples(nome, idade);

        if (u != null) {
            System.out.println("Utente admitido com sucesso! Número: " + u.getNumero());
        } else {
            System.out.println("Erro ao admitir utente.");
        }
    }

    private void avancarTempo() {
        limparEcra();
        System.out.println("\n--- A AVANÇAR O TEMPO ---");

        relogio.avancarTempo();
        System.out.println("Dia: " + relogio.getDiaAtual() + " | Hora Atual: " + relogio.getHoraAtual());

        System.out.println("O tempo avançou. Médicos atenderam doentes e altas foram dadas.");
        System.out.println("(Nota: As notificações aparecerão aqui quando o Aluno 3 terminar a lógica)");
    }

    private void listarUtentes() {
        limparEcra();
        System.out.println("\n=== UTENTES EM SALA DE ESPERA ===");
        if (gestao.getNUtentes() == 0) {
            System.out.println("Nenhum utente registado.");
            return;
        }
        for (int i = 0; i < gestao.getNUtentes(); i++) {
            System.out.println("Lista de utentes atualmente em sala de espera:");
            System.out.println(gestao.getUtenteAt(i).toString());
        }

        int opcao = lerInteiro("\nEscolha um Utente (0 para voltar): ");
        //if para verificar se a opcao é valida e escolher o utente para efetuar a acao
        if (opcao > 0 && opcao <= gestao.getNUtentes()) {
            acaoUtente(gestao.getUtenteAt(opcao - 1));
        }
    }

    //classe para interagir com o utente selecionado
    private void acaoUtente(Utente u) {
        //implementar ações para o utente selecionado
        limparEcra();
        int opcao;
        if (u == null) {
            System.out.println("Utente inválido.");
            return;
        }

        System.out.println("=== AÇÃO UTENTE ===\n");
        System.out.println("Utente Selecionado: " + u.toString());
        System.out.println("1. Realizar Triagem");
        System.out.println("2. Encaminhar para Médico");

        do {
            opcao = lerInteiro("\nEscolha uma ação (0 para voltar):");

            switch (opcao) {
                case 1:
                    realizarTriagem(u);
                    pausar();
                    break;
                case 2:
                    encaminharMedico(u);
                    pausar();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
                    pausar();
            }
        } while (opcao != 0);
    }

    // TESTAR PARA VER SE FUNCIONA
    private void realizarTriagem(Utente u) {
        limparEcra();
        System.out.println("\n=== NOVA TRIAGEM ===");

        System.out.println("Utente: " + u.getNome() + " | Idade: " + u.getIdade());

        System.out.println("Selecione o Sintoma Principal:");
        //Listar sintomas disponíveis para ajudar o utilizador
        for(int i=0; i < gestao.getNSintomas(); i++) {
            System.out.println("- " + gestao.getSintomaAt(i).getNome());
        }

        String nomeSintoma = lerTextoValido("Sintoma: ");
        Modelo.Sintoma s = gestao.procurarSintomaPorNome(nomeSintoma);

        if (s == null) {
            System.out.println("Erro: Sintoma não encontrado.");
            return;
        }
        // Valida se o sintoma existe antes de continuar
//        if (gestao.procurarSintomaPorNome(nomeSintoma) == null) {
//            System.out.println("Erro: Sintoma não reconhecido. Crie-o primeiro na Gestão de Dados.");
//            return;
//        }

        u.setSintoma(s.getNome());
        u.setNivelUrgencia(s.getNivelUrgencia());
        u.resetarTempoEspera(); // Garante que o tempo começa a contar agora

        System.out.println("Triagem concluída! Nível atribuído: " + u.getNivelUrgencia());

//        String nivelUrgencia = gestao.procurarSintomaPorNome(nomeSintoma).getNivelUrgencia();
//
//        Modelo.Utente utente = gestao.adicionarUtente(u.nome, u.idade, nomeSintoma, nivelUrgencia);
//        if (utente != null) {
//            ciclo para enviar utente diretamente para medico se disponivel
//            System.out.println("Triagem realizada.");
//        } else {
//            System.out.println("Erro ao realizar triagem. Verifique os dados fornecidos.");
//        }

    }

    private void encaminharMedico(Utente u) {
        limparEcra();
        System.out.println("\n=== ENCAMINHAR PARA MÉDICO ===");

        // Lógica para encaminhar o utente para o médico adequado
        // ver se medico da especialidade está disponivel

        System.out.println("Utente " + u.getNome() + " encaminhado para o médico.");
    }

    // --- MENU ESTATÍSTICAS E LOGS ---
    private void menuEstatisticas() {
        int opcao;
        do {
            limparEcra();
            System.out.println("╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║                     ESTATÍSTICAS E LOGS                      ║");
            System.out.println("╠══════════════════════════════════════════════════════════════╣");
            System.out.println("║  1. Ver Média de Utentes Atendidos                           ║");
            System.out.println("║  2. Top 3 Especialidades mais procuradas                     ║");
            System.out.println("║  3. Consultar Logs do Sistema                                ║");
            System.out.println("║  0. Voltar                                                   ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");

            opcao = lerInteiro("Opção: ");

            switch (opcao) {
                case 1:
                    // System.out.println("Média: " + gestao.calcularMediaUtentes());
                    System.out.println("A aguardar implementação do Aluno 2.");
                    pausar();
                    break;
                case 2:
                    // gestao.mostrarTopEspecialidades();
                    System.out.println("A aguardar implementação do Aluno 2.");
                    pausar();
                    break;
                case 3:
                    System.out.println("--- LOGS DO SISTEMA ---");
                    // DataIO.lerLogs(); ou similar
                    System.out.println("Ficheiro de logs não encontrado.");
                    pausar();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
                    pausar();
            }
        } while (opcao != 0);
    }

    // --- MENU CONFIGURAÇÕES ---
    private void menuConfiguracoes() {

        if(!autenticarAdmin()) {
            return;
        }

        int opcao;
        do {
            limparEcra();
            System.out.println("╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║                         CONFIGURAÇÕES                        ║");
            System.out.println("╠══════════════════════════════════════════════════════════════╣");
            System.out.println("║  1. Alterar Caminho dos Ficheiros                            ║");
            System.out.println("║  2. Alterar Tempos de Consulta                               ║");
            System.out.println("║  3. Alterar Limites de Espera (Mudança de Nível)             ║");
            System.out.println("║  4. Alterar Password                                         ║");
            System.out.println("║  0. Voltar                                                   ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");

            opcao = lerInteiro("Opção: ");

            switch (opcao) {
                case 1:
                    // Lógica para String (Caminho)
                    System.out.print("Novo Caminho (Atual: " + Configuracoes.getCaminhoficheiro() + "): ");
                    String novoCaminho = scanner.nextLine().trim();
                    if (!novoCaminho.isEmpty()) {
                        Configuracoes.setCaminhoficheiro(novoCaminho);
                        System.out.println("Caminho atualizado!");
                    } else {
                        System.out.println("Mantido o caminho anterior.");
                    }
                    pausar();
                    break;

                case 2:
                    System.out.println("\n--- Tempos de Consulta ---");

                    int tBaixa = lerIntAlterar("Tempo Baixa", Configuracoes.getTempoConsultaBaixa());
                    Configuracoes.setTempoConsultaBaixa(tBaixa);

                    int tMedia = lerIntAlterar("Tempo Média", Configuracoes.getTempoConsultaMedia());
                    Configuracoes.setTempoConsultaMedia(tMedia);

                    int tUrgente = lerIntAlterar("Tempo Urgente", Configuracoes.getTempoConsultaUrgente());
                    Configuracoes.setTempoConsultaUrgente(tUrgente);

                    System.out.println("Tempos atualizados!");
                    pausar();
                    break;

                case 3:
                    System.out.println("\n--- Limites de Espera (para subir de nível) ---");

                    int lVerde = lerIntAlterar("Verde -> Amarelo", Configuracoes.getLimiteEsperaVerdeParaAmarelo());
                    Configuracoes.setLimiteEsperaVerdeParaAmarelo(lVerde);

                    int lAmarelo = lerIntAlterar("Amarelo -> Vermelho", Configuracoes.getLimiteEsperaAmareloParaVermelho());
                    Configuracoes.setLimiteEsperaAmareloParaVermelho(lAmarelo);

                    int lVermelho = lerIntAlterar("Vermelho -> Saída", Configuracoes.getLimiteEsperaVermelhoSaida());
                    Configuracoes.setLimiteEsperaVermelhoSaida(lVermelho);

                    System.out.println("Limites atualizados!");
                    pausar();
                    break;

                case 4:
                    System.out.print("Nova Password (Enter para cancelar): ");
                    String passe = scanner.nextLine().trim();
                    if (!passe.isEmpty()) {
                        Configuracoes.setPassword(passe);
                        System.out.println("Password Atualizada!");
                    } else {
                        System.out.println("Password mantida.");
                    }
                    pausar();
                    break;

                case 0:
                    break;
                default:
                    System.out.println("\nOpção inválida!");
                    pausar();
            }
        } while (opcao != 0);
    }

    // --- MENU PARA SAIR ---
    private void menuSair () {
        String resposta = lerString("Deseja guardar as alterações antes de sair? (S/N): ");

        // Verifica se a resposta é "S" ou "s" (ignora maiúsculas/minúsculas)
        if (resposta.equalsIgnoreCase("S")) {
            try {
                System.out.println("A guardar dados...");
                ficheiros.guardarTudo(gestao);
                System.out.println("Dados guardados com sucesso!");
            } catch (IOException e) {
                System.out.println("ERRO: Não foi possível guardar os dados: " + e.getMessage());
            }
        } else if (resposta.equalsIgnoreCase("N")){
            System.out.println("A sair sem guardar alterações...");
        }
    }

    // --- MÉTODOS AUXILIARES ---

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
    //metodo para os submenus Alterar para validar o vazio e nao alterar nada.
    private String lerStringAlterar(String mensagem) {
        System.out.println(mensagem);
        return scanner.nextLine().trim();
    }

    //metodo para o menu configurações Alterar para validar o vazio e nao alterar nada.
    private int lerIntAlterar(String mensagem, int valorAtual) {
        System.out.print(mensagem + " (Atual " + valorAtual + "): ");
        String input = scanner.nextLine().trim();

        // Se estiver vazio, mantém o atual
        if (input.isEmpty()) {
            return valorAtual;
        }

        try {
            int novoValor = Integer.parseInt(input);
            // Podes adicionar validações extra aqui (ex: não aceitar negativos)
            if (novoValor < 0) {
                System.out.println("alor não pode ser negativo. Mantido o anterior.");
                return valorAtual;
            }
            return novoValor;
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido (não é número). Mantido o anterior.");
            return valorAtual;
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

    private String[] lerListaEspecialidades() {
        System.out.println("Insira os código de especialidades (ex: CARD).");
        System.out.println("Digite 'FIM' para terminar a lista");

        String[] temp = new String[10];
        int contador = 0;

        while (contador < temp.length) {
            System.out.println("Especialidade " + (contador +1) + ": ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("FIM")) {
                if (contador == 0) {
                    System.out.println("Tem que inserir pelo menos uma especialidade");
                    continue;
                }
                break;
            }
            if (!input.isEmpty()) {
                if (gestao.procurarEspecialidadePorCodigo(input) != null) {
                    temp[contador++] = input;
                } else {
                    System.out.println("Especialidade '" + input + "' não existe! Crie-a primeiro.");
                }
            }
        }
        String[] finalArray = new String[contador];
        for (int i = 0; i < contador; i++) {
            finalArray[i] = temp[i];
        }
            return finalArray;
    }
    private boolean autenticarAdmin() {
        while (true) {
            System.out.println("Password do admin (ou ENTER para voltar): ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return false;
            }
            if(input.equals(Configuracoes.getPassword())){
                return true;
            }
            System.out.println("Password incorreta! Tente novamente.");
        }
    }
}
