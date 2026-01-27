package Controlador;

import Configuracoes.Configuracoes;
import Modelo.Medico;
import Modelo.NivelUrgencia;
import Modelo.Sintoma;
import Modelo.Utente;

public class Calculos {
    /**
     * Determina a especialidade de encaminhamento com base nos sintomas.
     * Regra 1: Sintomas com maior urgência têm prioridade absoluta.
     * Regra 2: Dentro do nível de urgência vencedor, ganha a especialidade mais frequente.
     * * @param sintomas O array de objetos Sintoma do utente.
     * @param qtdSintomas O número real de sintomas no array (para ignorar posições null).
     * @return O código da especialidade vencedora (ex: "CARD") ou null se não for possível.
     */
    public static String determinarEspecialidade(Sintoma[] sintomas, int qtdSintomas) {
        if (qtdSintomas == 0 || sintomas == null) {
            return null;
        }

        // 1. Descobrir qual é o nível de urgência mais alto presente nos sintomas
        // Pesos: Vermelha = 3, Laranja = 2, Verde = 1
        int maiorPesoEncontrado = 0;
        String nivelVencedor = "";

        for (int i = 0; i < qtdSintomas; i++) {
            // Assumimos que o colega criou o metodo getNivelUrgencia() na classe Sintoma
            String nivelAtual = sintomas[i].getNivelUrgencia();
            int pesoAtual = converterNivelParaPeso(nivelAtual);

            if (pesoAtual > maiorPesoEncontrado) {
                maiorPesoEncontrado = pesoAtual;
                nivelVencedor = nivelAtual;
            }
        }

        // Se não houver níveis válidos, retornamos null
        if (maiorPesoEncontrado == 0) {
            return null;
        }

        // 2. Contar as especialidades APENAS dos sintomas com o nível vencedor
        // Como não podemos usar Maps/Listas, usamos arrays paralelos para contar.
        // Assumimos um limite seguro de 20 especialidades possíveis para contagem.
        String[] nomesEspecialidades = new String[20];
        int[] contagemVotos = new int[20];
        int totalSpecsDistintas = 0;

        for (int i = 0; i < qtdSintomas; i++) {
            // Só contamos se o sintoma for do nível vencedor (ignora os de menor urgência)
            if (sintomas[i].getNivelUrgencia().equals(nivelVencedor)) {

                // Assumimos que getEspecialidades() devolve um String[] (pois um sintoma pode ter várias)
                String[] specsDoSintoma = sintomas[i].getCodigoEspecialidade();

                if (specsDoSintoma != null) {
                    for (int j = 0; j < specsDoSintoma.length; j++) {
                        String specAtual = specsDoSintoma[j];

                        // Verificar se já temos esta especialidade na nossa lista de contagem
                        int indiceEncontrado = -1;
                        for (int k = 0; k < totalSpecsDistintas; k++) {
                            if (nomesEspecialidades[k].equals(specAtual)) {
                                indiceEncontrado = k;
                                break;
                            }
                        }

                        if (indiceEncontrado != -1) {
                            // Já existe, incrementa o voto
                            contagemVotos[indiceEncontrado]++;
                        } else {
                            // Nova especialidade, adiciona à lista
                            if (totalSpecsDistintas < nomesEspecialidades.length) {
                                nomesEspecialidades[totalSpecsDistintas] = specAtual;
                                contagemVotos[totalSpecsDistintas] = 1;
                                totalSpecsDistintas++;
                            }
                        }
                    }
                }
            }
        }

        // 3. Determinar quem teve mais votos
        String especialidadeVencedora = null;
        int maxVotos = -1;

        for (int i = 0; i < totalSpecsDistintas; i++) {
            if (contagemVotos[i] > maxVotos) {
                maxVotos = contagemVotos[i];
                especialidadeVencedora = nomesEspecialidades[i];
            }
        }

        return especialidadeVencedora;
    }

    //     Metodo auxiliar para converter texto em peso numérico
    private static int converterNivelParaPeso(String nivel) {
        if (nivel == null) return 0;
        if (nivel.equalsIgnoreCase("Vermelha")) return 3;
        if (nivel.equalsIgnoreCase("Laranja")) return 2;
        if (nivel.equalsIgnoreCase("Verde")) return 1;
        return 0;
    }

    /**
     * Atualiza o nível de urgência e devolve TRUE se houve alguma alteração.
     * Isto permite ao Menu saber se deve mostrar avisos ao utilizador.
     */
    public static boolean atualizarNiveisUrgencia(Utente[] utentes, int nUtentes) {
        if (nUtentes == 0) return false;
        boolean houveAlteracao = false;

        for (int i = 0; i < nUtentes; i++) {
            Utente u = utentes[i];
            u.incrementarTempoEspera();
            String nivelAtual = u.getNivelUrgencia();
            int tempoEspera = u.getTempoEsperaNivel();

            // Lógica 1: Verde -> Amarelo [cite: 57]
            if (nivelAtual.equalsIgnoreCase(NivelUrgencia.VERDE)) {
                if (tempoEspera >= Configuracoes.getLimiteEsperaVerdeParaAmarelo()) {
                    u.setNivelUrgencia(NivelUrgencia.AMARELO);
                    u.resetarTempoEspera();
                    System.out.println("⚠️ O utente " + u.getNome() + " subiu para AMARELO.");
                    houveAlteracao = true;
                }
            }
            // Lógica 2: Amarelo -> Vermelho [cite: 58]
            else if (nivelAtual.equalsIgnoreCase(NivelUrgencia.AMARELO)) {
                if (tempoEspera >= Configuracoes.getLimiteEsperaAmareloParaVermelho()) {
                    u.setNivelUrgencia(NivelUrgencia.VERMELHO);
                    u.resetarTempoEspera();
                    System.out.println("🚨 O utente " + u.getNome() + " subiu para VERMELHO.");
                    houveAlteracao = true;
                }
            }

            // Lógica 3: Vermelho -> Saída
            if (u.getNivelUrgencia().equalsIgnoreCase(NivelUrgencia.VERMELHO)) {
                if (u.getTempoEsperaNivel() >= Configuracoes.getLimiteEsperaVermelhoSaida()) {
                    System.out.println("🚑 TRANSFERÊNCIA: Utente " + u.getNome() + " transferido por tempo limite.");
                    u.setNome(u.getNome() + " [TRANSFERIDO]");
                    houveAlteracao = true;
                }
            }
        }
        return houveAlteracao;
    }

