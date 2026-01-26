package Gestao;

import Configuracoes.Configuracoes;
import Modelo.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * DataIO: responsável por ler e guardar os ficheiros .txt obrigatórios.
 *
 * Importante:
 * - Carrega primeiro Especialidades, depois Médicos, depois Sintomas
 *   (porque médicos/sintomas dependem das especialidades).
 * - Se uma linha vier inválida, é ignorada e o programa não crasha.
 */
public class GestorFicheiros {

    // Constrói o caminho completo: "Dados/" + "especialidades.txt"
    private String fullPath(String fileName) {
        return Configuracoes.getCaminhoficheiro() + fileName;
    }

    // Faz split de uma linha usando o separador configurado (ex: ";")
    private String[] split(String line) {
        return line.split(Pattern.quote(Configuracoes.getSeparadorFicheiro()));
    }

    /**
     * Carrega tudo para a memória (arrays dentro de GestãoHOSP).
     * Ordem importa por causa das validações.
     */
    public void carregarTudo(GestaoHOSP g) throws IOException {
        carregarConfiguracoes();
        carregarEspecialidades(g);
        carregarMedicos(g);
        carregarSintomas(g);
        carregarUtentes(g);
    }

    /**
     * Guarda tudo nos ficheiros (reescreve o ficheiro completo).
     */
    public void guardarTudo(GestaoHOSP g) throws IOException {
        guardarEspecialidades(g);
        guardarMedicos(g);
        guardarSintomas(g);
        guardarUtentes(g);
    }

    // ---------------- ESPECIALIDADES ----------------

