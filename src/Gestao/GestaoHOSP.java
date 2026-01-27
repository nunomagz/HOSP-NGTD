package Gestao;

                                //linha 178 e 179 importantes

import Controlador.Calculos;
import Modelo.*;

/**
 * Classe de gestão dos dados do hospital.
 * Guarda tudo em ARRAYS + contadores, sem ArrayList.
 *
 * Aqui ficam os CRUDs (Create/Read/Update/Delete) para:
 * - Especialidades
 * - Médicos
 * - Sintomas
 * - Utentes
 */
public class GestaoHOSP {

    // "BASE DE DADOS" EM MEMÓRIA

    // Arrays + contadores (nX diz quantos elementos realmente existem no array)
    private Especialidade[] especialidades = new Especialidade[10];
    private int nEspecialidades = 0;

    private Medico[] medicos = new Medico[10];
    private int nMedicos = 0;

    private Sintoma[] sintomas = new Sintoma[80];
    private int nSintomas = 0;

    // Utentes são "ordem de chegada" (contador automático)
    private Utente[] utentes = new Utente[50];
    private int nUtentes = 0;
    private int proximoNumeroUtente = 1;
    private Utente[] historicoUtentes = new Utente[200];
    private int nHistorico = 0;


    // GETTERS (para DataIO e Menu
    public int getNEspecialidades() { return nEspecialidades; }
    public Especialidade getEspecialidadeAt(int i) {
        return (i < 0 || i >= nEspecialidades) ? null : especialidades[i];
    }

    public int getNMedicos() { return nMedicos; }
    public Medico getMedicoAt(int i) {
        return (i < 0 || i >= nMedicos) ? null : medicos[i];
    }

    public int getNSintomas() { return nSintomas; }
    public Sintoma getSintomaAt(int i) {
        return (i < 0 || i >= nSintomas) ? null : sintomas[i];
    }

    public int getNUtentes() { return nUtentes; }
    public Utente getUtenteAt(int i) {
        return (i < 0 || i >= nUtentes) ? null : utentes[i];
    }

    public int getNHistorico() { return nHistorico; }
    public Utente getUtenteHistoricoAt(int i) {
        if (i < 0 || i >= nHistorico) return null;
        return historicoUtentes[i];
    }


    // CRUD ESPECIALIDADE

    /**
     * CREATE: adiciona uma especialidade.
     * Regras:
     * - código e nome não vazios
     * - código único
     */
    public boolean adicionarEspecialidade(String codigo, String nome) {
        if (vazio(codigo) || vazio(nome)) return false;

        String c = cod(codigo);
        String n = nomeTrim(nome);

        if (procurarEspecialidadePorCodigo(c) != null) return false;

        ensureCapEspecialidades();
        especialidades[nEspecialidades++] = new Especialidade(c, n);
        return true;
    }

    /**
     * READ: procurar especialidade por código.
     */
    public Especialidade procurarEspecialidadePorCodigo(String codigo) {
        int idx = indexEspecialidade(codigo);
        return (idx == -1) ? null : especialidades[idx];
    }

    /**
     * UPDATE: alterar código e/ou nome da especialidade.
     * - novoCódigo só pode ser usado se não existir noutra especialidade
     * - campos vazios -> não atualiza
     */
    public boolean atualizarEspecialidade(String codigoAtual, String novoCodigo, String novoNome) {
        int idx = indexEspecialidade(codigoAtual);
        if (idx == -1) return false;

        if (novoCodigo != null && !novoCodigo.trim().isEmpty()) {
            int idxNovo = indexEspecialidade(novoCodigo);
            if (idxNovo != -1 && idxNovo != idx) return false; // duplicado
            especialidades[idx].setCodigo(novoCodigo.trim().toUpperCase());
        }

        if (novoNome != null && !novoNome.trim().isEmpty()) {
            especialidades[idx].setNome(novoNome.trim());
        }

        return true;
    }

    /**
     * DELETE: remove especialidade por código.
     * Faz SHIFT para não deixar buracos no array.
     *
     * Nota: dá para reforçar regra (não remover se estiver usada por médicos/sintomas),
     * mas não é obrigatório para o CRUD base.
     */
    public boolean removerEspecialidade(String codigo) {
        int idx = indexEspecialidade(codigo);
        if (idx == -1) return false;

        for (int i = idx; i < nEspecialidades - 1; i++) {
            especialidades[i] = especialidades[i + 1];
        }
        especialidades[nEspecialidades - 1] = null;
        nEspecialidades--;
        return true;
    }

    /**
     * Função auxiliar: devolve o índice da especialidade no array.
     */
    private int indexEspecialidade(String codigo) {
        if (codigo == null) return -1;
        String c = codigo.trim();
        for (int i = 0; i < nEspecialidades; i++) {
            if (especialidades[i].getCodigo().equalsIgnoreCase(c)) return i;
        }
        return -1;
    }

