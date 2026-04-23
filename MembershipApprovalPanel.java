import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * MembershipApprovalPanel class handles the approval or rejection of member requests.
 * It provides a user interface for reviewing requests and making decisions.
 */
public class MembershipApprovalPanel extends JPanel {
    private JTable requestTable;
    private JButton approveButton;
    private JButton rejectButton;

    public MembershipApprovalPanel() {
        setLayout(new BorderLayout());
        String[] columnNames = {"Member Name", "Request Date", "Status"};
        Object[][] data = {};  // Data to be populated from the member requests.
        requestTable = new JTable(data, columnNames);
        add(new JScrollPane(requestTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        approveButton = new JButton("Approve");
        rejectButton = new JButton("Reject");

        approveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleApproval();
            }
        });

        rejectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRejection();
            }
        });

        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleApproval() {
        int selectedRow = requestTable.getSelectedRow();
        if (selectedRow != -1) {
            // Logic to approve the member request.
            // This should include updating the database/state and notifying the member.
            JOptionPane.showMessageDialog(this, "Member approved!");
        } else {
            JOptionPane.showMessageDialog(this, "Please select a member request to approve.");
        }
    }

    private void handleRejection() {
        int selectedRow = requestTable.getSelectedRow();
        if (selectedRow != -1) {
            // Logic to reject the member request.
            // This should include updating the database/state and notifying the member.
            JOptionPane.showMessageDialog(this, "Member rejected!");
        } else {
            JOptionPane.showMessageDialog(this, "Please select a member request to reject.");
        }
    }
}