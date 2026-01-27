package Controlador;

import Gestao.GestaoHOSP;
import Modelo.Utente;
import Modelo.Medico;
import Modelo.Sintoma;

/**
 * Classe responsável pela análise de dados e geração de relatórios estatísticos do hospital.
 * Centraliza o processamento do histórico de utentes e cálculos de desempenho financeiro e operacional.
 * * @author Aluno 2
 */
public class Estatisticas {

    /**
     * Calcula a média de produtividade do hospital com base nos utentes que passaram pela triagem
     * ou foram transferidos, dividindo pelo total de dias decorridos na simulação.
     * * @param g Instância da gestão que contém o histórico de utentes.
     * @param diaAtual O dia atual do relógio do sistema para servir de divisor.
     * @return Valor decimal representando a média de atendimentos por dia. Devolve 0 se o dia for inválido.
     */
    public static double calcularMediaDiaria(GestaoHOSP g, int diaAtual) {
        if (diaAtual <= 0) return 0.0;

        int contagemAtendidos = 0;
        for (int i = 0; i < g.getNHistorico(); i++) {
            Utente u = g.getUtenteHistoricoAt(i);
            if (u.getNome().contains("[ATENDIDO]") || u.getNome().contains("[TRANSFERIDO]")) {
                contagemAtendidos++;
            }
        }
        return (double) contagemAtendidos / diaAtual;
    }

    /**
     * Percorre a lista de médicos e calcula o rendimento total diário de cada um,
     * multiplicando o valor/hora pelo período total do turno (saída - entrada).
     * Trata turnos que atravessam a meia-noite (ex: entrada 22h, saída 06h).
     * * @param g Instância da gestão para aceder ao array de médicos registados.
     */
    public static void exibirTabelaSalarios(GestaoHOSP g) {
        System.out.println("\n--- TABELA DE SALÁRIOS DIÁRIOS ---");
        for (int i = 0; i < g.getNMedicos(); i++) {
            Medico m = g.getMedicoAt(i);

            int horas = m.getHoraSaida() - m.getHoraEntrada();
            if (horas < 0) horas += 24;

            int total = horas * m.getSalario();
            System.out.println("Dr(a). " + m.getNome() + " | Especialidade: " + m.getEspecialidade() + " | Total: " + total + "€");
        }
    }

    /**
     * Analisa o histórico global para contar a frequência de cada sintoma catalogado.
     * Cruza os sintomas existentes no sistema com os registos de sintomas nos utentes do histórico.
     * * @param g Instância da gestão com os sintomas base e o histórico de utentes.
     */
    public static void exibirFrequenciaSintomas(GestaoHOSP g) {
        System.out.println("\n--- FREQUÊNCIA DE SINTOMAS (HISTÓRICO) ---");
        for (int i = 0; i < g.getNSintomas(); i++) {
            Sintoma s = g.getSintomaAt(i);
            int contador = 0;

            for (int j = 0; j < g.getNHistorico(); j++) {
                if (g.getUtenteHistoricoAt(j).getSintoma().equalsIgnoreCase(s.getNome())) {
                    contador++;
                }
            }

            if (contador > 0) {
                System.out.println("Sintoma: " + s.getNome() + " | Casos: " + contador);
            }
        }
    }

