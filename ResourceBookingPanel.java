package clubconnect.ui;

import clubconnect.util.Config;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;

/**
 * Resource Booking Panel for Leader Dashboard
 * - Shows all resources
 * - Allows leader to request new resources (sends to admin)
 * - Back button returns to Leader Dashboard
 */
public class ResourceBookingPanel extends JPanel {
    private MainFrame parent;
    private final Color accent = new Color(175, 0, 0);

    public ResourceBookingPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Resource Booking", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 23));
        title.setForeground(accent);
        title.setBorder(new EmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Table for resources
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Name", "Capacity", "Description", "Available"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(26);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Available Resources"));
        add(scroll, BorderLayout.CENTER);

        // Load resources from DB
        loadResources(model);

        // Booking UI stub (expand as needed for real booking)
        JPanel bookingPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        bookingPanel.setBackground(Color.WHITE);

        JButton bookBtn = new JButton("Book");
        bookBtn.setBackground(accent);
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        bookBtn.setFocusPainted(false);
        bookBtn.addActionListener(e -> {
            int selected = table.getSelectedRow();
            if (selected == -1) {
                JOptionPane.showMessageDialog(this, "Select a resource to book.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String name = model.getValueAt(selected, 0).toString();
            JOptionPane.showMessageDialog(this, "Resource \"" + name + "\" booked! (stub)", "Booking", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton requestBtn = new JButton("Request New Resource");
        requestBtn.setBackground(Color.WHITE);
        requestBtn.setForeground(accent);
        requestBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        requestBtn.setFocusPainted(false);
        requestBtn.addActionListener(e -> showResourceRequestDialog(model));

        JButton backBtn = new JButton("Back");
        backBtn.setBackground(Color.WHITE);
        backBtn.setForeground(accent);
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> parent.showCard("leader"));

        bookingPanel.add(bookBtn);
        bookingPanel.add(requestBtn);
        bookingPanel.add(backBtn);

        add(bookingPanel, BorderLayout.SOUTH);
    }

    private void loadResources(DefaultTableModel model) {
        model.setRowCount(0);
        try (Connection c = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER, Config.MYSQL_PASSWORD);
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT name, capacity, description, available FROM resources")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("name"),
                        rs.getInt("capacity"),
                        rs.getString("description"),
                        rs.getInt("available") == 1 ? "Yes" : "No"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading resources: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showResourceRequestDialog(DefaultTableModel model) {
        JTextField nameField = new JTextField();
        JTextField capField = new JTextField();
        JTextArea descField = new JTextArea(3, 20);

        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 7));
        panel.add(new JLabel("Resource Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Capacity:"));
        panel.add(capField);
        panel.add(new JLabel("Description:"));
        panel.add(new JScrollPane(descField));

        int res = JOptionPane.showConfirmDialog(this, panel, "Request New Resource", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String capStr = capField.getText().trim();
            String desc = descField.getText().trim();
            int capacity;
            try {
                capacity = Integer.parseInt(capStr);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid capacity.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Simulate sending request to admin (store in resources as unavailable)
            try (Connection c = DriverManager.getConnection(
                    Config.MYSQL_SERVER_URL + Config.DB_NAME,
                    Config.MYSQL_USER, Config.MYSQL_PASSWORD)) {
                PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO resources(name, capacity, description, available) VALUES (?, ?, ?, 0)");
                ps.setString(1, name);
                ps.setInt(2, capacity);
                ps.setString(3, desc);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Resource request sent to admin!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadResources(model); // Refresh table after adding
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error sending request: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}