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
     *
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

        boolean houveAlteracao = false; // Começa a false

        for (int i = 0; i < nUtentes; i++) {
            Utente u = utentes[i];

            u.incrementarTempoEspera();

            String nivelAtual = u.getNivelUrgencia();
            int tempoEspera = u.getTempoEsperaNivel();

            // Lógica 1: Verde -> Amarelo
            if (nivelAtual.equalsIgnoreCase(NivelUrgencia.VERDE)) {
                if (tempoEspera >= Configuracoes.getLimiteEsperaVerdeParaAmarelo()) {
                    u.setNivelUrgencia(NivelUrgencia.AMARELO);
                    u.resetarTempoEspera();

                    System.out.println("NOTIFICAÇÃO: O utente " + u.getNome() + " passou para urgência AMARELO.");
                    houveAlteracao = true;
                }
            }
            // Lógica 2: Amarelo -> Vermelho
            else if (nivelAtual.equalsIgnoreCase(NivelUrgencia.AMARELO)) {
                if (tempoEspera >= Configuracoes.getLimiteEsperaAmareloParaVermelho()) {
                    u.setNivelUrgencia(NivelUrgencia.VERMELHO);
                    u.resetarTempoEspera();

                    System.out.println("NOTIFICAÇÃO: O utente " + u.getNome() + " passou para urgência VERMELHO.");
                    houveAlteracao = true;
                }
                // Lógica 3: Vermelho -> Saída
                else if (nivelAtual.equalsIgnoreCase(NivelUrgencia.VERMELHO)) {
                    if (tempoEspera >= Configuracoes.getLimiteEsperaVermelhoSaida()) {
                        System.out.println("🚑 NOTIFICAÇÃO: O utente " + u.getNome() + " foi transferido (Tempo Limite).");

                        // Aqui podes marcar o utente para remoção ou mudar o nome
                        u.setNome(u.getNome() + " [TRANSFERIDO]");
                        // Ou chamar o removerUtente(u.getNumero()) se tiveres a certeza dos índices

                        houveAlteracao = true;
                    }
                }

                return houveAlteracao;
            }
        }
        return houveAlteracao;
    }

    /**
     * Procura um médico que tenha a especialidade certa, esteja no turno e esteja livre.
     * Requisito: "atribuir médicos consoante as especialidades"[cite: 23].
     *
     * @param medicos Array de médicos
     * @param nMedicos Quantidade de médicos
     * @param especialidadeAlvo A especialidade necessária (ex: "CARD")
     * @param horaAtual A hora atual do relógio
     * @return O objeto Medico se encontrar, ou null se ninguém puder atender.
     */
    public Medico procurarMedicoDisponivel(Medico[] medicos, int nMedicos, String especialidadeAlvo, int horaAtual) {
        if (especialidadeAlvo == null || medicos == null) {
            return null;
        }

        for (int i = 0; i < nMedicos; i++) {
            Medico m = medicos[i];

            // 1. Verifica se a especialidade corresponde
            if (m.getEspecialidade().equalsIgnoreCase(especialidadeAlvo)) {

                // 2. Verifica se está dentro do horário de trabalho
                // Regra: O médico está disponível se (Hora >= Entrada) E (Hora < Saída)
                // Exemplo: Entra às 8, sai às 16. Às 15h atende, às 16h já não.
                if (horaAtual >= m.getHoraEntrada() && horaAtual < m.getHoraSaida()) {

                    // 3. Verifica se está efetivamente livre (não está a atender ninguém)
                    if (m.isDisponivel()) {
                        return m;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Atualiza o estado dos médicos com base na hora atual.
     * Serve para simular a entrada e saída de turno.
     *
     * @param medicos Array de médicos
     * @param nMedicos Quantidade de médicos
     * @param horaAtual A hora atual
     */
    public void atualizarEstadoMedicos(Medico[] medicos, int nMedicos, int horaAtual) {
        for (int i = 0; i < nMedicos; i++) {
            Medico m = medicos[i];

            // Se for a hora de entrada, o médico fica disponível
            if (m.getHoraEntrada() == horaAtual) {
                // Só marcamos disponível se ele não estiver marcado (para evitar bugs de estado)
                if (!m.isDisponivel()) {
                    m.setDisponivel(true);
                    System.out.println("👨‍⚕️ O Dr(a). " + m.getNome() + " iniciou o turno.");
                }
            }

            // Se for altura para o medico tirar uma pausa
            m.setHorasSeguidasTrabalhadas(m.getHorasSeguidasTrabalhadas() + 1); // Incrementa as horas seguidas trabalhadas
            if (m.getHorasSeguidasTrabalhadas() >= 5 && m.isDisponivel()) {
                m.setDisponivel(false); // Medico entra em pausa (1 hora)
                System.out.println("👨‍⚕️ O Dr(a). " + m.getNome() + " entrou em pausa obrigatoria.");
            }

            // Se for a hora de saída, o médico sai (indisponível)
            if (m.getHoraSaida() == horaAtual && m.isDisponivel()) {
                m.setDisponivel(false);
                System.out.println("🚪 O Dr(a). " + m.getNome() + " terminou o turno.");
            } else {
                System.out.println("O médico " + m.getNome() + " (" + m.getEspecialidade() + ") permanece em serviço.");
            }
        }
    }

    /**
     * Metodo Mestre: Percorre a fila de espera e atribui médicos aos utentes.
     *
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

        // Ignorar utentes que já foram atendidos ou transferidos
        if (u.getNome().contains("[ATENDIDO]") || u.getNome().contains("[TRANSFERIDO]")) {
            return;
        }

        // 1. Encontrar o objeto Sintoma correspondente ao nome no utente
        Sintoma sintomaDoUtente = null;
        for (int k = 0; k < nSintomas; k++) {
            if (todosSintomas[k].getNome().equalsIgnoreCase(u.getSintoma())) {
                sintomaDoUtente = todosSintomas[k];
                break;
            }
        }

        if (sintomaDoUtente == null) {
            // não encontramos o sintoma -> não dá para encaminhar
            return;
        }

        // 2. Determinar a especialidade (reutilizando o teu metodo)
        Sintoma[] temp = { sintomaDoUtente };   // array de 1 posição
        String especialidade = determinarEspecialidade(temp, 1);

        if (especialidade == null) {
            // não foi possível escolher especialidade
            return;
        }

        // 3. Tentar encontrar médico disponível
        Medico medico = procurarMedicoDisponivel(medicos, nMedicos, especialidade, horaAtual);

        if (medico != null) {
            // 4. SUCESSO: atribuir
            medico.setDisponivel(false); // médico fica ocupado
            System.out.println("✅ ATRIBUIÇÃO: O Dr(a). " + medico.getNome() +
                    " (" + medico.getEspecialidade() + ") chamou o utente " + u.getNome());
            u.setNome(u.getNome() + " [ATENDIDO]");
        }
    }

}