    /**
     * Aumenta capacidade do array quando está cheio (duplicando o tamanho).
     */
    private void ensureCapEspecialidades() {
        if (nEspecialidades < especialidades.length) return;

        Especialidade[] novo = new Especialidade[especialidades.length * 2];
        for (int i = 0; i < especialidades.length; i++) {
            novo[i] = especialidades[i];
        }
        especialidades = novo;
    }

    // CRUD MÉDICO

    /**
     * CREATE: adiciona médico.
     * Regras:
     * - nome não vazio
     * - especialidade tem de existir (validação importante!)
     * - não permitir médico duplicado por nome (não há ID no modelo)
     */
    public boolean adicionarMedico(String nome, String espCodigo, int entrada, int saida, int salario) {
        if (vazio(nome) || vazio(espCodigo)) return false;

        String n = nomeTrim(nome);
        String esp = cod(espCodigo);

        // especialidade tem de existir
        if (procurarEspecialidadePorCodigo(esp) == null) return false;

        // impedir duplicados por nome
        if (procurarMedicoPorNome(n) != null) return false;

        ensureCapMedicos();

        medicos[nMedicos++] = new Medico(
                n,
                esp,
                entrada,
                saida,
                salario,
                false,
                0
        );
        return true;
    }

    /**
     * READ: procurar médico por nome.
     */
    public Medico procurarMedicoPorNome(String nome) {
        int idx = indexMedico(nome);
        return (idx == -1) ? null : medicos[idx];
    }

    /**
     * UPDATE: atualizar campos do médico.
     * - se novaEsp existir, tem de existir como especialidade
     * - valores "sentinela" (0 ou -1) -> não atualiza
     */
    public boolean atualizarMedico(String nome, String novaEsp, int novaEntrada, int novaSaida, int novoSalario) {
        int idx = indexMedico(nome);
        if (idx == -1) return false;

        if (novaEsp != null && !novaEsp.trim().isEmpty()) {
            if (procurarEspecialidadePorCodigo(novaEsp) == null) return false;
            medicos[idx].setEspecialidade(novaEsp.trim().toUpperCase());
        }

        if (novaEntrada > 0) medicos[idx].setHoraEntrada(novaEntrada);
        if (novaSaida > 0) medicos[idx].setHoraSaida(novaSaida);
        if (novoSalario >= 0) medicos[idx].setSalario(novoSalario);

        return true;
    }

    /**
     * DELETE: remover médico por nome.
     * Faz SHIFT no array.
     */
    public boolean removerMedico(String nome) {
        int idx = indexMedico(nome);
        if (idx == -1) return false;

        for (int i = idx; i < nMedicos - 1; i++) {
            medicos[i] = medicos[i + 1];
        }
        medicos[nMedicos - 1] = null;
        nMedicos--;
        return true;
    }

    private int indexMedico(String nome) {
        if (nome == null) return -1;
        String n = nome.trim();
        for (int i = 0; i < nMedicos; i++) {
            if (medicos[i].getNome().equalsIgnoreCase(n)) return i;
        }
        return -1;
    }

    private void ensureCapMedicos() {
        if (nMedicos < medicos.length) return;

        Medico[] novo = new Medico[medicos.length * 2];
        for (int i = 0; i < medicos.length; i++) {
            novo[i] = medicos[i];
        }
        medicos = novo;
    }

    // CRUD SINTOMA

    /**
     * CREATE: adicionar sintoma.
     * Regras:
     * - nome e nível não vazios
     * - tem de ter pelo menos 1 especialidade associada
     * - cada código associado tem de existir no array de especialidades
     */
    public boolean adicionarSintoma(String nome, String nivel, String[] codigosEspecialidade) {
        if (vazio(nome) || vazio(nivel)) return false;

        String n = nomeTrim(nome);
        String nv = nomeTrim(nivel);

        // valida + normaliza códigos (já garante que existem)
        String[] codsOk = validarCodigosEspecialidade(codigosEspecialidade);
        if (codsOk == null) return false;

        if (procurarSintomaPorNome(n) != null) return false;

        ensureCapSintomas();
        sintomas[nSintomas++] = new Sintoma(n, nv, codsOk);
        return true;
    }

    /**
     * READ: procurar sintoma por nome.
     */
    public Sintoma procurarSintomaPorNome(String nome) {
        int idx = indexSintoma(nome);
        return (idx == -1) ? null : sintomas[idx];
    }

