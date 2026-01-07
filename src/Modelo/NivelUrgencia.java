package Modelo;

public class NivelUrgencia {

        public static final String VERDE = "Verde";
        public static  final String AMARELO = "Amarelo";
        public static final String VERMELHO = "Vermelho";

        public static boolean isValido(String nivel) {
            if (nivel == null) return false;

            String n = nivel.trim();
            return n.equalsIgnoreCase(VERDE)
                    || n.equalsIgnoreCase(AMARELO)
                    || n.equalsIgnoreCase(VERMELHO);
        }

        public static String normalizar(String nivel){
            if (nivel == null) return null;

            String n = nivel.trim().toLowerCase();
            if (n.equals("Verde")) return VERDE;
            if (n.equals("Amarelo")) return AMARELO;
            if (n.equals("Vermelho")) return VERMELHO;

            return null;
        }
}
