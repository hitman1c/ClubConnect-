package clubconnect.ui;
import javax.swing.*; import java.awt.*;
public class DiscussionBoard extends JPanel {
    public DiscussionBoard(MainFrame parent) {
        setLayout(new BorderLayout());
        add(new JLabel("Discussion Board", SwingConstants.CENTER), BorderLayout.NORTH);
        add(new JLabel("Discussion forum stub"), BorderLayout.CENTER);
        JButton back=new JButton("Back"); back.addActionListener(e->parent.showCard("member")); add(back, BorderLayout.SOUTH);
    }
}
