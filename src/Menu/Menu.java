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

/**
 * Classe Menu onde se inicializa os componentes principais (menus, gestão)
 * Trabalho realizado pelo grupo HOSP-NGTD
 */
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
    /** --- MENU INICIAL --- */
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

    /** --- MENU GESTÃO DE DADOS --- */
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

    /** --- MENU GESTÃO DE MÉDICOS --- */
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
        String nome = lerString("Nome do Médico: ");
        String codEsp = lerString("Código da Especíalidade (ex: CARD): ").toUpperCase();
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
        String nome = lerString("Nome do Médico: ");

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
        String nome = lerString("Nome do medico a remover: ");

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

    /**--- MENU GESTÃO DE ESPECIALIDADES ---*/
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
        String codigo = lerString("Código (ex: CARD): ").toUpperCase();
        String nome = lerString("Nome (ex: Cardiologia): ");

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
        String codAtual = lerString("Código da especialidade a alterar: ").toUpperCase();

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
        String codigo = lerString("Código da especialidade a remover: ").toUpperCase();

        String cond = lerString("Tem a certeza? (S/N): ");
        if (!cond.equalsIgnoreCase("S")) return;

        boolean ok = gestao.removerEspecialidade(codigo);
        if (ok) {
            System.out.println("Especialidade removida.");
        } else {
            System.out.println("Erro: Especialidade não encontrada.");
        }
    }

    /** --- MENU GESTÃO DE SINTOMAS --- */
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
        String nome = lerString("Nome do Sintoma: ");

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
        String nome = lerString("Nome do Sintoma a alterar: ");

        if (gestao.procurarSintomaPorNome(nome) == null) {
            System.out.println("Sintoma não encontrado.");
            return;
        }

        System.out.println("1. Alterar Nível de Urgência");
        System.out.println("2. Alterar Especialidades Associadas");
        System.out.println("0. Cancelar");
        int op = lerInteiro("Escolha: ");

        if (op == 1) {
            String novoNivel = lerString("Novo Nível (Verde/Amarelo/Vermelho): ");
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
        String nome = lerString("Nome do Sintoma: ");

        String conf = lerString("Tem a certeza? (S/N): ");
        if (!conf.equalsIgnoreCase("S")) return;

        if (gestao.removerSintoma(nome)) {
            System.out.println("Sintoma removido.");
        } else {
            System.out.println("Erro: Sintoma não encontrado.");
        }
    }

    /** --- MENU FUNCIONAMENTO DO HOSPITAL --- */
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

    /**
     * Responsável pelo registo de um novo utente no balcão de admissão.
     * Recolhe os dados básicos (Nome, Idade) e cria a ficha de utente com estado "Pendente".
     */
    private void admitirUtente() {
        System.out.println("\n=== ADMITIR UTENTE ===");
        String nome = lerString("Nome do Utente: ");
        int idade = lerInteiro("Idade do Utente: ");

        Utente u = gestao.admitirUtenteSimples(nome, idade);

        if (u != null) {
           registarEvento("Novo Utente admitido: " + u.getNome() + " (Nº" + u.getNumero() + ")");
        } else {
            System.out.println("Erro ao admitir utente.");
        }
    }

    /**
     * Avança uma unidade de tempo (1 hora).
     * Para alem de avançar o relogio do hospital verifica mudanças de turno dos médicos
     * Verifica se algum utente deve subir de nivel de urgência.
     * Remove da lista de espera utentes que foram transferidos por excesso de tempo.
     * Tudo é registado no ficheiro log.
     */
    private void avancarTempo() {
        limparEcra();
        System.out.println("\n--- A AVANÇAR O TEMPO ---");

        relogio.avancarTempo();
        int hora = relogio.getHoraAtual();

        if (relogio.getHoraAtual() == 1) {
            ficheiros.escreverLog("=== INÍCIO DO DIA " + relogio.getDiaAtual() + " ===");
        }

        String[] notificacoesMedicos = gestao.verificarTurnosMedicos(hora);

        for (int i = 0; i < notificacoesMedicos.length; i++) {
            String msg = notificacoesMedicos[i];
            registarEvento(msg);
        }

        boolean houveMudancas = gestao.verificarAlteracoesUrgencia();

        if (houveMudancas) {
            registarEvento("Níveis de urgência atualidazos e transferências verificadas.");

            // verificar se algum utente foi marcado como transferido

            for (int i = 0; i < gestao.getNUtentes(); i++) {
                Utente u = gestao.getUtenteAt(i);

                if (u.getNome().contains("[TRANSFERIDO]")) {
                    boolean removido = gestao.removerUtente(u.getNumero());
                    if (removido) {
                        i--;
                    }
                }
            }
            System.out.println("--------------------------------------------------");
            System.out.println("📅 Dia: " + relogio.getDiaAtual() + " | 🕒 Hora Atual: " + hora + "h");

            if (notificacoesMedicos.length == 0 && !houveMudancas) {
                System.out.println("(Hora tranquila: Nenhuma alteração de turno ou urgência registada)");
            }
        }
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
        } else if (opcao != 0) {
            System.out.println("Erro: O número [" + opcao + "] não está na lista!");
        }
    }

    private void acaoUtente(Utente u) {
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

    /**
     * Realiza a triagem para um utente que se encontra na admissão.
     * Pesquisa e seleciona um sintoma válido da lista da gestão.
     * Atribui o sintoma e o nível de urgência ao utente.
     * Reinicia o contador de tempo de espera do utente
     * @param u O objeto Utente que será submetido à triagem.
     */
    private void realizarTriagem(Utente u) {
        limparEcra();
        System.out.println("\n=== NOVA TRIAGEM: " + u.getNome() + " ===");

        Modelo.Sintoma sintomaSelecionado = null;

        while (sintomaSelecionado == null) {
            System.out.println("Digite uma palavra-chave (ex: 'dor', 'febre') ou ENTER para ver todos:");
            String termo = scanner.nextLine().trim();

            Modelo.Sintoma[] resultados = gestao.pesquisarSintomas(termo);

            if (resultados.length == 0) {
                System.out.println("Nenhum sintoma encontrado com '" + termo + "'. Tente novamente.");
            } else {
                for (int i = 0; i < resultados.length; i++) {
                    System.out.println("[" + (i+1) + "]" + resultados[i].getNome() + " (" + resultados[i].getNivelUrgencia() + ")");
                }
                System.out.println("[0] Nova Pesquisa / Cancelar");

                int escolha = lerInteiro("Selecione o numero: ");
                if (escolha > 0 && escolha <= resultados.length) {
                    sintomaSelecionado = resultados[escolha - 1];
                }
            }
        }

        u.setSintoma(sintomaSelecionado.getNome());
        u.setNivelUrgencia(sintomaSelecionado.getNivelUrgencia());
        u.resetarTempoEspera();

        System.out.println("\n--------------------------------");
        System.out.println("Sintoma: " + u.getSintoma());
        System.out.println("Nível Atribuído: " + u.getNivelUrgencia());
        System.out.println("--------------------------------");

        registarEvento("Triagem concluída: " + u.getNome() + "Nº" + u.getNumero() +
                ") classificado com urgência " + u.getNivelUrgencia().toUpperCase());
    }

    /**
     * Encaminhar um utente da sala de espera para o consultório médico.
     * Verifica se o utente já tem uma triagem feita e depois de confirmar,
     * remove-o da lista de espera e regista a saida no ficheiro log.
     * @param u O objeto Utente a ser encaminhado.
     */
    private void encaminharMedico(Utente u) {
        limparEcra();
        System.out.println("\n=== ENCAMINHAR PARA MÉDICO ===");

        if (u.getSintoma().equals("Pendente")) {
            System.out.println("AVISO: O utente ainda não fez a triagem.");
            return;
        }

        System.out.println("Utente: " + u.getNome());
        System.out.println("Sintoma: " + u.getSintoma() + " | Urgência: " + u.getNivelUrgencia());
        System.out.println("--------------------------------------------------");

        String confirmacao = lerString("Confirmar encaminhamento para consultório? (S/N): ");

        if (confirmacao.equalsIgnoreCase("S")) {

            boolean removido = gestao.removerUtente(u.getNumero());

            if (removido) {
                registarEvento("Utente " + u.getNome() + " (Nº" + u.getNumero() + ") foi encaminhado para o médico e saiu da sala de espera.");
            } else {
                System.out.println("Erro: Não foi possível remover o utente da lista (pode já ter saído).");
            }
        } else {
            System.out.println("Operação cancelada.");
        }
    }

    /** --- MENU ESTATÍSTICAS E LOGS --- */
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

    /**
     * Menu dedicado à gestão das configurações da aplicação.
     * Permite alterar caminhos de ficheiros, tempos de consulta, limites de espera
     * e regras de descanso, etc.
     */
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

                    if (ficheiros.mudarDiretorioDados(novoCaminho)) {
                        System.out.println("Caminho alterado com sucesso para :" + Configuracoes.getCaminhoficheiro());

                        try {
                            ficheiros.guardarConfiguracoes();
                        } catch (IOException e ){
                            System.out.println("Erro ao guardar as configurações: " + e.getMessage());
                        }

                    } else {
                        System.out.println("Erro: Não foi possivel mover a pasta ou o caminho é inválido.");
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
                        String separadorAntigo = Configuracoes.getSeparadorFicheiro();

                        try {
                            Configuracoes.setSeparadorFicheiro(novoSep);
                            System.out.println("A converter ficheiros para o novo formato...");
                            ficheiros.guardarTudo(gestao);
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


    /** --- MENU PARA SAIR --- */
    private void menuSair () {
        String resposta = lerString("Deseja guardar as alterações antes de sair? (S/N): ");
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

    /** --- MÉTODOS AUXILIARES --- */

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

    /**
     * Metodo auxiliar para os menus ALTERAR os dados.
     * Diferente do metodo lerString, este metodo aceita uma entrada vazia
     * o que é interpretado pelo sistama como "manter o valor atual" e não altera o dado.
     * @param mensagem A mensagem indicando o campo a alterar.
     * @return A nova String inserida ou uma String vazia.
     */
    private String lerStringAlterar(String mensagem) {
        System.out.println(mensagem);
        return scanner.nextLine().trim();
    }

    /**
     * Metodo auxiliar para ALTERAR valores inteiros.
     * Apresenta o valor atual ao utilizador. Se a entrada for vazia o sistema intrepreta como 'manter o valor atual.
     * se for inserido um numero valido, esse valor é retornado.
     * @param mensagem A descrição do campo
     * @param valorAtual O valor que o campo tem atualmente.
     * @return O novo valor inserido ou o valor original.
     */
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

    /**
     * Recolhe uma lista dinamica de códigos de especialidades inseridos pelo utilizador.
     * Valida se cada codigo inserido existe na gestão.
     * O ciclo termina quando o utilizador escreve "FIM" ou atinge o limite do array temporário.
     * Garante que não são associadas especialidades inexistentes a sintomas.
     * @return Um array de String contendo apenas os códigos de especialidades válidos.
     */
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

    /**
     * Realiza a autenticação do utilizador para acesso a áreas sensiveis.
     * Compara o input do utilizador com a password armazenada nas configurações.
     * Permite cancelar a operação pressionando ENTER sem escrever nada.
     * @return true se a password estiver correta, false se o utilizador cancelar ou falhar.
     */
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

    /**
     * Metodo auxiliar para reutilizar o registo de eventos.
     * Apresenta a mensagem na consola para o utilizador ver no momento.
     * Grava a mensagem no ficheiro de logs com
     * @param mensagem A descrição do evento a ser registado.
     */
    private void registarEvento(String mensagem) {
        System.out.println(mensagem);

        String logEntrada = "Dia " + relogio.getDiaAtual() + " | Hora " + relogio.getHoraAtual() + ": " + mensagem;
        ficheiros.escreverLog(logEntrada);
    }
}
