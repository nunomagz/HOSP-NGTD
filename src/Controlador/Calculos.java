package Controlador;

import Configuracoes.Configuracoes;
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
    public boolean atualizarNiveisUrgencia(Utente[] utentes, int nUtentes) {
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

                    System.out.println("⚠️ NOTIFICAÇÃO: O utente " + u.getNome() + " passou para urgência AMARELO.");
                    houveAlteracao = true;
                }
            }
            // Lógica 2: Amarelo -> Vermelho
            else if (nivelAtual.equalsIgnoreCase(NivelUrgencia.AMARELO)) {
                if (tempoEspera >= Configuracoes.getLimiteEsperaAmareloParaVermelho()) {
                    u.setNivelUrgencia(NivelUrgencia.VERMELHO);
                    u.resetarTempoEspera();

                    System.out.println("🚨 NOTIFICAÇÃO: O utente " + u.getNome() + " passou para urgência VERMELHO.");
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
}