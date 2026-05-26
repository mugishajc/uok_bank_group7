import storage.DatabaseInitializer;
import ui.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        DatabaseInitializer.initialize();
        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
