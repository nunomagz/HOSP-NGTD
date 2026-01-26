package Configuracoes;

/**
 * Classe responsável por armazenar as configurações globais do sistema.
 */
public class Configuracoes {

    /**
     * Caminho para a pasta onde os ficheiros de texto estão guardados.
     */
    private static String caminhoficheiro = "Dados/";

    /**
     * Nome do ficheiro da classe medicos
     */
    private static String nomeFicheiroMedicos = "medicos.txt";

    /**
     * Nome do ficheiro da classe sintomas
     */
    private static String nomeFicheiroSintomas = "sintomas.txt";

    /**
     * Nome do ficheiro da classe especialidades
     */
    private static String nomeFicheiroEspecialidade = "especialidades.txt";

    /**
     * Caracter utilizado para separar campos dentro dos ficheiros de texto
     */
    private static String separadorFicheiro = ";";

    /**
     * Tempo estimado (unidades de tempo) para uma consulta de prioridade baixa
     */
    private static int tempoConsultaBaixa = 1;

    /**
     * Tempo estimado (unidades de tempo) para uma consulta de prioridade media
     */
    private static int tempoConsultaMedia = 2;

    /**
     * Tempo estimado (unidades de tempo) para uma consulta de prioridade urgente
     */
    private static int tempoConsultaUrgente = 3;

    /**
     * Limite de espera para passar de verde para amarelo
     */
    private static int LimiteEsperaVerdeParaAmarelo = 3;

    /**
     * Limite de espera para passar de amarelo para vermelho
     */
    private static int LimiteEsperaAmareloParaVermelho = 3;

    /**
     * Limite de espera para passar de vermelho para saida
     */
    private static int LimiteEsperaVermelhoSaida = 2;

    /**
     * Horas o médico necessita de trabalhar seguidas para poder descansar
     */
    private static int horasTrabalhoParaDescanso = 5;

    /**
     * Tempo que o médico tem de descanso
     */
    private static int tempoDescanso = 1;

    /**
     * Password necessaria para aceder ao menu gestão de dados e configurações
     */
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