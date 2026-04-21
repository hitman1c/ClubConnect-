package clubconnect.ui;

import clubconnect.dao.ClubDAO;
import clubconnect.dao.UserDAO;
import clubconnect.models.Club;
import clubconnect.models.User;
import clubconnect.service.NotificationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Modern Notifications Panel:
 * - Attractive UI (material look, colors, spacing, icons)
 * - Admin can select recipient group and send notifications
 * - Dashboard shows incoming notifications (with reply option for users)
 */
public class NotificationsPanel extends JPanel {
    private MainFrame parent;
    private ClubDAO clubDAO = new ClubDAO();
    private UserDAO userDAO = new UserDAO();
    private DefaultTableModel inboxModel;
    private JTable inboxTable;

    // THEME CONSTANTS
    private final Color primaryColor = new Color(175, 0, 0);
    private final Color accentColor = new Color(220, 0, 0);
    private final Color lightBg = new Color(245,245,245);
    private final Font fontTitle = new Font("Segoe UI", Font.BOLD, 24);
    private final Font fontSubtitle = new Font("Segoe UI", Font.BOLD, 17);
    private final Font fontButton = new Font("Segoe UI", Font.BOLD, 14);
    private final Font fontTable = new Font("Segoe UI", Font.PLAIN, 14);

    public NotificationsPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(lightBg);

        // === TOP BAR ===
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(primaryColor);
        topBar.setBorder(new EmptyBorder(14, 30, 14, 30));
        JLabel title = new JLabel("📢 Notifications Center");
        title.setForeground(Color.WHITE);
        title.setFont(fontTitle);
        JButton backBtn = styledButton("← Back", Color.WHITE, primaryColor);
        backBtn.addActionListener(e -> parent.showCard("admin"));
        topBar.add(title, BorderLayout.WEST);
        topBar.add(backBtn, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // === MAIN SPLIT PANEL (Inbox + Send) ===
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createInboxPanel(), createSendPanel());
        split.setDividerLocation(520);
        split.setResizeWeight(0.55);
        split.setBorder(null);

