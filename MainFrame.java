package clubconnect.ui;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import clubconnect.models.User; // Make sure this is imported

public class MainFrame extends JFrame {
    private CardLayoutPanel cards;
    private User currentUser; // Holds the logged-in user session

    public MainFrame() {
        setTitle("ClubConnect - University Clubs Management System");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        cards = new CardLayoutPanel(this);
        add(cards);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int opt = JOptionPane.showConfirmDialog(MainFrame.this,
                        "Exit application?", "Confirm Exit", JOptionPane.YES_NO_OPTION);
                if (opt == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    public void showCard(String name) {
        cards.show(name);
    }

    public CardLayoutPanel getCards() {
        return cards;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}