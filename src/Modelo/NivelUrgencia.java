package Modelo;

public class NivelUrgencia {

    // Constantes usadas no sistema (valores "oficiais" internamente)
    public static final String VERDE = "Verde";
    public static final String AMARELO = "Amarelo";
    public static final String VERMELHO = "Vermelho";

    public static boolean isValido(String nivel) {
        if (nivel == null) return false;

        String n = nivel.trim();
        return n.equalsIgnoreCase(VERDE)
                || n.equalsIgnoreCase(AMARELO)
                || n.equalsIgnoreCase(VERMELHO);
    }

    /**
     * Normaliza valores que vêm "sujos" ou diferentes no ficheiro.
     * Ex:
     *  - "Vermelha" -> "Vermelho"
     *  - "Laranja"  -> "Amarelo"
     *  - "verde"    -> "Verde"
     *
     * Se não reconhecer, devolve null (linha pode ser ignorada no DataIO).
     */
    public static String normalizar(String nivel) {
        if (nivel == null) return null;

        String n = nivel.trim().toLowerCase();

        if (n.equals("verde")) return VERDE;

        // No ficheiro aparece "Laranja" (nível intermédio) -> mapeamos para AMARELO
        if (n.equals("amarelo") || n.equals("laranja")) return AMARELO;

        // No ficheiro aparece "Vermelha" -> mapeamos para VERMELHO
        if (n.equals("vermelho") || n.equals("vermelha")) return VERMELHO;

        return null;
    }
}
