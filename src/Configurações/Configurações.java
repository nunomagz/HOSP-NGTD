package Configurações;

public class Configurações {
    private static String caminhoficheiro = "Dados/";
    private static String nomeFicheiroMedicos = "medicos.txt";
    private static String nomeFicheiroSintomas = "sintomas.txt";
    private static String nomeFicheiroEspecialidade = "especialidades.txt";
    private static String separadorFicheiro = ";";

    // Getters
    public static String getCaminhoficheiro() {
        return caminhoficheiro;
    }

    public static String getNomeFicheiroMedicos() {
        return nomeFicheiroMedicos;
    }

    public static String getNomeFicheiroSintomas() {
        return nomeFicheiroSintomas;
    }

    public static String getNomeFicheiroEspecialidade() {
        return nomeFicheiroEspecialidade;
    }

    public static String getSeparadorFicheiro() {
        return separadorFicheiro;
    }
}
