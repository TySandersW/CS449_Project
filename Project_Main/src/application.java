import javax.swing.SwingUtilities;

public class application {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SolitaireGUI::createAndShowGUI);
    }
}
