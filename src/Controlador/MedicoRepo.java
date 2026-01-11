package Controlador;

import Modelo.Medico;

public class MedicoRepo {
    private Medico[] dados = new Medico[10];
    private int size = 0;

    public int size() { return size; }

    public Medico getAt(int i) {
        if (i < 0 || i >= size) return null;
        return dados[i];
    }

    public Medico findByNome(String nome) {
        int idx = findIndexByNome(nome);
        return (idx == -1) ? null : dados[idx];
    }

    public boolean add(Medico m) {
        if (m == null) return false;
        if (findIndexByNome(m.getNome()) != -1) return false; // evitar duplicados por nome
        ensureCap();
        dados[size++] = m;
        return true;
    }

    public boolean update(String nome, String novaEspecialidade, int novaEntrada, int novaSaida, int novoSalario) {
        int idx = findIndexByNome(nome);
        if (idx == -1) return false;

        if (novaEspecialidade != null && !novaEspecialidade.trim().isEmpty())
            dados[idx].setEspecialidade(novaEspecialidade.trim());

        if (novaEntrada > 0) dados[idx].setHoraEntrada(novaEntrada);
        if (novaSaida > 0) dados[idx].setHoraSaida(novaSaida);
        if (novoSalario >= 0) dados[idx].setSalario(novoSalario);

        return true;
    }

    public boolean removeByNome(String nome) {
        int idx = findIndexByNome(nome);
        if (idx == -1) return false;

        // shift: puxar tudo para a esquerda a partir do idx
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

        Medico[] novo = new Medico[dados.length * 2];
        for (int i = 0; i < dados.length; i++) {
            novo[i] = dados[i];
        }
        dados = novo;
    }
}

