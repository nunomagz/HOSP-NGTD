package Configurações;

public class Configuracoes {

    // Gestao de ficheios
    private static String caminhoficheiro = "Dados/";
    private static String nomeFicheiroMedicos = "medicos.txt";
    private static String nomeFicheiroSintomas = "sintomas.txt";
    private static String nomeFicheiroEspecialidade = "especialidades.txt";
    private static String separadorFicheiro = ";";

    // Tempo de consulta
    private static int tempoConsultaBaixa = 1;
    private static int tempoConsultaMedia = 2;
    private static int tempoConsultaUrgente = 3;

    // elevação de nivel de urgencia

    private static int LimiteEsperaVerdeParaAmarelo = 3;
    private static int LimiteEsperaAmareloParaVermelho = 3;
    private static int LimiteEsperaVermelhoSaida = 2;

    //Regras de descanso
    private static int horasTrabalhoParaDescanso = 5;
    private static int tempoDescanso = 1;

    // Segurança

    private static String password = "Admin123";

    //Getters e Setters


    public static String getCaminhoficheiro() {
        return caminhoficheiro;
    }

    public static void setCaminhoficheiro(String caminhoficheiro) {
        Configuracoes.caminhoficheiro = caminhoficheiro;
    }

    public static String getNomeFicheiroMedicos() {
        return nomeFicheiroMedicos;
    }

    public static void setNomeFicheiroMedicos(String nomeFicheiroMedicos) {
        Configuracoes.nomeFicheiroMedicos = nomeFicheiroMedicos;
    }

    public static String getNomeFicheiroSintomas() {
        return nomeFicheiroSintomas;
    }

    public static void setNomeFicheiroSintomas(String nomeFicheiroSintomas) {
        Configuracoes.nomeFicheiroSintomas = nomeFicheiroSintomas;
    }

    public static String getNomeFicheiroEspecialidade() {
        return nomeFicheiroEspecialidade;
    }

    public static void setNomeFicheiroEspecialidade(String nomeFicheiroEspecialidade) {
        Configuracoes.nomeFicheiroEspecialidade = nomeFicheiroEspecialidade;
    }

    public static String getSeparadorFicheiro() {
        return separadorFicheiro;
    }

    public static void setSeparadorFicheiro(String separadorFicheiro) {
        Configuracoes.separadorFicheiro = separadorFicheiro;
    }

    public static int getTempoConsultaBaixa() {
        return tempoConsultaBaixa;
    }

    public static void setTempoConsultaBaixa(int tempoConsultaBaixa) {
        Configuracoes.tempoConsultaBaixa = tempoConsultaBaixa;
    }

    public static int getTempoConsultaMedia() {
        return tempoConsultaMedia;
    }

    public static void setTempoConsultaMedia(int tempoConsultaMedia) {
        Configuracoes.tempoConsultaMedia = tempoConsultaMedia;
    }

    public static int getTempoConsultaUrgente() {
        return tempoConsultaUrgente;
    }

    public static void setTempoConsultaUrgente(int tempoConsultaUrgente) {
        Configuracoes.tempoConsultaUrgente = tempoConsultaUrgente;
    }

    public static int getLimiteEsperaVerdeParaAmarelo() {
        return LimiteEsperaVerdeParaAmarelo;
    }

    public static void setLimiteEsperaVerdeParaAmarelo(int limiteEsperaVerdeParaAmarelo) {
        LimiteEsperaVerdeParaAmarelo = limiteEsperaVerdeParaAmarelo;
    }

    public static int getLimiteEsperaAmareloParaVermelho() {
        return LimiteEsperaAmareloParaVermelho;
    }

    public static void setLimiteEsperaAmareloParaVermelho(int limiteEsperaAmareloParaVermelho) {
        LimiteEsperaAmareloParaVermelho = limiteEsperaAmareloParaVermelho;
    }

    public static int getLimiteEsperaVermelhoSaida() {
        return LimiteEsperaVermelhoSaida;
    }

    public static void setLimiteEsperaVermelhoSaida(int limiteEsperaVermelhoSaida) {
        LimiteEsperaVermelhoSaida = limiteEsperaVermelhoSaida;
    }

    public static int getHorasTrabalhoParaDescanso() {
        return horasTrabalhoParaDescanso;
    }

    public static void setHorasTrabalhoParaDescanso(int horasTrabalhoParaDescanso) {
        Configuracoes.horasTrabalhoParaDescanso = horasTrabalhoParaDescanso;
    }

    public static int getTempoDescanso() {
        return tempoDescanso;
    }

    public static void setTempoDescanso(int tempoDescanso) {
        Configuracoes.tempoDescanso = tempoDescanso;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        Configuracoes.password = password;
    }
}
