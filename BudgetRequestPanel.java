package clubconnect.ui;

import clubconnect.util.Config;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Approve Budgets Panel - Fully matches your database schema.
 * Shows all budget requests, joined with events for event title.
 * Themed to match admin panel.
 * Now also shows all events (with pending budgets), and allows approving/rejecting directly.
 */
public class BudgetRequestPanel extends JPanel {
    private MainFrame parent;
    private DefaultTableModel model;
    private JTable table;

    public BudgetRequestPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top Bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(175, 0, 0));
        topBar.setBorder(new EmptyBorder(12, 20, 12, 20));
        JLabel title = new JLabel("Approve Budgets");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        JButton backBtn = styledButton("← Back", Color.WHITE, new Color(175, 0, 0));
        backBtn.addActionListener(e -> parent.showCard("admin"));
        topBar.add(title, BorderLayout.WEST);
        topBar.add(backBtn, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel(new Object[]{
                "Budget ID", "Event", "Requested By", "Amount", "Status", "Notes", "Event Date", "Venue"
        }, 0);
        table = new JTable(model) { public boolean isCellEditable(int r, int c) { return false; } };
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Budget Requests"));
        add(scroll, BorderLayout.CENTER);

        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 12));
        JButton approveBtn = styledButton("Approve", Color.WHITE, new Color(175, 0, 0));
        JButton rejectBtn = styledButton("Reject", Color.WHITE, new Color(175, 0, 0));
        JButton refreshBtn = styledButton("Refresh", Color.WHITE, new Color(175, 0, 0));
        approveBtn.addActionListener(e -> handleApprove("approved"));
        rejectBtn.addActionListener(e -> handleApprove("rejected"));
        refreshBtn.addActionListener(e -> load());
        actions.add(approveBtn); actions.add(rejectBtn); actions.add(refreshBtn);
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
        for (BudgetRow r : getRequests()) {
            model.addRow(new Object[]{
                r.id,
                r.eventTitle,
                r.requestedBy,
                r.amount,
                r.status,
                r.notes,
                r.eventDate,
                r.venue
            });
        }
    }

    /**
     * Helper structure for UI table row (includes event name, event date, venue).
     */
    private static class BudgetRow {
        int id;
        String eventTitle;
        String requestedBy;
        double amount;
        String status;
        String notes;
        String eventDate;
        String venue;
        BudgetRow(int id, String eventTitle, String requestedBy, double amount, String status, String notes, String eventDate, String venue) {
            this.id = id;
            this.eventTitle = eventTitle;
            this.requestedBy = requestedBy;
            this.amount = amount;
            this.status = status;
            this.notes = notes;
            this.eventDate = eventDate;
            this.venue = venue;
        }
    }

    // Budget requests joined to events and users for full info, and event date/venue shown in table.
    private List<BudgetRow> getRequests() {
        List<BudgetRow> out = new ArrayList<>();
        String sql =
            "SELECT b.id, e.title AS event_title, u.full_name AS requested_by, b.amount, b.status, b.notes, e.date_time, e.venue " +
            "FROM budgets b " +
            "LEFT JOIN events e ON b.event_id = e.id " +
            "LEFT JOIN users u ON b.requested_by = u.id " +
            "ORDER BY b.status ASC, b.id DESC";
        try (Connection c = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER, Config.MYSQL_PASSWORD);
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String eventDate = "";
                Timestamp ts = rs.getTimestamp("date_time");
                if (ts != null) {
                    eventDate = ts.toLocalDateTime().toString().replace('T', ' ');
                }
                out.add(new BudgetRow(
                    rs.getInt("id"),
                    rs.getString("event_title") != null ? rs.getString("event_title") : "(Unknown Event)",
                    rs.getString("requested_by") != null ? rs.getString("requested_by") : "(Unknown User)",
                    rs.getDouble("amount"),
                    rs.getString("status"),
                    rs.getString("notes"),
                    eventDate,
                    rs.getString("venue") != null ? rs.getString("venue") : ""
                ));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return out;
    }

    private void handleApprove(String status) {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a request first."); return; }
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        try (Connection c = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER, Config.MYSQL_PASSWORD)) {
            PreparedStatement ps = c.prepareStatement("UPDATE budgets SET status=? WHERE id=?");
            ps.setString(1, status);
            ps.setInt(2, id);
            int ok = ps.executeUpdate();
            JOptionPane.showMessageDialog(this, ok > 0 ? "Request " + status + "!" : "Failed!");
            load();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "DB Error: "+e.getMessage()); }
    }
}