    /**
     * UPDATE: alterar o nível do sintoma com validação de valores permitidos.
     */
    public boolean atualizarNivelSintoma(String nome, String novoNivel) {
        int idx = indexSintoma(nome);
        if (idx == -1) return false;

        if (!NivelUrgencia.isValido(novoNivel)) {
            System.out.println("⚠️ Nível inválido. Use Verde, Amarelo ou Vermelho.");
            return false;
        }

        sintomas[idx].setNivelUrgencia(NivelUrgencia.normalizar(novoNivel));
        return true;
    }

    /**
     * UPDATE: alterar o nome de um sintoma existente.
     * Verifica se o novo nome já está em uso para evitar duplicados.
     */
    public boolean atualizarNomeSintoma(String nomeAntigo, String novoNome) {
        if (vazio(novoNome)) return false;

        int idxOriginal = indexSintoma(nomeAntigo);
        if (idxOriginal == -1) return false;

        // Verifica se já existe outro sintoma com o novo nome proposto
        if (procurarSintomaPorNome(novoNome) != null) {
            System.out.println("⚠️ Já existe um sintoma com o nome: " + novoNome);
            return false;
        }

        sintomas[idxOriginal].setNome(nomeTrim(novoNome));
        return true;
    }

    /**
     * UPDATE: alterar as especialidades associadas.
     * Regras: array não vazio e todos os códigos têm de existir.
     */
    public boolean atualizarEspecialidadesSintoma(String nome, String[] novosCodigos) {
        int idx = indexSintoma(nome);
        if (idx == -1) return false;

        if (novosCodigos == null || novosCodigos.length == 0) return false;

        for (String c : novosCodigos) {
            if (c == null || c.trim().isEmpty()) return false;
            if (procurarEspecialidadePorCodigo(c) == null) return false;
        }

        sintomas[idx].setCodigoEspecialidade(novosCodigos);
        return true;
    }

    /**
     * DELETE: remover sintoma por nome (shift).
     */
    public boolean removerSintoma(String nome) {
        int idx = indexSintoma(nome);
        if (idx == -1) return false;

        for (int i = idx; i < nSintomas - 1; i++) {
            sintomas[i] = sintomas[i + 1];
        }
        sintomas[nSintomas - 1] = null;
        nSintomas--;
        return true;
    }

    private int indexSintoma(String nome) {
        if (nome == null) return -1;
        String n = nome.trim();
        for (int i = 0; i < nSintomas; i++) {
            if (sintomas[i].getNome().equalsIgnoreCase(n)) return i;
        }
        return -1;
    }

    private void ensureCapSintomas() {
        if (nSintomas < sintomas.length) return;

        Sintoma[] novo = new Sintoma[sintomas.length * 2];
        for (int i = 0; i < sintomas.length; i++) {
            novo[i] = sintomas[i];
        }
        sintomas = novo;
    }

    // CRUD UTENTE (ordem de chegada)

    /**
     * CREATE: adiciona utente.
     * O número é gerado automaticamente (ordem de chegada).
     */
//    public Utente adicionarUtente(String nome, int idade, String sintoma, String nivelUrgencia) {
//        if (nome == null || nome.trim().isEmpty()) return null;
//        if (idade < 0) return null;
//        if (sintoma == null || sintoma.trim().isEmpty()) return null;
//        if (nivelUrgencia == null || nivelUrgencia.trim().isEmpty()) return null;
//
//        ensureCapUtentes();
//
//        int numero = proximoNumeroUtente++; // ordem de chegada
//        Utente u = new Utente(numero, nome.trim(), idade, sintoma.trim(), nivelUrgencia.trim());
//        utentes[nUtentes++] = u;
//        return u;
//    }

    /**
     * Admissão inicial: guarda apenas nome e idade.
     */
    public Utente admitirUtenteSimples(String nome, int idade) {
        if (vazio(nome) || idade < 0) return null;

        ensureCapUtentes();

        int numero = proximoNumeroUtente++;
        Utente u = new Utente(numero, nomeTrim(nome), idade);
        utentes[nUtentes++] = u;
        return u;
    }

    public Utente procurarUtentePorNumero(int numero) {
        int idx = indexUtente(numero);
        return (idx == -1) ? null : utentes[idx];
    }

    public boolean atualizarUtente(int numero, String novoNome, int novaIdade, String novoSintoma, String novoNivel) {
        int idx = indexUtente(numero);
        if (idx == -1) return false;

        if (novoNome != null && !novoNome.trim().isEmpty()) utentes[idx].setNome(novoNome.trim());
        if (novaIdade >= 0) utentes[idx].setIdade(novaIdade);
        if (novoSintoma != null && !novoSintoma.trim().isEmpty()) utentes[idx].setSintoma(novoSintoma.trim());
        if (novoNivel != null && !novoNivel.trim().isEmpty()) utentes[idx].setNivelUrgencia(novoNivel.trim());

        return true;
    }

