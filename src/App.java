import javax.swing.SwingUtilities;

/** Starts the application */
public class App {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> GameMain.createAndShowGUI());
    }
}
