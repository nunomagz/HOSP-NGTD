import Menu.Menu;

/**
 * Classe principal da aplicacao de gestao TVDE.
 * Ponto de entrada do programa.
 */
public class Main {

    /**
     * Metodo principal que inicia a aplicacao.
     *
     * @param args Argumentos da linha de comandos (nao utilizados)
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


        // Criar e iniciar o menu
        Menu menu = new Menu();
        menu.menuInicial();

        System.out.println("\nSistema encerrado.");
    }
}
