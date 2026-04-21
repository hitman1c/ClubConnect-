package clubconnect.ui;
import javax.swing.*; import java.awt.*;
public class SettingsPanel extends JPanel {
    public SettingsPanel(MainFrame parent) {
        setLayout(new BorderLayout());
        add(new JLabel("Settings", SwingConstants.CENTER), BorderLayout.NORTH);
        add(new JLabel("Settings stub"), BorderLayout.CENTER);
        JButton back=new JButton("Back"); back.addActionListener(e->parent.showCard("home")); add(back, BorderLayout.SOUTH);
    }
}