    /**
     * Procura um médico que tenha a especialidade certa, esteja no turno e esteja livre.
     * Requisito: "atribuir médicos consoante as especialidades"[cite: 23].
     * @param medicos Array de médicos
     * @param nMedicos Quantidade de médicos
     * @param especialidadeAlvo A especialidade necessária (ex: "CARD")
     * @param horaAtual A hora atual do relógio
     * @return O objeto Medico se encontrar, ou null se ninguém puder atender.
     */
    public Medico procurarMedicoDisponivel(Medico[] medicos, int nMedicos, String especialidadeAlvo, int horaAtual) {
        // 1ª Passagem: Tenta encontrar alguém da mesma especialidade
        for (int i = 0; i < nMedicos; i++) {
            Medico m = medicos[i];
            if (m.isDisponivel() && m.getEspecialidade().equalsIgnoreCase(especialidadeAlvo)) {
                if (horaAtual >= m.getHoraEntrada() && horaAtual < m.getHoraSaida()) {
                    return m;
                }
            }
        }

        // 2ª Passagem: Se não encontrou, tenta qualquer médico disponível no turno
        for (int i = 0; i < nMedicos; i++) {
            Medico m = medicos[i];
            if (m.isDisponivel() && horaAtual >= m.getHoraEntrada() && horaAtual < m.getHoraSaida()) {
                return m;
            }
        }
        return null;
    }

    /**
     * Atualiza o estado dos médicos com base na hora atual.
     * Serve para simular a entrada e saída de turno.
     * @param medicos Array de médicos
     * @param nMedicos Quantidade de médicos
     * @param horaAtual A hora atual
     */
    public void atualizarEstadoMedicos(Medico[] medicos, int nMedicos, int horaAtual) {
        int limiteTrabalho = Configuracoes.getHorasTrabalhoParaDescanso();

        for (int i = 0; i < nMedicos; i++) {
            Medico m = medicos[i];
            m.decrementarTempoOcupado();

            // 1. Gestão de Entrada no Turno
            if (m.getHoraEntrada() == horaAtual && !m.isDisponivel()) {
                m.setDisponivel(true);
                m.setHorasSeguidasTrabalhadas(0);
                System.out.println("👨‍⚕️ O Dr(a). " + m.getNome() + " iniciou o turno.");
                continue;
            }

            // 2. Gestão de Pausa e Cansaço
            if (m.isDisponivel()) {
                m.setHorasSeguidasTrabalhadas(m.getHorasSeguidasTrabalhadas() + 1);

                if (m.getHorasSeguidasTrabalhadas() >= limiteTrabalho) {
                    m.setDisponivel(false);
                    m.setHorasSeguidasTrabalhadas(0);
                    System.out.println("☕ O Dr(a). " + m.getNome() + " entrou em pausa obrigatória (atingiu " + limiteTrabalho + "h).");
                }
            } else if (horaAtual > m.getHoraEntrada() && horaAtual < m.getHoraSaida()) {
                m.setDisponivel(true);
                System.out.println("✅ O Dr(a). " + m.getNome() + " terminou a pausa e voltou ao serviço.");
            }

            // 3. Gestão de Saída do Turno
            if (m.getHoraSaida() == horaAtual) {
                m.setDisponivel(false);
                m.setHorasSeguidasTrabalhadas(0);
                System.out.println("🚪 O Dr(a). " + m.getNome() + " terminou o turno.");
            } else  if (m.isDisponivel()){
                System.out.println("O médico " + m.getNome() + " (" + m.getEspecialidade() + ") permanece em serviço.");
            }
        }
    }

    public void processarUtente(Utente u, Medico[] medicos, int nMedicos, Sintoma[] todosSintomas, int nSintomas, int horaAtual) {
        if (u.getNome().contains("[ATENDIDO]") || u.getNome().contains("[TRANSFERIDO]")) return;

        Sintoma sintomaDoUtente = null;
        for (int k = 0; k < nSintomas; k++) {
            if (todosSintomas[k].getNome().equalsIgnoreCase(u.getSintoma())) {
                sintomaDoUtente = todosSintomas[k];
                break;
            }
        }

        if (sintomaDoUtente == null) return;

        Sintoma[] temp = { sintomaDoUtente };
        String especialidadeNecessaria = determinarEspecialidade(temp, 1);

        Medico medico = procurarMedicoDisponivel(medicos, nMedicos, especialidadeNecessaria, horaAtual);

        if (medico != null) {
            // Define o tempo de atendimento
            int tempoDeCura = medico.getEspecialidade().equalsIgnoreCase(especialidadeNecessaria) ? 1 : 2;

            medico.setDisponivel(false);
            medico.setTempoOcupadoRestante(tempoDeCura);

            System.out.println("✅ ATRIBUIÇÃO: Dr. " + medico.getNome() + " atende " + u.getNome() +
                    " (Tempo: " + tempoDeCura + " un.)");
            u.setNome(u.getNome() + " [ATENDIDO]");
        }
    }

}