package Controlador;

import Configuracoes.Configuracoes;
import Gestao.GestaoHOSP;
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
        // 1ª Passagem: Tenta encontrar alguém da mesma especialidade (apenas se a especialidade for conhecida)
        if (especialidadeAlvo != null) {
            for (int i = 0; i < nMedicos; i++) {
                Medico m = medicos[i];
                if (m.isDisponivel() && m.getEspecialidade().equalsIgnoreCase(especialidadeAlvo)) {
                    if (horaAtual >= m.getHoraEntrada() && horaAtual < m.getHoraSaida()) {
                        return m;
                    }
                }
            }
        }

        // 2ª Passagem: Se não encontrou especialista OU se o utente não tem especialidade definida,
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
    public void atualizarEstadoMedicos(Medico[] medicos, int nMedicos, int horaAtual, GestaoHOSP gestao) {
        for (int i = 0; i < nMedicos; i++) {
            Medico m = medicos[i];

            // 1. Decrementar tempo de consulta e dar alta
            if (m.getTempoOcupadoRestante() > 0) {
                m.decrementarTempoOcupado();

                if (m.getTempoOcupadoRestante() == 0 && m.getUtenteEmConsulta() != null) {
                    Utente u = m.getUtenteEmConsulta();
                    System.out.println("🏁 ALTA: O utente " + u.getNome() + " terminou a consulta com Dr. " + m.getNome());

                    // Mover para o histórico e remover da sala de espera
                    gestao.adicionarAoHistorico(u);
                    gestao.removerUtente(u.getNumero());
                    m.finalizarConsulta();
                }
            }

            // 2. Lógica de Entrada no Turno
            if (m.getHoraEntrada() == horaAtual && !m.isDisponivel()) {
                m.setDisponivel(true);
                m.setHorasSeguidasTrabalhadas(0);
                System.out.println("👨‍⚕️ Dr. " + m.getNome() + " iniciou o turno.");
            }

            // 3. Lógica de Saída do Turno (Respeita o serviço em curso )
            if (horaAtual >= m.getHoraSaida()) {
                if (m.getTempoOcupadoRestante() == 0) {
                    if (m.isDisponivel()) {
                        m.setDisponivel(false);
                        System.out.println("🚪 Dr. " + m.getNome() + " terminou o turno e saiu do hospital.");
                    }
                } else {
                    // Notificação de que o médico está a fazer "horas extra" para acabar o serviço
                    System.out.println("⏳ Dr. " + m.getNome() + " aguarda fim da consulta para sair (Turno encerrado).");
                }
            }

            // 4. Pausas (Resetar contador após a pausa)
            if (m.isDisponivel() && m.getTempoOcupadoRestante() == 0) {
                m.setHorasSeguidasTrabalhadas(m.getHorasSeguidasTrabalhadas() + 1);
                if (m.getHorasSeguidasTrabalhadas() >= 5) {
                    m.setDisponivel(false);
                    m.setHorasSeguidasTrabalhadas(0);
                    System.out.println("☕ Dr. " + m.getNome() + " entrou em pausa obrigatória.");
                }
            }
        }
    }

    public void processarUtente(Utente u, Medico[] medicos, int nMedicos, Sintoma[] todosSintomas, int nSintomas, int horaAtual) {
        if (u.getNome().contains("[ATENDIDO]") || u.getNome().contains("[TRANSFERIDO]")) return;

        // 1. Tentar encontrar o sintoma
        Sintoma sintomaDoUtente = null;
        for (int k = 0; k < nSintomas; k++) {
            if (todosSintomas[k].getNome().equalsIgnoreCase(u.getSintoma())) {
                sintomaDoUtente = todosSintomas[k];
                break;
            }
        }

        // 2. Tentar determinar a especialidade (pode resultar em null)
        String especialidadeNecessaria = null;
        if (sintomaDoUtente != null) {
            Sintoma[] temp = { sintomaDoUtente };
            especialidadeNecessaria = determinarEspecialidade(temp, 1);
        }

        // 3. Procurar médico (mesmo que especialidadeNecessaria seja null)
        Medico medico = procurarMedicoDisponivel(medicos, nMedicos, especialidadeNecessaria, horaAtual);

        if (medico != null) {
            // Regra de tempo: 1 un. para especialista, 2 un. para atendimento geral
            int tempoDeCura = 2; // Valor padrão (Clínica Geral)
            if (especialidadeNecessaria != null && medico.getEspecialidade().equalsIgnoreCase(especialidadeNecessaria)) {
                tempoDeCura = 1; // Especialista
            }

            medico.setDisponivel(false);
            medico.setTempoOcupadoRestante(tempoDeCura);
            u.setEspecialidadeAtendimento(medico.getEspecialidade());
            medico.setUtenteEmConsulta(u); // Vincula o utente ao médico

            System.out.println("✅ ATRIBUIÇÃO: Dr. " + medico.getNome() + " atende " + u.getNome() +
                    " (Tempo: " + tempoDeCura + " un.)");
            u.setNome(u.getNome() + " [ATENDIDO]");
        } else {
            System.out.println("⏳ Ninguém disponível para atender " + u.getNome() + " no momento.");
        }
    }

}