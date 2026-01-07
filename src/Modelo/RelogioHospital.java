package Modelo;

/**
 * Classe responsável pela gestão do tempo do hospital.
 * Controla a hora atual (1 a 24) e o dia atual.
 * Não contém lógica de negócio do hospital, apenas o tempo.
 */
public class RelogioHospital {

    // Hora atual do sistema (vai de 1 a 24)
    private int horaAtual;

    // Dia atual da simulação (começa em 1 e vai aumentando)
    private int diaAtual;

    /**
     * Construtor do relógio.
     * Inicializa o sistema no dia 1, hora 1.
     */
    public RelogioHospital() {
        horaAtual = 1;
        diaAtual = 1;
    }

    /**
     * Avança o tempo uma unidade.
     *
     * - Se a hora for menor que 24, apenas incrementa a hora.
     * - Se a hora for 24, reinicia para 1 e avança o dia.
     */
    public void avancarTempo() {

        // Caso ainda não seja o final do dia
        if (horaAtual < 24) {
            horaAtual++;
        }
        // Caso seja a última hora do dia
        else {
            horaAtual = 1;
            diaAtual++;

            // Mensagem informativa para o utilizador
            System.out.println("🔄 Novo dia iniciado: Dia " + diaAtual);
        }
    }

    /**
     * Devolve a hora atual do sistema.
     * @return hora atual (1 a 24)
     */
    public int getHoraAtual() {
        return horaAtual;
    }

    /**
     * Devolve o dia atual da simulação.
     * @return dia atual
     */
    public int getDiaAtual() {
        return diaAtual;
    }
}
