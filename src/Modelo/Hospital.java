package Modelo;

/**
 * Classe central do sistema.
 * Gere o funcionamento diário do hospital.
 */
public class Hospital {

    // Relógio do hospital
    private RelogioHospital relogio;

    // Array de médicos
    private Medico[] medicos;
    private int totalMedicos;

    // Array de utentes
    private Utente[] utentes;
    private int totalUtentes;

    /**
     * Construtor do hospital.
     */
    public Hospital(/*int maxMedicos, int maxUtentes*/) {
        relogio = new RelogioHospital();
//        medicos = new Medico[maxMedicos];
//        utentes = new Utente[maxUtentes];
//        totalMedicos = 0;
//        totalUtentes = 0;
    }

    // Adiciona um médico ao hospital
//    public void adicionarMedico(Medico m) {
//        medicos[totalMedicos++] = m;
//    }

    // Adiciona um paciente à sala de espera
//    public void adicionarPaciente(Paciente p) {
//        pacientes[totalPacientes++] = p;
//    }

    /**
     * Avança o tempo e processa eventos associados.
     */
    public void avancarTempo() {
        relogio.avancarTempo();
//        processarMedicos();
    }

    /**
     * Processa entradas e saídas de médicos consoante a hora.
     */
//    private void processarMedicos() {
//        int hora = relogio.getHoraAtual();
//
//        for (int i = 0; i < totalMedicos; i++) {
//            Medico m = medicos[i];

            // Entrada do médico
//            if (m.getHoraEntrada() == hora &&
//                    m.getEstado() == EstadoMedico.FORA_SERVICO) {
//
//                m.setEstado(EstadoMedico.DISPONIVEL);
//                System.out.println("👨‍⚕️ Médico " + m.getNome() + " entrou em serviço");
//            }

            // Saída do médico (se estiver disponível)
//            if (m.getHoraSaida() == hora &&
//                    m.getEstado() == EstadoMedico.DISPONIVEL) {
//
//                m.setEstado(EstadoMedico.FORA_SERVICO);
//                System.out.println("🚪 Médico " + m.getNome() + " saiu de serviço");
//            }
//        }
//    }

    // Mostra a hora atual do sistema
//    public void mostrarHora() {
//        System.out.println(
//                "Dia " + relogio.getDiaAtual() + " | Hora " + relogio.getHoraAtual()
//        );
//    }
}
