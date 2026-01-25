package Menu;

import Configuracoes.Configuracoes;
import Gestao.GestorFicheiros;
import Gestao.GestaoHOSP;
import Modelo.Hospital;
import Modelo.RelogioHospital;
import Modelo.Utente;

import java.io.File;
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
            ficheiros.escreverLog("Dia " + relogio.getDiaAtual() + " | Hora " + relogio.getHoraAtual() +
                    ": Utente " + u.getNome() + " (Nº" + u.getNumero() + ") deu entrada no hospital.");
        } else {
            System.out.println("Erro ao admitir utente.");
        }
    }

    private void avancarTempo() {
        limparEcra();
        System.out.println("\n--- A AVANÇAR O TEMPO ---");

        // 1. Verificar mudança de dia para o Log
        if (relogio.getHoraAtual() == 1) {
            ficheiros.escreverLog("=== INÍCIO DO DIA " + relogio.getDiaAtual() + " ===");
        }

        // 2. Avançar o relógio
        relogio.avancarTempo();
        int hora = relogio.getHoraAtual(); // Guardar a hora nova numa variável para usar abaixo

        // 3. Processar Turnos dos Médicos (Entradas e Saídas) [NOVO]
        // Percorre todos os médicos registados na gestao
        for (int i = 0; i < gestao.getNMedicos(); i++) {
            Modelo.Medico m = gestao.getMedicoAt(i);

            // Verificar Entrada
            if (m.getHoraEntrada() == hora) {
                m.setDisponivel(true); // Coloca o médico como disponível
                String msg = "O médico " + m.getNome() + " (" + m.getEspecialidade() + ") entrou ao serviço.";
                System.out.println(msg); // Mostra no ecrã
                ficheiros.escreverLog("Dia " + relogio.getDiaAtual() + " | Hora " + hora + ": " + msg); // Guarda no ficheiro
            }

            // Verificar Pausa
            m.setHorasSeguidasTrabalhadas(m.getHorasSeguidasTrabalhadas() + 1); // Incrementa as horas seguidas trabalhadas
            if (m.getHorasSeguidasTrabalhadas() >= 5 && m.isDisponivel()) {
                m.setDisponivel(false); // Medico entra em pausa (1 hora)
                String msg = "O médico " + m.getNome() + " (" + m.getEspecialidade() + ") entrou em pausa obrigatória.";
                System.out.println(msg);
                ficheiros.escreverLog("Dia " + relogio.getDiaAtual() + " | Hora " + hora + ": " + msg);
            }


            // Verificar Saída
            if (m.getHoraSaida() == hora && m.isDisponivel()) {
                m.setDisponivel(false); // Retira a disponibilidade
                String msg = "O médico " + m.getNome() + " (" + m.getEspecialidade() + ") saiu do serviço.";
                System.out.println(msg);
                ficheiros.escreverLog("Dia " + relogio.getDiaAtual() + " | Hora " + hora + ": " + msg);
            } else {
                System.out.println("O médico " + m.getNome() + " (" + m.getEspecialidade() + ") permanece em serviço.");
            }
        }

        // 4. Verificar alterações de urgência nos Utentes (Lógica do Aluno 2)
        boolean houveMudancas = gestao.verificarAlteracoesUrgencia();

        for (int i = 0; i < gestao.getNUtentes(); i++) {
            Modelo.Utente u = gestao.getUtenteAt(i);
            if (u.getNome().contains("[TRANSFERIDO]")) {
                ficheiros.escreverLog("Dia " + relogio.getDiaAtual() + " | Hora " + relogio.getHoraAtual() +
                        ": Utente " + u.getNome() + " excedeu o tempo limite e foi transferido/removido.");
                // Opcional: remover mesmo o utente da lista para não acumular
                gestao.removerUtente(u.getNumero());
                i--; // Ajustar índice após remoção
            }
        }
        if (houveMudancas){
            ficheiros.escreverLog("Dia " + relogio.getDiaAtual() + " | Hora " + hora +
                    ": Níveis de urgência atualizados devido ao tempo de espera.");
        }

        // Resumo final para o utilizador
        System.out.println("--------------------------------------------------");
        System.out.println("Dia: " + relogio.getDiaAtual() + " | Hora Atual: " + hora);

        if (!houveMudancas) {
            System.out.println("(Nenhuma alteração de urgência nos utentes registada nesta hora)");
        }
    }

    private void listarUtentes() {
        limparEcra();
        System.out.println("\n=== UTENTES EM SALA DE ESPERA ===");
        System.out.println("Lista de utentes atualmente em sala de espera:");
        if (gestao.getNUtentes() == 0) {
            System.out.println("Nenhum utente registado.");
            return;
        }
        for (int i = 0; i < gestao.getNUtentes(); i++) {
            System.out.println(gestao.getUtenteAt(i).toString());
        }

        int opcao = lerInteiro("\nEscolha um Utente (0 para voltar): ");
        //if para verificar se a opcao é valida e escolher o utente para efetuar a acao
        if (opcao > 0 && opcao <= gestao.getNUtentes()) {
            acaoUtente(gestao.getUtenteAt(opcao - 1));
        } else if (opcao != 0) {
            System.out.println("Erro: O número [" + opcao + "] não está na lista!");
        }
    }

    //classe para interagir com o utente selecionado
    private void acaoUtente(Utente u) {
        //implementar ações para o utente selecionado
        limparEcra();
        int opcao;

        System.out.println("=== AÇÃO UTENTE ===\n");
        System.out.println("Utente Selecionado: " + u.toString());
        System.out.println("1. Realizar Triagem");
        System.out.println("2. Encaminhar para Médico");

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
    }

    private void realizarTriagem(Utente u) {
        limparEcra();
        System.out.println("\n=== NOVA TRIAGEM ===");
        System.out.println("Utente: " + u.getNome() + " | Idade: " + u.getIdade());

        Modelo.Sintoma sintomaSelecionado = null;

        // Ciclo para garantir que o utilizador escolhe um sintoma válido
        while (sintomaSelecionado == null) {
            System.out.println("\n--- Pesquisa de Sintomas ---");
            System.out.println("Digite uma palavra-chave (ex: 'dor', 'febre') ou ENTER para ver todos:");
            String termo = scanner.nextLine().trim().toLowerCase();

            // Array temporário para guardar os resultados da pesquisa
            // (Usamos o tamanho total de sintomas como limite máximo seguro)
            Modelo.Sintoma[] resultados = new Modelo.Sintoma[gestao.getNSintomas()];
            int countResultados = 0;

            // 1. Filtrar sintomas baseados na pesquisa
            for (int i = 0; i < gestao.getNSintomas(); i++) {
                Modelo.Sintoma s = gestao.getSintomaAt(i);
                // Se termo vazio (ENTER), mostra tudo. Se não, verifica se contém a palavra.
                if (termo.isEmpty() || s.getNome().toLowerCase().contains(termo)) {
                    resultados[countResultados++] = s;
                }
            }

            // 2. Mostrar resultados numerados
            if (countResultados == 0) {
                System.out.println("Nenhum sintoma encontrado com '" + termo + "'. Tente novamente.");
            } else {
                System.out.println("\nEncontrados " + countResultados + " sintomas:");
                for (int i = 0; i < countResultados; i++) {
                    System.out.println("[" + (i + 1) + "] " + resultados[i].getNome() + " (" + resultados[i].getNivelUrgencia() + ")");
                }
                System.out.println("[0] Nova Pesquisa / Cancelar");

                // 3. Selecionar pelo número
                int escolha = lerInteiro("Selecione o número do sintoma: ");

                if (escolha > 0 && escolha <= countResultados) {
                    sintomaSelecionado = resultados[escolha - 1]; // -1 porque o array começa em 0
                } else if (escolha == 0) {
                    // Volta ao início do loop para pesquisar outra vez
                    continue;
                } else {
                    System.out.println("Opção inválida!");
                }
            }
        }

        // 4. Gravar dados no Utente
        u.setSintoma(sintomaSelecionado.getNome());
        u.setNivelUrgencia(sintomaSelecionado.getNivelUrgencia());
        u.resetarTempoEspera(); // O tempo começa a contar agora para subir de nível

        System.out.println("\nTriagem concluída com sucesso!");
        System.out.println("Sintoma: " + u.getSintoma());
        System.out.println("Nível Atribuído: " + u.getNivelUrgencia());

        // 5. Escrever no Log (Requisito obrigatório)
        ficheiros.escreverLog("Dia " + relogio.getDiaAtual() + " | Hora " + relogio.getHoraAtual() +
                ": Utente " + u.getNome() + " (Nº" + u.getNumero() + ") realizou triagem. Sintoma: " +
                u.getSintoma() + " -> Nível: " + u.getNivelUrgencia());
    }

    private void encaminharMedico(Utente u) {
        limparEcra();
        System.out.println("\n=== ENCAMINHAR PARA MÉDICO ===");

        // 1. Validação de Segurança
        // Não faz sentido encaminhar alguém que ainda não sabemos o que tem (Pendente)
        if (u.getSintoma().equals("Pendente")) {
            System.out.println("AVISO: O utente ainda não fez a triagem.");
            System.out.println("Realize a triagem primeiro (Opção 1) para determinar a urgência.");
            return;
        }

        System.out.println("Utente: " + u.getNome());
        System.out.println("Sintoma: " + u.getSintoma() + " | Urgência: " + u.getNivelUrgencia());
        System.out.println("--------------------------------------------------");

        // 2. Procurar o sintoma para saber quais as especialidades permitidas
        String[] especialidadesPermitidas = null;
        for (int i = 0; i < gestao.getNSintomas(); i++) {
            Modelo.Sintoma s = gestao.getSintomaAt(i);
            if (s != null && s.getNome().equalsIgnoreCase(u.getSintoma())) {
                // Aqui guardamos o ARRAY de códigos
                especialidadesPermitidas = s.getCodigoEspecialidade();
                break;
            }
        }

        if (especialidadesPermitidas == null) {
            System.out.println("Erro: Sintoma não encontrado no sistema.");
            return;
        }

        // 3. Procurar um médico que tenha UMA das especialidades permitidas
        Modelo.Medico medicoEncontrado = null;

        for (int i = 0; i < gestao.getNMedicos(); i++) {
            Modelo.Medico m = gestao.getMedicoAt(i);

            if (m != null && m.isDisponivel()) {
                // Como o sintoma tem várias especialidades, temos de ver se a do médico está lá
                for (String codEsp : especialidadesPermitidas) {
                    if (m.getEspecialidade().equalsIgnoreCase(codEsp)) {
                        medicoEncontrado = m;
                        break;
                    }
                }
            }
            if (medicoEncontrado != null) break;
        }

        // 4. Se não encontrar nenhum médico
        if (medicoEncontrado == null) {
            System.out.println("Não existem médicos de " + medicoEncontrado.getEspecialidade() + " disponíveis de momento.");
            return;
        }

        System.out.println("Médico sugerido: Dr(a). " + medicoEncontrado.getNome() + " [" + medicoEncontrado.getEspecialidade() + "]");
        String confirmacao = lerString("Confirmar encaminhamento para consultório? (S/N): ");

        if (confirmacao.equalsIgnoreCase("S")) {

            // 2. Ação Principal: Remover da Sala de Espera
            // (Assumimos que o Aluno 3 garante a lógica de qual médico atende.
            // A tua parte é garantir que ele sai da lista de espera e fica registado).

            medicoEncontrado.setDisponivel(false);

            boolean removido = gestao.removerUtente(u.getNumero());

            if (removido) {
                System.out.println("\nUtente encaminhado com sucesso!");

                // 3. LOG DE SAÍDA (Obrigatório para o Histórico)
                // Isto fecha o ciclo: Entrada -> Triagem -> Saída
                ficheiros.escreverLog("Dia " + relogio.getDiaAtual() + " | Hora " + relogio.getHoraAtual() +
                        ": Utente " + u.getNome() + " (Nº" + u.getNumero() + ") foi atendido e saiu da sala de espera.");

            } else {
                System.out.println("Erro: Não foi possível remover o utente da lista (pode já ter saído).");
            }
        } else {
            System.out.println("Operação cancelada.");
        }
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
                    // SIMULAÇÃO: Gera um número aleatório entre 5 e 15 para parecer real
                    double mediaSimulada = 5 + (Math.random() * 10);
                    System.out.println("\n--- ESTATÍSTICA (PREVISÃO) ---");
                    System.out.printf("Média diária de utentes atendidos: %.1f utentes/dia\n", mediaSimulada);
                    System.out.println("(Nota: Cálculo real será implementado pelo Aluno 2)");
                    pausar();
                    break;
                case 2:
                    // SIMULAÇÃO: Mostra dados estáticos
                    System.out.println("\n--- TOP 3 ESPECIALIDADES (DADOS HISTÓRICOS) ---");
                    System.out.println("1. Cardiologia (45% dos casos)");
                    System.out.println("2. Pediatria   (30% dos casos)");
                    System.out.println("3. Ortopedia   (25% dos casos)");
                    System.out.println("(Nota: Dados em tempo real serão implementados pelo Aluno 2)");
                    pausar();
                    break;
                case 3:
                    System.out.println("--- LOGS DO SISTEMA ---");
                    ficheiros.lerLogs(); // Este é o único real e já funciona!
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
            System.out.println("║                       CONFIGURAÇÕES                          ║");
            System.out.println("╠══════════════════════════════════════════════════════════════╣");
            System.out.println("║ 1. Alterar Caminho dos Ficheiros                             ║");
            System.out.println("║ 2. Alterar Tempos de Consulta                                ║");
            System.out.println("║ 3. Alterar Limites de Espera (Mudança de Nível)              ║");
            System.out.println("║ 4. Alterar Password                                          ║");
            System.out.println("║ 5. Alterar Separador dos Ficheiros                           ║");
            System.out.println("║ 6. Alterar Regras de Descanso (Médicos)                      ║");
            System.out.println("║ 7. Listar Todas as Regras Atuais                             ║");
            System.out.println("║ 0. Voltar                                                    ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");

            opcao = lerInteiro("Opção: ");

            switch (opcao) {
                case 1:
                    System.out.print("Novo Caminho (Atual: " + Configuracoes.getCaminhoficheiro() + "): ");
                    String novoCaminho = scanner.nextLine().trim();

                    if (!novoCaminho.isEmpty()) {
                        // 1. Adicionar a barra final se faltar
                        if (!novoCaminho.endsWith("/") && !novoCaminho.endsWith("\\")) {
                            novoCaminho += "/";
                        }

                        // 2. Criar referências para a pasta antiga e nova
                        File pastaAntiga = new File(Configuracoes.getCaminhoficheiro());
                        File pastaNova = new File(novoCaminho);

                        // 3. Verificar se a pasta antiga realmente existe
                        if (pastaAntiga.exists()) {
                            // Tenta MOVER (Renomear) a pasta
                            boolean sucesso = pastaAntiga.renameTo(pastaNova);

                            if (sucesso) {
                                Configuracoes.setCaminhoficheiro(novoCaminho);
                                System.out.println("Sucesso: A pasta foi movida de '" + pastaAntiga.getName() + "' para '" + pastaNova.getName() + "'.");
                                try {
                                    ficheiros.guardarConfiguracoes();
                                } catch (IOException e) {
                                    System.out.println("Erro ao atualizar ficheiro de config: " + e.getMessage());
                                }
                            } else {
                                System.out.println("Erro: Não foi possível mover a pasta (verifique permissões ou se o nome já existe).");
                            }
                        } else {
                            // Se a pasta antiga não existe (primeira vez a correr), apenas muda o caminho
                            Configuracoes.setCaminhoficheiro(novoCaminho);
                            System.out.println("Caminho alterado (a pasta antiga não existia para ser movida).");
                        }

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
                    String novaPasse = scanner.nextLine().trim();

                    if (!novaPasse.isEmpty()) {
                        System.out.println("Confirme a nova Password: ");
                        String confirmacao = scanner.nextLine().trim();
                        if (novaPasse.equals(confirmacao)) {
                            Configuracoes.setPassword(novaPasse);
                            System.out.println("Password Atualizada!");
                        } else {
                            System.out.println("Erro: As passwords não coincidem. Operação cancelada.");
                        }
                    } else {
                        System.out.println("Password mantida.");
                    }
                    pausar();
                    break;

                case 5:
                    System.out.print("Novo Separador (Atual: " + Configuracoes.getSeparadorFicheiro() + "): ");
                    String novoSep = scanner.nextLine().trim();

                    if (!novoSep.isEmpty()) {
                        // 1. Guardar o separador antigo (caso dê erro)
                        String separadorAntigo = Configuracoes.getSeparadorFicheiro();

                        try {
                            // 2. Definir o novo separador na memória
                            Configuracoes.setSeparadorFicheiro(novoSep);

                            System.out.println("A converter ficheiros para o novo formato...");

                            // 3. REESCREVER todos os dados (Médicos, Sintomas, etc) com o NOVO separador
                            ficheiros.guardarTudo(gestao);

                            // 4. Guardar a configuração
                            ficheiros.guardarConfiguracoes();

                            System.out.println("Sucesso! Separador alterado e ficheiros convertidos.");

                        } catch (IOException e) {
                            System.out.println("Erro grave ao converter ficheiros: " + e.getMessage());
                            System.out.println("A reverter para o separador antigo...");
                            Configuracoes.setSeparadorFicheiro(separadorAntigo);
                        }
                    } else {
                        System.out.println("Mantido o anterior.");
                    }
                    pausar();
                    break;

                case 6:
                    System.out.println("\n--- Regras de Descanso dos Médicos ---");

                    int horasTrab = lerIntAlterar("Horas de trabalho seguidas antes do descanso", Configuracoes.getHorasTrabalhoParaDescanso());
                    Configuracoes.setHorasTrabalhoParaDescanso(horasTrab);

                    int tempoDesc = lerIntAlterar("Duração do descanso (unidades de tempo)", Configuracoes.getTempoDescanso());
                    Configuracoes.setTempoDescanso(tempoDesc);

                    System.out.println("Regras de descanso atualizadas!");
                    pausar();
                    break;

                case 7:
                    limparEcra();
                    System.out.println("╔══════════════════════════════════════════════════════════════╗");
                    System.out.println("║                  REGRAS ATUAIS DO SISTEMA                    ║");
                    System.out.println("╠══════════════════════════════════════════════════════════════╣");

                    System.out.println("FICHEIROS");
                    System.out.println("   • Caminho:   " + Configuracoes.getCaminhoficheiro());
                    System.out.println("   • Separador: " + Configuracoes.getSeparadorFicheiro());
                    System.out.println("   • Medicos:   " + Configuracoes.getNomeFicheiroMedicos());
                    System.out.println("   • Sintomas:  " + Configuracoes.getNomeFicheiroSintomas());
                    System.out.println("   • Especialidades:  " + Configuracoes.getNomeFicheiroEspecialidade());
                    System.out.println("--------------------------------------------------------------");

                    System.out.println("TEMPOS DE CONSULTA (Unidades de Tempo)");
                    System.out.println("   • Baixa:     " + Configuracoes.getTempoConsultaBaixa());
                    System.out.println("   • Média:     " + Configuracoes.getTempoConsultaMedia());
                    System.out.println("   • Urgente:   " + Configuracoes.getTempoConsultaUrgente());
                    System.out.println("--------------------------------------------------------------");

                    System.out.println("LIMITES DE ESPERA (Para subir de nível)");
                    System.out.println("   • Verde -> Amarelo:    " + Configuracoes.getLimiteEsperaVerdeParaAmarelo());
                    System.out.println("   • Amarelo -> Vermelho: " + Configuracoes.getLimiteEsperaAmareloParaVermelho());
                    System.out.println("   • Vermelho -> Saída:   " + Configuracoes.getLimiteEsperaVermelhoSaida());
                    System.out.println("--------------------------------------------------------------");

                    System.out.println("REGRAS DE DESCANSO");
                    System.out.println("   • Trabalha:  " + Configuracoes.getHorasTrabalhoParaDescanso() + " horas seguidas");
                    System.out.println("   • Descansa:  " + Configuracoes.getTempoDescanso() + " unidades de tempo");
                    System.out.println("--------------------------------------------------------------");

                    System.out.println("PASSWORD");
                    System.out.println("   • Password:  " + Configuracoes.getPassword());

                    System.out.println("╚══════════════════════════════════════════════════════════════╝");
                    pausar();
                    break;

                case 0:
                    String resposta = lerString("Deseja guardar as novas configurações? (S/N): ");

                    if (resposta.equalsIgnoreCase("S")) {
                        try {
                            System.out.println("A guardar configurações...");
                            ficheiros.guardarConfiguracoes();
                            System.out.println("Configurações guardadas!");
                        } catch (IOException e) {
                            System.out.println("Erro ao guardar: " + e.getMessage());
                        }
                    } else {
                        System.out.println("Alterações descartadas (não serão lembradas na próxima vez).");
                    }
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
