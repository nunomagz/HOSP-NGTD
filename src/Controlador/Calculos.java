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
        // 1ª Passagem: Tenta encontrar um especialista disponível e no turno
        if (especialidadeAlvo != null) {
            for (int i = 0; i < nMedicos; i++) {
                Medico m = medicos[i];
                if (m.isDisponivel() && m.getEspecialidade().equalsIgnoreCase(especialidadeAlvo)) {
                    if (estaNoTurno(m, horaAtual)) {
                        return m;
                    }
                }
            }
        }

        // 2ª Passagem: Se não houver especialista ou a especialidade for null, busca qualquer médico no turno
        for (int i = 0; i < nMedicos; i++) {
            Medico m = medicos[i];
            if (m.isDisponivel() && estaNoTurno(m, horaAtual)) {
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

                    gestao.adicionarAoHistorico(u);
                    gestao.removerUtente(u.getNumero());
                    m.finalizarConsulta();
                }
            }

            // 2. Lógica de Entrada e Saída do Turno
            boolean noTurno = estaNoTurno(m, horaAtual);

            if (noTurno && !m.isDisponivel() && m.getTempoOcupadoRestante() == 0) {
                m.setDisponivel(true);
                m.setHorasSeguidasTrabalhadas(0);
                System.out.println("👨‍⚕️ Dr. " + m.getNome() + " iniciou o turno.");
            }
            else if (!noTurno && m.isDisponivel()) {
                if (m.getTempoOcupadoRestante() == 0) {
                    m.setDisponivel(false);
                    System.out.println("🚪 Dr. " + m.getNome() + " terminou o turno e saiu.");
                } else {
                    System.out.println("⏳ Dr. " + m.getNome() + " aguarda fim da consulta para sair.");
                }
            }

            // 3. Pausas (Regra das 5 horas)
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

    /**
     * Metodo Mestre: Percorre a fila de espera e atribui médicos aos utentes.
     * @param utentes Array de utentes na sala de espera
     * @param nUtentes Número de utentes
     * @param medicos Array de médicos
     * @param nMedicos Número de médicos
     * @param todosSintomas Array de todos os sintomas (necessário para buscar o objeto Sintoma pelo nome)
     * @param nSintomas Número de sintomas
     * @param horaAtual A hora atual do sistema
     */
    public void processarFilaEspera(Utente[] utentes, int nUtentes,
                                    Medico[] medicos, int nMedicos,
                                    Sintoma[] todosSintomas, int nSintomas,
                                    int horaAtual) {

        System.out.println("--- A processar fila de espera... ---");

        for (int i = 0; i < nUtentes; i++) {
            Utente u = utentes[i];

            // Ignorar utentes que já foram marcados como atendidos ou transferidos
            if (u.getNome().contains("[ATENDIDO]") || u.getNome().contains("[TRANSFERIDO]")) {
                continue;
            }

            // 1. Precisamos de encontrar o objeto Sintoma correspondente ao nome que o utente tem
            Sintoma sintomaDoUtente = null;
            for (int k = 0; k < nSintomas; k++) {
                if (todosSintomas[k].getNome().equalsIgnoreCase(u.getSintoma())) {
                    sintomaDoUtente = todosSintomas[k];
                    break;
                }
            }

            if (sintomaDoUtente != null) {
                // 2. Determinar a especialidade
                // Criamos um array temporário de 1 posição porque o teu metodo pede um array
                Sintoma[] temp = { sintomaDoUtente };
                String especialidade = determinarEspecialidade(temp, 1);

                if (especialidade != null) {
                    // 3. Tentar encontrar médico
                    Medico medico = procurarMedicoDisponivel(medicos, nMedicos, especialidade, horaAtual);

                    if (medico != null) {
                        // 4. SUCESSO: Realizar a atribuição
                        medico.setDisponivel(false); // O médico fica ocupado

                        // Marcamos o utente (para depois ser removido da sala pelo metodo de limpeza)
                        System.out.println("✅ ATRIBUIÇÃO: O Dr(a). " + medico.getNome() +
                                " (" + medico.getEspecialidade() + ") chamou o utente " + u.getNome());

                        u.setNome(u.getNome() + " [ATENDIDO]");
                    }
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

    /**
     * Verifica se o médico está dentro do seu horário de serviço,
     * suportando turnos que atravessam a meia-noite.
     */
    public boolean estaNoTurno(Medico m, int horaAtual) {
        if (m.getHoraEntrada() < m.getHoraSaida()) {
            // Turno padrão (ex: 08h às 16h)
            return horaAtual >= m.getHoraEntrada() && horaAtual < m.getHoraSaida();
        } else {
            // Turno noturno (ex: 22h às 06h)
            return horaAtual >= m.getHoraEntrada() || horaAtual < m.getHoraSaida();
        }
    }

}