        add(split, BorderLayout.CENTER);
    }

    // ========= INBOX/DASHBOARD PANEL =========
    private JPanel createInboxPanel() {
        JPanel inboxPanel = new JPanel(new BorderLayout());
        inboxPanel.setBackground(lightBg);
        inboxPanel.setBorder(new EmptyBorder(18,18,18,8));

        JLabel inboxTitle = new JLabel("📥 Incoming Notifications");
        inboxTitle.setFont(fontSubtitle);
        inboxTitle.setForeground(primaryColor);
        inboxTitle.setBorder(new EmptyBorder(0,0,10,0));

        inboxPanel.add(inboxTitle, BorderLayout.NORTH);

        // Table for notifications (simulate message inbox)
        inboxModel = new DefaultTableModel(new Object[]{
            "From", "Title", "Message", "Reply"
        }, 0);

        inboxTable = new JTable(inboxModel) {
            public boolean isCellEditable(int r, int c) { return c==3; }
        };
        inboxTable.setRowHeight(32);
        inboxTable.setFont(fontTable);
        inboxTable.setShowGrid(false);
        inboxTable.setGridColor(new Color(220,220,220));
        inboxTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                 boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (!isSelected) c.setBackground(row % 2 == 0 ? new Color(255, 235, 235) : Color.WHITE);
                return c;
            }
        });

        JScrollPane inboxScroll = new JScrollPane(inboxTable);
        inboxScroll.setBorder(BorderFactory.createLineBorder(primaryColor, 1));
        inboxPanel.add(inboxScroll, BorderLayout.CENTER);

        JButton refreshBtn = styledButton("⟳ Refresh Inbox", Color.WHITE, accentColor);
        refreshBtn.addActionListener(e -> loadInbox());
        inboxPanel.add(refreshBtn, BorderLayout.SOUTH);

        loadInbox();

        return inboxPanel;
    }

    // Simulate incoming notifications (replace with DB for real app)
    private void loadInbox() {
        inboxModel.setRowCount(0);
        // Demo: add a few sample notifications
        inboxModel.addRow(new Object[]{
            "Admin", "Event Reminder", "Don't forget Robotics Club meeting tomorrow!",
            "Reply"
        });
        inboxModel.addRow(new Object[]{
            "Leader", "Budget Approved", "Your budget for Science Fair is approved.", "Reply"
        });
        inboxModel.addRow(new Object[]{
            "Admin", "Welcome!", "Welcome to ClubConnect. Join your favorite clubs.", "Reply"
        });

        // Add action for reply button
        for (int i = 0; i < inboxTable.getRowCount(); i++) {
            inboxTable.getColumn("Reply").setCellEditor(new ButtonEditor(new JCheckBox()));
        }
    }

    // ========= SEND NOTIFICATION PANEL =========
    private JPanel createSendPanel() {
        JPanel sendPanel = new JPanel();
        sendPanel.setLayout(new BoxLayout(sendPanel, BoxLayout.Y_AXIS));
        sendPanel.setBackground(Color.WHITE);
        sendPanel.setBorder(new EmptyBorder(20, 12, 20, 18));

        JLabel sendTitle = new JLabel("✉️ Send Notification");
        sendTitle.setFont(fontSubtitle);
        sendTitle.setForeground(primaryColor);
        sendTitle.setBorder(new EmptyBorder(0,0,10,0));
        sendPanel.add(sendTitle);

        // Recipient selection
        JPanel recipientPanel = new JPanel();
        recipientPanel.setLayout(new BoxLayout(recipientPanel, BoxLayout.Y_AXIS));
        recipientPanel.setBackground(Color.WHITE);

        JRadioButton allMembersBtn = new JRadioButton("All Members");
        JRadioButton clubLeadersBtn = new JRadioButton("Club Leaders");
        JRadioButton adminsBtn = new JRadioButton("Admins");
        JRadioButton specificClubBtn = new JRadioButton("Specific Club Members");

        ButtonGroup group = new ButtonGroup();
        group.add(allMembersBtn);
        group.add(clubLeadersBtn);
        group.add(adminsBtn);
        group.add(specificClubBtn);

        allMembersBtn.setSelected(true);

        JComboBox<Club> clubsDropdown = new JComboBox<>();
        List<Club> clubs = clubDAO.listAll();
        for (Club club : clubs) {
            clubsDropdown.addItem(club);
        }
        clubsDropdown.setVisible(false);

        specificClubBtn.addActionListener(e -> clubsDropdown.setVisible(true));
        clubLeadersBtn.addActionListener(e -> clubsDropdown.setVisible(true));
        allMembersBtn.addActionListener(e -> clubsDropdown.setVisible(false));
        adminsBtn.addActionListener(e -> clubsDropdown.setVisible(false));

        recipientPanel.add(allMembersBtn);
        recipientPanel.add(clubLeadersBtn);
        recipientPanel.add(adminsBtn);
        recipientPanel.add(specificClubBtn);
        recipientPanel.add(new JLabel("Select Club:"));
        recipientPanel.add(clubsDropdown);

        sendPanel.add(recipientPanel);
        sendPanel.add(Box.createVerticalStrut(8));

        // Notification fields
        JTextField titleField = new JTextField("Important Announcement");
        JTextArea bodyField = new JTextArea(5, 28);
        bodyField.setText("Type your message here...");
        bodyField.setLineWrap(true);
        bodyField.setWrapStyleWord(true);

        sendPanel.add(new JLabel("Title:"));
        sendPanel.add(titleField);
        sendPanel.add(Box.createVerticalStrut(7));
        sendPanel.add(new JLabel("Message:"));
        sendPanel.add(new JScrollPane(bodyField));

        sendPanel.add(Box.createVerticalStrut(12));

        JButton sendBtn = styledButton("📤 Send Notification", Color.WHITE, primaryColor);

        sendBtn.addActionListener(e -> {
            List<String> recipients = new ArrayList<>();
            if (allMembersBtn.isSelected()) {
                recipients = getAllUserEmails();
            } else if (clubLeadersBtn.isSelected()) {
                Club selectedClub = (Club) clubsDropdown.getSelectedItem();
                if (selectedClub != null) recipients = getClubLeadersEmails(selectedClub);
            } else if (adminsBtn.isSelected()) {
                recipients = getAdminEmails();
            } else if (specificClubBtn.isSelected()) {
                Club selectedClub = (Club) clubsDropdown.getSelectedItem();
                if (selectedClub != null) recipients = getClubMembersEmails(selectedClub);
            }
            if (recipients != null && !recipients.isEmpty()) {
                NotificationService svc = new NotificationService();
                svc.sendBulk(recipients, titleField.getText(), bodyField.getText(), info -> {
                    System.out.println(info);
                });
                JOptionPane.showMessageDialog(this, "Notification sent to " + recipients.size() + " users.");
            } else {
                JOptionPane.showMessageDialog(this, "No recipients found for selection.");
            }
        });

        sendPanel.add(sendBtn);

        return sendPanel;
    }

    // === Helper Methods to Fetch Emails ===

    private List<String> getAllUserEmails() {
        List<String> emails = new ArrayList<>();
        for (User user : userDAO.findAll()) {
            emails.add(user.getEmail());
        }
        return emails;
    }

    private List<String> getClubLeadersEmails(Club club) {
        List<String> emails = new ArrayList<>();
        for (User user : userDAO.findAll()) {
            if (user.getRole().equalsIgnoreCase("leader")) {
                emails.add(user.getEmail());
            }
        }
        return emails;
    }

    private List<String> getAdminEmails() {
        List<String> emails = new ArrayList<>();
        for (User user : userDAO.findAll()) {
            if (user.getRole().equalsIgnoreCase("admin")) {
                emails.add(user.getEmail());
            }
        }
        return emails;
    }

    private List<String> getClubMembersEmails(Club club) {
        List<String> emails = new ArrayList<>();
        for (User user : userDAO.findAll()) {
            emails.add(user.getEmail());
        }
        return emails;
    }

    /**
     * Styled button: rounded, themed, with hover effect.
     */
    private JButton styledButton(String text, Color fg, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(fontButton);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(9,18,9,18));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(accentColor); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(bg); }
        });
        return btn;
    }

    // ==== ButtonEditor for Reply Button in Inbox Table ====
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = styledButton("↩️ Reply", Color.WHITE, accentColor);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "↩️ Reply" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                int row = inboxTable.getSelectedRow();
                String from = (String) inboxModel.getValueAt(row, 0);
                String subject = (String) inboxModel.getValueAt(row, 1);
                showReplyDialog(from, subject);
            }
            isPushed = false;
            return label;
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    // === Reply Dialog ===
    private void showReplyDialog(String to, String subject) {
        JTextField replyTitle = new JTextField("RE: " + subject);
        JTextArea replyMessage = new JTextArea(5, 28);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Reply to: " + to));
        panel.add(new JLabel("Title:"));
        panel.add(replyTitle);
        panel.add(new JLabel("Message:"));
        panel.add(new JScrollPane(replyMessage));

        int res = JOptionPane.showConfirmDialog(this, panel, "Send Reply", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            // Simulate reply send (in a real app, save to DB and notify recipient)
            JOptionPane.showMessageDialog(this, "Reply sent!");
        }
    }
}