    /**
     * Analisa o histórico para encontrar a especialidade que mais atendeu um sintoma.
     * Se essa especialidade representar 80% ou mais dos atendimentos, sugere o catálogo.
     */
    public static void verificarRegra80(GestaoHOSP g, String nomeSintoma) {
        int totalCasosSintoma = 0;
        String[] especialidadesAtendidas = new String[20];
        int[] contagemPorEspec = new int[20];
        int nEspecEncontradas = 0;

        // 1. Contar atendimentos por especialidade para este sintoma no histórico
        for (int i = 0; i < g.getNHistorico(); i++) {
            Utente u = g.getUtenteHistoricoAt(i);
            if (u.getSintoma().equalsIgnoreCase(nomeSintoma)) {
                totalCasosSintoma++;
                // Extrair especialidade do log ou do médico que atendeu (simplificado pelo histórico)
                // Aqui assumimos que o utente guardou a especialidade que o atendeu no histórico
                String especQueAtendeu = extrairEspecialidadeDoHistorico(u);

                if (especQueAtendeu != null) {
                    int idx = -1;
                    for (int j = 0; j < nEspecEncontradas; j++) {
                        if (especialidadesAtendidas[j].equals(especQueAtendeu)) {
                            idx = j;
                            break;
                        }
                    }
                    if (idx != -1) {
                        contagemPorEspec[idx]++;
                    } else if (nEspecEncontradas < 20) {
                        especialidadesAtendidas[nEspecEncontradas] = especQueAtendeu;
                        contagemPorEspec[nEspecEncontradas] = 1;
                        nEspecEncontradas++;
                    }
                }
            }
        }

        if (totalCasosSintoma == 0) {
            System.out.println("Sem histórico suficiente para o sintoma: " + nomeSintoma);
            return;
        }

        // 2. Verificar se alguma especialidade atinge o limiar de 80%
        for (int i = 0; i < nEspecEncontradas; i++) {
            double percentagem = (double) contagemPorEspec[i] / totalCasosSintoma;
            if (percentagem >= 0.8) {
                System.out.println("💡 SUGESTÃO (Regra 80%): O sintoma '" + nomeSintoma +
                        "' é atendido em " + (percentagem * 100) +
                        "% dos casos por " + especialidadesAtendidas[i] + ".");
                return;
            }
        }
        System.out.println("O sintoma '" + nomeSintoma + "' não tem uma especialidade predominante (>=80%).");
    }

    /**
     * Calcula e exibe as 3 especialidades com maior volume de pacientes atendidos.
     */
    public static void exibirTop3Especialidades(GestaoHOSP g) {
        int totalAtendidos = g.getNHistorico();
        if (totalAtendidos == 0) {
            System.out.println("Ainda não existem utentes atendidos no histórico.");
            return;
        }

        // Criar arrays para contar pacientes por especialidade existente
        String[] nomesEspecs = new String[g.getNEspecialidades()];
        int[] contagens = new int[g.getNEspecialidades()];

        for (int i = 0; i < g.getNEspecialidades(); i++) {
            String cod = g.getEspecialidadeAt(i).getCodigo();
            nomesEspecs[i] = cod;

            for (int j = 0; j < g.getNHistorico(); j++) {
                Utente u = g.getUtenteHistoricoAt(j);
                if (u.getEspecialidadeAtendimento() != null &&
                        u.getEspecialidadeAtendimento().equalsIgnoreCase(cod)) {
                    contagens[i]++;
                }
            }
        }

        // Ordenar (Bubble Sort) para obter os maiores valores no início
        for (int i = 0; i < nomesEspecs.length - 1; i++) {
            for (int j = 0; j < nomesEspecs.length - i - 1; j++) {
                if (contagens[j] < contagens[j + 1]) {
                    // Trocar contagem
                    int tempC = contagens[j];
                    contagens[j] = contagens[j + 1];
                    contagens[j + 1] = tempC;
                    // Trocar nome
                    String tempN = nomesEspecs[j];
                    nomesEspecs[j] = nomesEspecs[j + 1];
                    nomesEspecs[j + 1] = tempN;
                }
            }
        }

        System.out.println("\n--- TOP 3 ESPECIALIDADES MAIS PROCURADAS ---");
        for (int i = 0; i < 3 && i < nomesEspecs.length; i++) {
            double percentagem = ((double) contagens[i] / totalAtendidos) * 100;
            System.out.printf("%d. %s: %d pacientes (%.2f%%)\n", (i + 1), nomesEspecs[i], contagens[i], percentagem);
        }
    }

    /**
     * Metodo auxiliar para identificar qual especialidade atendeu o utente.
     * Baseia-se no campo que adicionámos ao Utente ou no nome/log.
     */
    private static String extrairEspecialidadeDoHistorico(Utente u) {
        // Se usaste o campo novo no Utente:
        return u.getEspecialidadeAtendimento();
    }
}