    private void carregarEspecialidades(GestaoHOSP g) throws IOException {
        File f = new File(fullPath(Configuracoes.getNomeFicheiroEspecialidade()));
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // formato: COD;Nome
                String[] p = split(line);
                if (p.length < 2) {
                    System.out.println("⚠️ Linha inválida em especialidades: " + line);
                    continue;
                }

                boolean ok = g.adicionarEspecialidade(p[0].trim(), p[1].trim());
                if (!ok) {
                    // ok=false pode ser duplicado ou vazio
                    System.out.println("⚠️ Especialidade ignorada: " + line);
                }
            }
        }
    }

    private void guardarEspecialidades(GestaoHOSP g) throws IOException {
        File f = new File(fullPath(Configuracoes.getNomeFicheiroEspecialidade()));

        // false -> reescreve o ficheiro inteiro
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(f, false), StandardCharsets.UTF_8))) {

            for (int i = 0; i < g.getNEspecialidades(); i++) {
                Especialidade e = g.getEspecialidadeAt(i);
                bw.write(e.getCodigo() + Configuracoes.getSeparadorFicheiro() + e.getNome());
                bw.newLine();
            }
        }
    }

    // ---------------- MÉDICOS ----------------

    private void carregarMedicos(GestaoHOSP g) throws IOException {
        File f = new File(fullPath(Configuracoes.getNomeFicheiroMedicos()));
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // formato: Nome;COD;Entrada;Saida;Salario
                String[] p = split(line);
                if (p.length < 5) {
                    System.out.println("⚠️ Linha inválida em médicos: " + line);
                    continue;
                }

                try {
                    String nome = p[0].trim();
                    String esp = p[1].trim().toUpperCase();
                    int entrada = Integer.parseInt(p[2].trim());
                    int saida = Integer.parseInt(p[3].trim());
                    int salario = Integer.parseInt(p[4].trim());

                    // Se a especialidade não existir, o método devolve false e ignoramos
                    boolean ok = g.adicionarMedico(nome, esp, entrada, saida, salario);
                    if (!ok) System.out.println("⚠️ Médico ignorado: " + line);

                } catch (NumberFormatException ex) {
                    // Proteção: se vier um número mal formatado no ficheiro
                    System.out.println("⚠️ Médico ignorado (número inválido): " + line);
                }
            }
        }
    }

    private void guardarMedicos(GestaoHOSP g) throws IOException {
        File f = new File(fullPath(Configuracoes.getNomeFicheiroMedicos()));

        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(f, false), StandardCharsets.UTF_8))) {

            for (int i = 0; i < g.getNMedicos(); i++) {
                Medico m = g.getMedicoAt(i);

                bw.write(m.getNome() + Configuracoes.getSeparadorFicheiro()
                        + m.getEspecialidade() + Configuracoes.getSeparadorFicheiro()
                        + m.getHoraEntrada() + Configuracoes.getSeparadorFicheiro()
                        + m.getHoraSaida() + Configuracoes.getSeparadorFicheiro()
                        + m.getSalario());
                bw.newLine();
            }
        }
    }

    // ---------------- SINTOMAS ----------------

    private void carregarSintomas(GestaoHOSP g) throws IOException {
        File f = new File(fullPath(Configuracoes.getNomeFicheiroSintomas()));
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // formato: Nome;Nivel;COD;COD;...
                String[] p = split(line);

                // pelo menos: nome + nivel + 1 especialidade
                if (p.length < 3) {
                    System.out.println("⚠️ Sintoma ignorado (sem especialidade): " + line);
                    continue;
                }

                String nome = p[0].trim();

                // Normaliza "Laranja" e "Vermelha" para valores do sistema
                String nivel = NivelUrgencia.normalizar(p[1].trim());
                if (nivel == null) {
                    System.out.println("⚠️ Sintoma ignorado (nível inválido): " + line);
                    continue;
                }

                // resto da linha = códigos de especialidade
                String[] cods = new String[p.length - 2];
                for (int i = 0; i < cods.length; i++) {
                    cods[i] = p[i + 2].trim().toUpperCase();
                }

                // Gestão valida se os códigos existem como especialidade
                boolean ok = g.adicionarSintoma(nome, nivel, cods);
                if (!ok) System.out.println("⚠️ Sintoma ignorado: " + line);
            }
        }
    }

    private void guardarSintomas(GestaoHOSP g) throws IOException {
        File f = new File(fullPath(Configuracoes.getNomeFicheiroSintomas()));

        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(f, false), StandardCharsets.UTF_8))) {

            for (int i = 0; i < g.getNSintomas(); i++) {
                Sintoma s = g.getSintomaAt(i);

                // Construímos linha: Nome;Nivel;COD;COD...
                StringBuilder sb = new StringBuilder();
                sb.append(s.getNome())
                        .append(Configuracoes.getSeparadorFicheiro())
                        .append(s.getNivelUrgencia());

                String[] cods = s.getCodigoEspecialidade();
                if (cods != null) {
                    for (String c : cods) {
                        if (c != null && !c.trim().isEmpty()) {
                            sb.append(Configuracoes.getSeparadorFicheiro())
                                    .append(c.trim().toUpperCase());
                        }
                    }
                }

                bw.write(sb.toString());
                bw.newLine();
            }
        }
    }
    // ---------------- CONFIGURAÇÕES ----------------

    private static final String NOME_FICHEIRO_CONFIG = "config.txt";

    // Helper: lê próxima linha (trim) ou devolve null
    private String readLineTrim(BufferedReader br) throws IOException {
        String l = br.readLine();
        return (l == null) ? null : l.trim();
    }

    // Helper: aplica int apenas se a linha for válida (evita repetir try/catch)
    private void applyIntIfValid(String line, java.util.function.IntConsumer setter) {
        if (line == null || line.isEmpty()) return;
        try {
            setter.accept(Integer.parseInt(line));
        } catch (NumberFormatException ignored) {
            // Mantém valor atual (tolerante a ficheiro desatualizado/mal formatado)
        }
    }

    // Helper: aplica String apenas se não vier vazia
    private void applyStringIfValid(String line, java.util.function.Consumer<String> setter) {
        if (line == null) return;
        line = line.trim();
        if (line.isEmpty()) return;
        setter.accept(line);
    }

    /**
     * Guarda as configurações atuais em "Dados/config.txt".
     * Formato (uma linha por campo), por ordem:
     * 1) tempos consulta (baixa, media, urgente)
     * 2) limites espera (verde->amarelo, amarelo->vermelho, vermelho->saida)
     * 3) password
     * 4) separador ficheiro
     * 5) descanso (horasTrabalhoParaDescanso, tempoDescanso)
     */
    public void guardarConfiguracoes() throws IOException {
        File f = new File("config.txt");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {

            // 1) Tempos de consulta
            bw.write(String.valueOf(Configuracoes.getTempoConsultaBaixa()));
            bw.newLine();
            bw.write(String.valueOf(Configuracoes.getTempoConsultaMedia()));
            bw.newLine();
            bw.write(String.valueOf(Configuracoes.getTempoConsultaUrgente()));
            bw.newLine();

            // 2) Limites de espera
            bw.write(String.valueOf(Configuracoes.getLimiteEsperaVerdeParaAmarelo()));
            bw.newLine();
            bw.write(String.valueOf(Configuracoes.getLimiteEsperaAmareloParaVermelho()));
            bw.newLine();
            bw.write(String.valueOf(Configuracoes.getLimiteEsperaVermelhoSaida()));
            bw.newLine();

            // 3) Password
            bw.write(Configuracoes.getPassword() == null ? "" : Configuracoes.getPassword());
            bw.newLine();

            // 4) Separador
            bw.write(Configuracoes.getSeparadorFicheiro() == null ? "" : Configuracoes.getSeparadorFicheiro());
            bw.newLine();

            // 5) Regras de descanso
            bw.write(String.valueOf(Configuracoes.getHorasTrabalhoParaDescanso()));
            bw.newLine();
            bw.write(String.valueOf(Configuracoes.getTempoDescanso()));
            bw.newLine();

            // 6) Caminho dos Ficheiros
            String caminho = Configuracoes.getCaminhoficheiro();
            bw.write(caminho == null ? "Dados/" : caminho);
            bw.newLine();
        }
    }

    /**
     * Carrega configurações de "Dados/config.txt".
     * Se faltar alguma linha (ficheiro antigo), mantém os defaults atuais.
     * Se um número vier inválido, ignora essa linha e mantém o valor atual.
     */
    public void carregarConfiguracoes() {
        File f = new File("config.txt");
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader( new FileReader(f))) {

            // 1) Tempos
            applyIntIfValid(readLineTrim(br), Configuracoes::setTempoConsultaBaixa);
            applyIntIfValid(readLineTrim(br), Configuracoes::setTempoConsultaMedia);
            applyIntIfValid(readLineTrim(br), Configuracoes::setTempoConsultaUrgente);

            // 2) Limites
            applyIntIfValid(readLineTrim(br), Configuracoes::setLimiteEsperaVerdeParaAmarelo);
            applyIntIfValid(readLineTrim(br), Configuracoes::setLimiteEsperaAmareloParaVermelho);
            applyIntIfValid(readLineTrim(br), Configuracoes::setLimiteEsperaVermelhoSaida);

            // 3) Password
            applyStringIfValid(readLineTrim(br), Configuracoes::setPassword);

            // 4) Separador
            applyStringIfValid(readLineTrim(br), Configuracoes::setSeparadorFicheiro);

            // 5) Descanso
            applyIntIfValid(readLineTrim(br), Configuracoes::setHorasTrabalhoParaDescanso);
            applyIntIfValid(readLineTrim(br), Configuracoes::setTempoDescanso);

            // 6) Caminho dos Ficheiros
            applyStringIfValid(readLineTrim(br), Configuracoes::setCaminhoficheiro);

        } catch (Exception e) {
            System.out.println("⚠️ Erro ao carregar configurações (ficheiro pode estar desatualizado): " + e.getMessage());
        }
    }

    /**
     * Tenta alterar o diretório onde os ficheiros de dados são guardados.
     * Verifica se a pasta antiga existe e tenta movê-la (renomear) para o novo caminho.
     * Se a pasta antiga não existir, apenas atualiza a configuração para o novo caminho.
     * @param novoCaminho O caminho para a nova pasta de dados.
     * @return true se a alteração foi bem sucedida, false caso contrário.
     */
    public boolean mudarDiretorioDados(String novoCaminho) {
        if (novoCaminho.isEmpty()) return false;

        if (!novoCaminho.endsWith("/") && !novoCaminho.endsWith("\\")) {
            novoCaminho +="/";
        }

        File pastaAntiga = new File(Configuracoes.getCaminhoficheiro());
        File pastaNova = new File(novoCaminho);

        if (pastaAntiga.exists()) {
            boolean sucesso = pastaAntiga.renameTo(pastaNova);
            if (sucesso) {
                Configuracoes.setCaminhoficheiro(novoCaminho);
                return true;
            } else {
                return false;
            }
        } else {
            Configuracoes.setCaminhoficheiro(novoCaminho);
            return true;
        }
    }

