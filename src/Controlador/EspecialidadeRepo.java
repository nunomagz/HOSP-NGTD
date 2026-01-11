package Controlador;

import Modelo.Especialidade;

public class EspecialidadeRepo {

    private Especialidade[] dados = new Especialidade[10];
    private int size = 0;

    //  para o DataIO usar repo.size()
    public int size() {
        return size;
    }

    // (se quiseres manter também)
    public int getSize() {
        return size;
    }

    //  usado para guardar no ficheiro (repo.getAt(i))
    public Especialidade getAt(int i) {
        if (i < 0 || i >= size) return null;
        return dados[i];
    }

    //  usado ao carregar do ficheiro
    public boolean add(Especialidade e) {
        if (e == null) return false;

        // evitar duplicados pelo código
        if (findIndexByCodigo(e.getCodigo()) != -1) return false;

        ensureCap();
        dados[size++] = e;
        return true;
    }

    public Especialidade findByCodigo(String codigo) {
        int idx = findIndexByCodigo(codigo);
        return (idx == -1) ? null : dados[idx];
    }

    public boolean update(String codigoAtual, String novoCodigo, String novoNome) {
        int idx = findIndexByCodigo(codigoAtual);
        if (idx == -1) return false;

        if (novoCodigo != null && !novoCodigo.trim().isEmpty()) {
            int idxNovo = findIndexByCodigo(novoCodigo);
            if (idxNovo != -1 && idxNovo != idx) return false;
            dados[idx].setCodigo(novoCodigo.trim());
        }

        if (novoNome != null && !novoNome.trim().isEmpty()) {
            dados[idx].setNome(novoNome.trim());
        }

        return true;
    }

    public boolean removeByCodigo(String codigo) {
        int idx = findIndexByCodigo(codigo);

        //  não encontrado
        if (idx == -1) return false;

        for (int i = idx; i < size - 1; i++) {
            dados[i] = dados[i + 1];
        }
        dados[size - 1] = null;
        size--;
        return true;
    }

    private int findIndexByCodigo(String codigo) {
        if (codigo == null) return -1;
        String c = codigo.trim();
        for (int i = 0; i < size; i++) {
            if (dados[i].getCodigo().equalsIgnoreCase(c)) return i;
        }
        return -1;
    }

    //  nome em Java: camelCase (não é obrigatório, mas é o padrão)
    private void ensureCap() {
        if (size < dados.length) return;

        Especialidade[] novo = new Especialidade[dados.length * 2];

        //  copiar do antigo para o novo
        for (int i = 0; i < dados.length; i++) {
            novo[i] = dados[i];
        }

        //  trocar referência do array
        dados = novo;
    }
}
