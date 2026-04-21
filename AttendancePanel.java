package clubconnect.ui;
import javax.swing.*; import java.awt.*;
public class AttendancePanel extends JPanel {
    public AttendancePanel(MainFrame parent) {
        setLayout(new BorderLayout());
        add(new JLabel("Attendance", SwingConstants.CENTER), BorderLayout.NORTH);
        add(new JLabel("Attendance marking stub"), BorderLayout.CENTER);
        JButton back=new JButton("Back"); back.addActionListener(e->parent.showCard("leader")); add(back, BorderLayout.SOUTH);
    }
}