// ---------------- LOGS / HISTÓRICO ----------------

    /**
     * Escreve uma mensagem no ficheiro de histórico (logs.txt).
     * O metodo utiliza o modo 'append' (acrescentar) para não apagar registos anteriores.
     * Se o ficheiro não existir, é criado automaticamente.
     *
     * @param mensagem A linha de texto a gravar no histórico.
     */
    public void escreverLog(String mensagem) {
        File f = new File(Configuracoes.getCaminhoficheiro() + "logs.txt");

        try {
            if (!f.exists()) {
                f.createNewFile();
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(f, true))) { // true = append
                bw.write(mensagem);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Erro ao escrever no log: " + e.getMessage());
        }
    }

    /**
     * Lê e apresenta na consola o conteúdo do ficheiro de histórico (logs.txt)
     * Utilizado para consulta de eventos passados no menu Estatísticas.
     */
    public void lerLogs() {
        File f = new File(Configuracoes.getCaminhoficheiro() + "logs.txt");
        if (!f.exists()) {
            System.out.println("Ainda não existem registos de histórico.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linha;
            System.out.println("--- INÍCIO DO HISTÓRICO ---");
            while ((linha = br.readLine()) != null) {
                System.out.println(linha);
            }
            System.out.println("--- FIM DO HISTÓRICO ---");
        } catch (IOException e) {
            System.out.println("Erro ao ler logs: " + e.getMessage());
        }
    }

    // ---------------- UTENTES ----------------

    public void guardarUtentes(GestaoHOSP g) throws IOException {
        File f = new File(fullPath("utentes.txt"));

        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(f, false), StandardCharsets.UTF_8))) {

            for (int i = 0; i < g.getNUtentes(); i++) {
                Utente u = g.getUtenteAt(i);
                String sintoma = (u.getSintoma() == null) ? "Pendente" : u.getSintoma();
                String nivel = (u.getNivelUrgencia() == null) ? "A aguardar" : u.getNivelUrgencia();
                bw.write(u.getNumero() + Configuracoes.getSeparadorFicheiro() +
                        u.getNome() + Configuracoes.getSeparadorFicheiro() +
                        u.getIdade() + Configuracoes.getSeparadorFicheiro() +
                        sintoma + Configuracoes.getSeparadorFicheiro() +
                        nivel + Configuracoes.getSeparadorFicheiro() +
                        u.getTempoEsperaNivel());
                bw.newLine();
            }
        }
    }

    public void carregarUtentes(GestaoHOSP g) throws IOException {
        File f = new File(fullPath("utentes.txt"));
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] p = split(line);
                if (p.length < 6) continue; // Precisa de pelo menos 6 campos

                try {
                    int numero = Integer.parseInt(p[0].trim());
                    String nome = p[1].trim();
                    int idade = Integer.parseInt(p[2].trim());
                    String sintoma = p[3].trim();
                    String nivel = p[4].trim();
                    int tempo = Integer.parseInt(p[5].trim());

                    // Usamos um metodo especial na GestaoHOSP para carregar (ver passo seguinte)
                    g.carregarUtenteDoFicheiro(numero, nome, idade, sintoma, nivel, tempo);

                } catch (Exception e) {
                    System.out.println("Erro ao ler utente: " + line);
                }
            }
        }
    }
}

