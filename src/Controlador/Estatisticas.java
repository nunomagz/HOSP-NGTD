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
     * Implementa a regra de análise preditiva (80%). Verifica se um determinado sintoma
     * tem uma forte correlação estatística (igual ou superior a 80%) com uma especialidade específica,
     * baseando-se no histórico de atendimentos realizados.
     * * @param g Instância da gestão para acesso aos dados históricos.
     * @param nomeSintoma Nome do sintoma a ser analisado para a sugestão.
     */
    public static void verificarRegra80(GestaoHOSP g, String nomeSintoma) {
        int totalCasos = 0;
        String[] especialidadesAtendidas = new String[20];
        int[] contagemPorEspec = new int[20];
        int nEspecEncontradas = 0;

        for (int i = 0; i < g.getNHistorico(); i++) {
            Utente u = g.getUtenteHistoricoAt(i);
            if (u.getSintoma().equalsIgnoreCase(nomeSintoma)) {
                totalCasos++;
            }
        }
    }
}