package Controlador;

import Configurações.Configurações;
import Modelo.Especialidade;
import Modelo.Medico;
import Modelo.NivelUrgencia;
import Modelo.Sintoma;

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
public class DataIO {

    // Constrói o caminho completo: "Dados/" + "especialidades.txt"
    private String fullPath(String fileName) {
        return Configurações.getCaminhoficheiro() + fileName;
    }

    // Faz split de uma linha usando o separador configurado (ex: ";")
    private String[] split(String line) {
        return line.split(Pattern.quote(Configurações.getSeparadorFicheiro()));
    }

    /**
     * Carrega tudo para a memória (arrays dentro de GestãoHOSP).
     * Ordem importa por causa das validações.
     */
    public void carregarTudo(GestãoHOSP g) throws IOException {
        carregarEspecialidades(g);
        carregarMedicos(g);
        carregarSintomas(g);
    }

    /**
     * Guarda tudo nos ficheiros (reescreve o ficheiro completo).
     */
    public void guardarTudo(GestãoHOSP g) throws IOException {
        guardarEspecialidades(g);
        guardarMedicos(g);
        guardarSintomas(g);
    }

    // ---------------- ESPECIALIDADES ----------------

    private void carregarEspecialidades(GestãoHOSP g) throws IOException {
        File f = new File(fullPath(Configurações.getNomeFicheiroEspecialidade()));
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

    private void guardarEspecialidades(GestãoHOSP g) throws IOException {
        File f = new File(fullPath(Configurações.getNomeFicheiroEspecialidade()));

        // false -> reescreve o ficheiro inteiro
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(f, false), StandardCharsets.UTF_8))) {

            for (int i = 0; i < g.getNEspecialidades(); i++) {
                Especialidade e = g.getEspecialidadeAt(i);
                bw.write(e.getCodigo() + Configurações.getSeparadorFicheiro() + e.getNome());
                bw.newLine();
            }
        }
    }

    // ---------------- MÉDICOS ----------------

    private void carregarMedicos(GestãoHOSP g) throws IOException {
        File f = new File(fullPath(Configurações.getNomeFicheiroMedicos()));
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

    private void guardarMedicos(GestãoHOSP g) throws IOException {
        File f = new File(fullPath(Configurações.getNomeFicheiroMedicos()));

        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(f, false), StandardCharsets.UTF_8))) {

            for (int i = 0; i < g.getNMedicos(); i++) {
                Medico m = g.getMedicoAt(i);

                bw.write(m.getNome() + Configurações.getSeparadorFicheiro()
                        + m.getEspecialidade() + Configurações.getSeparadorFicheiro()
                        + m.getHoraEntrada() + Configurações.getSeparadorFicheiro()
                        + m.getHoraSaida() + Configurações.getSeparadorFicheiro()
                        + m.getSalario());
                bw.newLine();
            }
        }
    }

    // ---------------- SINTOMAS ----------------

    private void carregarSintomas(GestãoHOSP g) throws IOException {
        File f = new File(fullPath(Configurações.getNomeFicheiroSintomas()));
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

    private void guardarSintomas(GestãoHOSP g) throws IOException {
        File f = new File(fullPath(Configurações.getNomeFicheiroSintomas()));

        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(f, false), StandardCharsets.UTF_8))) {

            for (int i = 0; i < g.getNSintomas(); i++) {
                Sintoma s = g.getSintomaAt(i);

                // Construímos linha: Nome;Nivel;COD;COD...
                StringBuilder sb = new StringBuilder();
                sb.append(s.getNome())
                        .append(Configurações.getSeparadorFicheiro())
                        .append(s.getNivelUrgencia());

                String[] cods = s.getCodigoEspecialidade();
                if (cods != null) {
                    for (String c : cods) {
                        if (c != null && !c.trim().isEmpty()) {
                            sb.append(Configurações.getSeparadorFicheiro())
                                    .append(c.trim().toUpperCase());
                        }
                    }
                }

                bw.write(sb.toString());
                bw.newLine();
            }
        }
    }
}

