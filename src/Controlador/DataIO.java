package Controlador;

import Modelo.Especialidade;

import Configurações.Configurações;
import Modelo.Medico;
import Modelo.NivelUrgencia;
import Modelo.Sintoma;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class DataIO {
    private String fullPath(String fileName) {
        return Configurações.getCaminhoficheiro() + fileName;
    }

    private String[] split(String line) {
        return line.split(Pattern.quote(Configurações.getSeparadorFicheiro()));
    }

    public void carregarTudo(EspecialidadeRepo espRepo, MedicoRepo medRepo, SintomaRepo sinRepo) throws IOException {
        carregarEspecialidades(espRepo);
        carregarMedicos(medRepo);
        carregarSintomas(sinRepo);
    }

    public void guardarTudo(EspecialidadeRepo espRepo, MedicoRepo medRepo, SintomaRepo sinRepo) throws IOException {
        guardarEspecialidades(espRepo);
        guardarMedicos(medRepo);
        guardarSintomas(sinRepo);
    }

    private void carregarEspecialidades(EspecialidadeRepo repo) throws IOException {
        File f = new File(fullPath((Configurações.getNomeFicheiroEspecialidade())));
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null){
                line = line.trim();
                if (line.isEmpty()) continue;

                String [] p = split(line);
                if (p.length < 2) continue;

                repo.add(new Especialidade(p[0].trim(),p[1].trim()));
            }
        }
    }

    private void guardarEspecialidades(EspecialidadeRepo repo) throws IOException {
        File f = new File(fullPath(Configurações.getNomeFicheiroEspecialidade()));

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, false), StandardCharsets.UTF_8))) {
            for (int i = 0; i < repo.size(); i++) {
                Especialidade e = repo.getAt(i);
                bw.write(e.getCodigo() + Configurações.getSeparadorFicheiro() + e.getNome());
                bw.newLine();
            }

        }
    }

    // ---------- MÉDICOS ----------
    private void carregarMedicos(MedicoRepo repo) throws IOException {
        File f = new File(fullPath(Configurações.getNomeFicheiroMedicos()));
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(f), StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // formato: Nome;Esp;Entrada;Saida;Salario
                String[] p = split(line);
                if (p.length < 5) continue;

                String nome = p[0].trim();
                String esp = p[1].trim();
                int entrada = Integer.parseInt(p[2].trim());
                int saida = Integer.parseInt(p[3].trim());
                int salario = Integer.parseInt(p[4].trim());

                // campos que não vêm no ficheiro -> default
                repo.add(new Medico(nome, esp, entrada, saida, salario, false, 0));
            }
        }
    }

    private void guardarMedicos(MedicoRepo repo) throws IOException {
        File f = new File(fullPath(Configurações.getNomeFicheiroMedicos()));

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(f, false), StandardCharsets.UTF_8))) {

            for (int i = 0; i < repo.size(); i++) {
                Medico m = repo.getAt(i);
                bw.write(m.getNome() + Configurações.getSeparadorFicheiro()
                        + m.getEspecialidade() + Configurações.getSeparadorFicheiro()
                        + m.getHoraEntrada() + Configurações.getSeparadorFicheiro()
                        + m.getHoraSaida() + Configurações.getSeparadorFicheiro()
                        + m.getSalario());
                bw.newLine();
            }
        }
    }

    // ---------- SINTOMAS ----------
    private void carregarSintomas(SintomaRepo repo) throws IOException {
        File f = new File(fullPath(Configurações.getNomeFicheiroSintomas()));
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(f), StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // formato: Nome;Nivel;COD;COD;...
                String[] p = split(line);
                if (p.length < 2) continue;

                String nome = p[0].trim();

                // normaliza para VERDE/AMARELO/VERMELHO (consistência do sistema)
                String nivel = NivelUrgencia.normalizar(p[1].trim());
                if (nivel == null) continue; // se vier inválido, ignoramos linha

                int nEsp = Math.max(0, p.length - 2);
                String[] cods = new String[nEsp];
                for (int i = 0; i < nEsp; i++) cods[i] = p[i + 2].trim();

                repo.add(new Sintoma(nome, nivel, cods));
            }
        }
    }

    private void guardarSintomas(SintomaRepo repo) throws IOException {
        File f = new File(fullPath(Configurações.getNomeFicheiroSintomas()));

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(f, false), StandardCharsets.UTF_8))) {

            for (int i = 0; i < repo.size(); i++) {
                Sintoma s = repo.getAt(i);

                StringBuilder sb = new StringBuilder();
                sb.append(s.getNome()).append(Configurações.getSeparadorFicheiro())
                        .append(s.getNivelUrgencia());

                String[] cods = s.getCodigoEspecialidade();
                if (cods != null) {
                    for (String c : cods) {
                        if (c != null && !c.trim().isEmpty()) {
                            sb.append(Configurações.getSeparadorFicheiro()).append(c.trim());
                        }
                    }
                }

                bw.write(sb.toString());
                bw.newLine();
            }
        }
    }
}
