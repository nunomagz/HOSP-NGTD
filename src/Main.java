import Menu.Menu;

/**
 * Classe principal da aplicacao de gestao TVDE.
 * Ponto de entrada do programa.
 */
public class Main {

    /**
     * Metodo principal que inicia a aplicacao.
     *
     * @param args Argumentos da linha de comandos
     */
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                              ║");
        System.out.println("║                                                              ║");
        System.out.println("║               SISTEMA DE GESTÃO DE UM HOSPITAL               ║");
        System.out.println("║                                                              ║");
        System.out.println("║                                                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("A iniciar o sistema...");

        Menu menu = new Menu();
        menu.iniciar();

        System.out.println("\nSistema encerrado.");
    }
}
