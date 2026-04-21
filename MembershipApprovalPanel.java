package clubconnect.ui;
import javax.swing.*; import java.awt.*;
public class MembershipApprovalPanel extends JPanel {
    public MembershipApprovalPanel(MainFrame parent) {
        setLayout(new BorderLayout());
        add(new JLabel("Membership Approvals", SwingConstants.CENTER), BorderLayout.NORTH);
        add(new JLabel("No pending requests (stub)"), BorderLayout.CENTER);
        JButton back=new JButton("Back"); back.addActionListener(e->parent.showCard("leader")); add(back, BorderLayout.SOUTH);
    }
}
