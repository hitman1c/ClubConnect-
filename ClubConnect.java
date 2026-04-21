package clubconnect;

import clubconnect.db.DatabaseManager;
import clubconnect.util.SampleData;
import clubconnect.ui.MainFrame;
import javax.swing.SwingUtilities;

public class ClubConnect {
    public static void main(String[] args) {
        try {
            DatabaseManager.getInstance();
            SampleData.seed();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Fatal DB init error. Check MySQL driver and Config settings.");
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
            frame.showCard("home"); // Force Home page first
        });
    }
}
