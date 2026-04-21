package clubconnect.ui;

import clubconnect.models.User;
import clubconnect.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * ManageUsersPanel - Full-featured panel for admin user management.
 * Features:
 * - Approve/unapprove users
 * - Change user role (admin, leader, member)
 * - Delete users
 * - Table with all user info
 * - Styled buttons and responsive layout
 */
public class ManageUsersPanel extends JPanel {
    private MainFrame parent;
    private UserService userService = new UserService();
    private DefaultTableModel model;
    private JTable userTable;

    public ManageUsersPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top Bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(175, 0, 0));
        topBar.setBorder(new EmptyBorder(12, 20, 12, 20));
        JLabel title = new JLabel("Manage Users");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        JButton backBtn = styledButton("← Back", Color.WHITE, new Color(175, 0, 0));
        backBtn.addActionListener(e -> parent.showCard("admin"));
        topBar.add(title, BorderLayout.WEST);
        topBar.add(backBtn, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // User Table
        model = new DefaultTableModel(new Object[]{
                "ID", "Username", "Name", "Email", "Student ID", "Role", "Approved"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        userTable = new JTable(model);
        userTable.setRowHeight(28);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(userTable);
        scroll.setBorder(BorderFactory.createTitledBorder("Users"));
        add(scroll, BorderLayout.CENTER);

        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 12));
        JButton approveBtn = styledButton("Approve", Color.WHITE, new Color(175, 0, 0));
        JButton unapproveBtn = styledButton("Unapprove", Color.WHITE, new Color(175, 0, 0));
        JButton changeRoleBtn = styledButton("Change Role", Color.WHITE, new Color(175, 0, 0));
        JButton deleteBtn = styledButton("Delete", Color.WHITE, new Color(175, 0, 0));
        JButton refreshBtn = styledButton("Refresh", Color.WHITE, new Color(175, 0, 0));

        approveBtn.addActionListener(e -> handleApprove(true));
        unapproveBtn.addActionListener(e -> handleApprove(false));
        changeRoleBtn.addActionListener(e -> handleChangeRole());
        deleteBtn.addActionListener(e -> handleDelete());
        refreshBtn.addActionListener(e -> load());

        actions.add(approveBtn);
        actions.add(unapproveBtn);
        actions.add(changeRoleBtn);
        actions.add(deleteBtn);
        actions.add(refreshBtn);

        add(actions, BorderLayout.SOUTH);
        load();
    }

    private JButton styledButton(String text, Color fg, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(7,15,7,15));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(new Color(220, 0, 0)); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(bg); }
        });
        return btn;
    }

    private void load() {
        model.setRowCount(0);
        List<User> users = userService.getAllUsers();
        for (User u : users) {
            model.addRow(new Object[]{
                    u.getId(), u.getUsername(), u.getFullName(), u.getEmail(),
                    u.getStudentId(), u.getRole(), u.isApproved() ? "Yes" : "No"
            });
        }
    }

    private void handleApprove(boolean approve) {
        int row = userTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a user first."); return; }
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        boolean ok = approve ? userService.approveUser(id) : userService.unapproveUser(id);
        JOptionPane.showMessageDialog(this, ok ? (approve ? "User approved!" : "User unapproved!") : "Operation failed!");
        load();
    }

    private void handleChangeRole() {
        int row = userTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a user first."); return; }
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        String currentRole = model.getValueAt(row, 5).toString();
        String[] roles = {"admin", "leader", "member"};
        String newRole = (String) JOptionPane.showInputDialog(this, "Select new role:", "Change Role",
                JOptionPane.QUESTION_MESSAGE, null, roles, currentRole);
        if (newRole != null && !newRole.equals(currentRole)) {
            boolean ok = userService.updateUserRole(id, newRole);
            JOptionPane.showMessageDialog(this, ok ? "Role updated!" : "Failed to update role!");
            load();
        }
    }

    private void handleDelete() {
        int row = userTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a user first."); return; }
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this user?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = userService.deleteUser(id);
            JOptionPane.showMessageDialog(this, ok ? "User deleted!" : "Failed to delete user!");
            load();
        }
    }
}