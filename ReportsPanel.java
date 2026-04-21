package clubconnect.ui;

import clubconnect.db.BackupHandler;
import clubconnect.util.Config;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * ReportsPanel - Shows system stats and allows exporting all data to a file (SQL format).
 * Fully themed to match AdminPanel.
 */
public class ReportsPanel extends JPanel {
    private MainFrame parent;

    public ReportsPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top Bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(175, 0, 0));
        topBar.setBorder(new EmptyBorder(12, 20, 12, 20));
        JLabel title = new JLabel("Reports");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JButton backBtn = styledButton("← Back", Color.WHITE, new Color(175, 0, 0));
        backBtn.addActionListener(e -> parent.showCard("admin"));

        topBar.add(title, BorderLayout.WEST);
        topBar.add(backBtn, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Main content (stats grid)
        JPanel statsPanel = new JPanel(new GridLayout(2,2,18,18));
        statsPanel.setBorder(new EmptyBorder(26,40,26,40));
        statsPanel.setBackground(Color.WHITE);

        statsPanel.add(makeStatCard("Total Users", getCount("users")));
        statsPanel.add(makeStatCard("Total Clubs", getCount("clubs")));
        statsPanel.add(makeStatCard("Total Events", getCount("events")));
        statsPanel.add(makeStatCard("Total Resources", getCount("resources")));
        add(statsPanel, BorderLayout.CENTER);

        // Export button
        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 18));
        exportPanel.setBackground(Color.WHITE);
        JButton exportBtn = styledButton("Export All Data", Color.WHITE, new Color(175, 0, 0));
        exportBtn.addActionListener(e -> doExport());
        exportPanel.add(exportBtn);

        add(exportPanel, BorderLayout.SOUTH);
    }

    private JPanel makeStatCard(String title, String value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(245,245,245));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
                new EmptyBorder(18,20,18,20)
        ));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 29));
        lblValue.setForeground(new Color(175, 0, 0));
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        return card;
    }

    private String getCount(String table) {
        try (Connection conn = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER, Config.MYSQL_PASSWORD)) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + table);
            rs.next();
            return String.valueOf(rs.getInt(1));
        } catch (Exception e) { return "0"; }
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

    private void doExport() {
        try {
            File exported = BackupHandler.exportDatabase();
            JOptionPane.showMessageDialog(this,
                "Export complete!\nSaved to:\n" + exported.getAbsolutePath(),
                "Export Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Export failed: " + ex.getMessage(),
                "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}