    public boolean removerUtente(int numero) {
        int idx = indexUtente(numero);
        if (idx == -1) return false;

        for (int i = idx; i < nUtentes - 1; i++) {
            utentes[i] = utentes[i + 1];
        }
        utentes[nUtentes - 1] = null;
        nUtentes--;
        return true;
    }

    private int indexUtente(int numero) {
        for (int i = 0; i < nUtentes; i++) {
            if (utentes[i].getNumero() == numero) return i;
        }
        return -1;
    }

    private void ensureCapUtentes() {
        if (nUtentes < utentes.length) return;

        Utente[] novo = new Utente[utentes.length * 2];
        for (int i = 0; i < utentes.length; i++) {
            novo[i] = utentes[i];
        }
        utentes = novo;
    }
// Em GestaoHOSP.java

    /**
     * Metodo exclusivo para carregar dados do ficheiro (restaura o estado anterior).
     */
    public void carregarUtenteDoFicheiro(int numero, String nome, int idade, String sintoma, String nivel, int tempoEspera) {
        ensureCapUtentes();

        Utente u = new Utente(numero, nome, idade);
        u.setSintoma(sintoma);
        u.setNivelUrgencia(nivel);

        // Precisas de garantir que a classe Utente tem um setter para o tempoEspera,
        // ou fazes um ciclo: for(int i=0; i<tempoEspera; i++) u.incrementarTempoEspera();
        // O ideal é adicionar setTempoEsperaNivel(int t) na classe Utente.
        // u.setTempoEsperaNivel(tempoEspera);

        utentes[nUtentes++] = u;

        // Atualizar o contador automático para não gerar números repetidos
        if (numero >= proximoNumeroUtente) {
            proximoNumeroUtente = numero + 1;
        }
    }

    /**
     * Verifica se existem utentes marcados como [TRANSFERIDO] ou [ATENDIDO] na sala de espera.
     * Se houver, move-os para o histórico e remove-os da sala de espera.
     * Este metodo é crucial para libertar espaço nos arrays e atualizar as estatísticas.
     */
    public void processarTransferencias() {
        // Percorremos de trás para a frente para que o shift do array (removerUtentePorIndice)
        // não salte elementos quando um é removido.
        for (int i = nUtentes - 1; i >= 0; i--) {
            Utente u = utentes[i];

            // Verifica se o utente terminou o seu percurso no hospital
            if (u.getNome().contains("[TRANSFERIDO]") || u.getNome().contains("[ATENDIDO]")) {

                // 1. Adicionar ao histórico para as estatísticas (Média Diária, Top 3, etc.)
                adicionarAoHistorico(u);

                // 2. Remover da sala de espera física
                removerUtentePorIndice(i);
            }
        }
    }

    public void adicionarAoHistorico(Utente u) {
        if (nHistorico >= historicoUtentes.length) {
            Utente[] novo = new Utente[historicoUtentes.length * 2];
            for(int k=0; k<historicoUtentes.length; k++) novo[k] = historicoUtentes[k];
            historicoUtentes = novo;
        }
        historicoUtentes[nHistorico++] = u;
    }

    private void removerUtentePorIndice(int indice) {
        for (int i = indice; i < nUtentes - 1; i++) {
            utentes[i] = utentes[i + 1];
        }
        utentes[nUtentes - 1] = null;
        nUtentes--;
    }
    // Helpers

    private boolean vazio(String s) {
        return s == null || s.trim().isEmpty();
    }

    // normaliza códigos (ex: " card " -> "CARD")
    private String cod(String s) {
        return s.trim().toUpperCase();
    }

    // normaliza nomes (só trim)
    private String nomeTrim(String s) {
        return s.trim();
    }

    /**
     * Valida e normaliza uma lista de códigos de especialidade.
     * - devolve null se for inválida (vazia, com elementos vazios, ou código inexistente)
     * - devolve array novo com códigos em UPPERCASE se for válida
     */
    private String[] validarCodigosEspecialidade(String[] codigos) {
        if (codigos == null || codigos.length == 0) return null;

        String[] out = new String[codigos.length];
        for (int i = 0; i < codigos.length; i++) {
            if (vazio(codigos[i])) return null;

            String c = cod(codigos[i]);
            if (procurarEspecialidadePorCodigo(c) == null) return null;

            out[i] = c;
        }
        return out;
    }

    /**
     * Chama a lógica de cálculo para atualizar os níveis de urgência de todos os utentes
     * com base no tempo de espera.
     */
    public boolean verificarAlteracoesUrgencia() {
        // Passamos o array "cru" e a quantidade de utentes para o Controlador
        return Calculos.atualizarNiveisUrgencia(this.utentes, this.nUtentes);
    }
}
