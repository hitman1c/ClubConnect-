package clubconnect.ui;

import clubconnect.dao.ClubDAO;
import clubconnect.models.Club;
import clubconnect.models.Membership;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.AbstractBorder;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * ClubManagementPanel - Modern UI, matches AdminPanel theme.
 * Full CRUD for clubs, shows all info, responsive layout, best colors & buttons.
 */
public class ClubManagementPanel extends JPanel {

    // === THEME COLORS & FONTS ===
    private final Color primaryColor = new Color(175, 0, 0);
    private final Color accentColor = new Color(220, 0, 0);
    private final Color backgroundColor = Color.WHITE;
    private final Font fontTitle = new Font("Segoe UI", Font.BOLD, 22);
    private final Font fontButton = new Font("Segoe UI", Font.BOLD, 14);
    private final Font fontTable = new Font("Segoe UI", Font.PLAIN, 14);

    private MainFrame parent;
    private DefaultTableModel model;
    private JTable clubTable;
    private ClubDAO dao = new ClubDAO();

    public ClubManagementPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(backgroundColor);
        setBorder(new EmptyBorder(20, 20, 20, 20)); // Padding for top-level panel

        // === TOP BAR ===
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(primaryColor);
        topBar.setBorder(new EmptyBorder(12, 20, 12, 20));
        JLabel title = new JLabel("Manage Clubs");
        title.setForeground(Color.WHITE);
        title.setFont(fontTitle);
        JButton backBtn = styledButton("← Back", Color.WHITE, primaryColor);
        backBtn.setBorder(new RoundedBorder(15));
        backBtn.addActionListener(e -> parent.showCard("admin"));
        topBar.add(title, BorderLayout.WEST);
        topBar.add(backBtn, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // === CLUB TABLE ===
        model = new DefaultTableModel(new Object[]{
            "ID", "Club Name", "Category", "Description", "Created By", "Archived", "Members"
        }, 0);
        clubTable = new JTable(model) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        clubTable.setRowHeight(30);
        clubTable.setFont(fontTable);
        clubTable.setFillsViewportHeight(true);
        clubTable.setShowGrid(true);
        clubTable.setGridColor(new Color(200, 200, 200));
        // Alternate row colors for readability
        clubTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(245, 245, 245) : Color.WHITE);
                }
                return c;
            }
        });
        JScrollPane scroll = new JScrollPane(clubTable);
        scroll.setBorder(BorderFactory.createTitledBorder("Clubs"));
        add(scroll, BorderLayout.CENTER);

        // === ACTIONS PANEL ===
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        actions.setBackground(backgroundColor);

        JButton createBtn = styledButton("Create", Color.WHITE, primaryColor);
        JButton editBtn = styledButton("Edit", Color.WHITE, primaryColor);
        JButton deleteBtn = styledButton("Delete", Color.WHITE, primaryColor);
        JButton refreshBtn = styledButton("Refresh", Color.WHITE, primaryColor);

        createBtn.setToolTipText("Create a new club");
        editBtn.setToolTipText("Edit selected club");
        deleteBtn.setToolTipText("Delete selected club");
        refreshBtn.setToolTipText("Reload club list");

        createBtn.addActionListener(e -> doCreate());
        editBtn.addActionListener(e -> doEdit());
        deleteBtn.addActionListener(e -> doDelete());
        refreshBtn.addActionListener(e -> load());

        actions.add(createBtn); actions.add(editBtn); actions.add(deleteBtn); actions.add(refreshBtn);

        add(actions, BorderLayout.SOUTH);

        load();
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
        btn.setBorder(new RoundedBorder(10));
        btn.setPreferredSize(new Dimension(120, 40));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(accentColor); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(bg); }
        });
        return btn;
    }

    /**
     * Custom border class for rounded corners.
     */
    public static class RoundedBorder extends AbstractBorder {
        private int radius;
        public RoundedBorder(int radius) { this.radius = radius; }
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(c.getBackground());
            g.drawRoundRect(x, y, width-1, height-1, radius, radius);
        }
    }

    /**
     * Loads all clubs from DB and populates table.
     */
    private void load() {
        model.setRowCount(0);
        List<Club> clubs = dao.listAll();
        for (Club c : clubs) {
            int memberCount = (c.getMembers() != null) ? c.getMembers().size() : 0;
            model.addRow(new Object[]{
                    c.getId(),
                    c.getName(),
                    c.getCategory(),
                    c.getDescription(),
                    c.getCreatedBy(),
                    c.isArchived() ? "Yes" : "No",
                    memberCount
            });
        }
    }

    /**
     * Shows dialog to create a new club.
     */
    private void doCreate() {
        JTextField name = new JTextField();
        JTextField category = new JTextField();
        JTextArea desc = new JTextArea(4, 20);
        JCheckBox archived = new JCheckBox("Archived");

        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 7));
        panel.add(new JLabel("Club Name:"));
        panel.add(name);
        panel.add(new JLabel("Category:"));
        panel.add(category);
        panel.add(new JLabel("Description:"));
        panel.add(new JScrollPane(desc));
        panel.add(archived);

        int res = JOptionPane.showConfirmDialog(this, panel, "Create Club", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            int userId = 1; // Should be session user, for demo use 1
            if (name.getText().trim().isEmpty() || category.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and Category are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Club c = new Club(name.getText().trim(), category.getText().trim(), desc.getText().trim(), userId);
            c.setArchived(archived.isSelected());
            boolean ok = dao.create(c);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Club created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                load();
            } else {
                JOptionPane.showMessageDialog(this, "Creation failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Shows dialog to edit selected club.
     */
    private void doEdit() {
        int index = clubTable.getSelectedRow();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Select a club first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(model.getValueAt(index, 0).toString());
        List<Club> clubs = dao.listAll();
        Club selected = null;
        for (Club c : clubs) if (c.getId() == id) selected = c;
        if (selected == null) return;

        JTextField name = new JTextField(selected.getName());
        JTextField category = new JTextField(selected.getCategory());
        JTextArea desc = new JTextArea(selected.getDescription(), 4, 20);
        JCheckBox archived = new JCheckBox("Archived", selected.isArchived());

        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 7));
        panel.add(new JLabel("Club Name:"));
        panel.add(name);
        panel.add(new JLabel("Category:"));
        panel.add(category);
        panel.add(new JLabel("Description:"));
        panel.add(new JScrollPane(desc));
        panel.add(archived);

        int res = JOptionPane.showConfirmDialog(this, panel, "Edit Club", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            if (name.getText().trim().isEmpty() || category.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and Category are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            selected.setName(name.getText().trim());
            selected.setCategory(category.getText().trim());
            selected.setDescription(desc.getText().trim());
            selected.setArchived(archived.isSelected());
            boolean ok = dao.update(selected);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Club updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                load();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Deletes selected club after confirmation.
     */
    private void doDelete() {
        int index = clubTable.getSelectedRow();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Select a club first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(model.getValueAt(index, 0).toString());
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this club?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = dao.delete(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Club deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                load();
            } else {
                JOptionPane.showMessageDialog(this, "Deletion failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}