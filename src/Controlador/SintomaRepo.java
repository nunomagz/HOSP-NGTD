package Controlador;

import Modelo.Sintoma;

public class SintomaRepo {
    private Sintoma[] dados = new Sintoma[80];
    private int size = 0;

    public int size() { return size; }

    public Sintoma getAt(int i) {
        if (i < 0 || i >= size) return null;
        return dados[i];
    }

    public Sintoma findByNome(String nome) {
        int idx = findIndexByNome(nome);
        return (idx == -1) ? null : dados[idx];
    }

    public boolean add(Sintoma s) {
        if (s == null) return false;
        if (findIndexByNome(s.getNome()) != -1) return false; // evitar duplicados por nome
        ensureCap();
        dados[size++] = s;
        return true;
    }

    // Atualiza só o nível de urgência
    public boolean updateNivel(String nome, String novoNivel) {
        int idx = findIndexByNome(nome);
        if (idx == -1) return false;
        dados[idx].setNivelUrgencia(novoNivel);
        return true;
    }

    // Atualiza as especialidades associadas (substitui o array)
    public boolean updateEspecialidades(String nome, String[] novosCodigos) {
        int idx = findIndexByNome(nome);
        if (idx == -1) return false;
        dados[idx].setCodigoEspecialidade(novosCodigos);
        return true;
    }

    public boolean removeByNome(String nome) {
        int idx = findIndexByNome(nome);
        if (idx == -1) return false;

        // shift
        for (int i = idx; i < size - 1; i++) {
            dados[i] = dados[i + 1];
        }
        dados[size - 1] = null;
        size--;
        return true;
    }

    private int findIndexByNome(String nome) {
        if (nome == null) return -1;
        String n = nome.trim();
        for (int i = 0; i < size; i++) {
            if (dados[i].getNome().equalsIgnoreCase(n)) return i;
        }
        return -1;
    }

    private void ensureCap() {
        if (size < dados.length) return;

        Sintoma[] novo = new Sintoma[dados.length * 2];
        for (int i = 0; i < dados.length; i++) {
            novo[i] = dados[i];
        }
        dados = novo;
    }
}

