package clubconnect.ui;

import clubconnect.dao.ClubDAO;
import clubconnect.models.Event;
import clubconnect.util.Config;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class EventManagementPanel extends JPanel {
    private MainFrame parent;
    private final Color accent = new Color(175, 0, 0);

    public EventManagementPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Create New Event", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 23));
        title.setForeground(accent);
        title.setBorder(new EmptyBorder(20, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(30, 60, 30, 60));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Title:");
        JTextField ttitle = new JTextField(28);

        JLabel lblDate = new JLabel("Date (YYYYDDMM):");
        JTextField tdate = new JTextField(28);

        JLabel lblTime = new JLabel("Time (HH:MM):");
        JTextField ttime = new JTextField(28);

        JLabel lblVenue = new JLabel("Venue:");
        JTextField tvenue = new JTextField(28);

        JLabel lblCap = new JLabel("Capacity:");
        JTextField tcap = new JTextField("50", 28);

        JLabel lblDetails = new JLabel("Details:");
        JTextArea tdetails = new JTextArea(3, 28);

        JCheckBox budgetReq = new JCheckBox("Request budget");
        JLabel lblBudgetStatus = new JLabel("Budget Status:");
        JTextField tBudgetStatus = new JTextField("pending", 28);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(lblTitle, gbc);
        gbc.gridx = 1; gbc.gridy = 0; formPanel.add(ttitle, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(lblDate, gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(tdate, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(lblTime, gbc);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(ttime, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(lblVenue, gbc);
        gbc.gridx = 1; gbc.gridy = 3; formPanel.add(tvenue, gbc);

        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(lblCap, gbc);
        gbc.gridx = 1; gbc.gridy = 4; formPanel.add(tcap, gbc);

        gbc.gridx = 0; gbc.gridy = 5; formPanel.add(lblDetails, gbc);
        gbc.gridx = 1; gbc.gridy = 5; formPanel.add(new JScrollPane(tdetails), gbc);

        gbc.gridx = 0; gbc.gridy = 6; formPanel.add(budgetReq, gbc);
        gbc.gridx = 1; gbc.gridy = 6; formPanel.add(new JLabel(""), gbc);

        gbc.gridx = 0; gbc.gridy = 7; formPanel.add(lblBudgetStatus, gbc);
        gbc.gridx = 1; gbc.gridy = 7; formPanel.add(tBudgetStatus, gbc);

        JButton create = new JButton("\uD83D\uDCC5 Create");
        create.setBackground(accent);
        create.setForeground(Color.WHITE);
        create.setFont(new Font("Segoe UI", Font.BOLD, 15));
        create.setFocusPainted(false);
        create.addActionListener(e -> {
            try {
                String titleVal = ttitle.getText().trim();
                String dateVal = tdate.getText().trim();
                String timeVal = ttime.getText().trim();
                String venueVal = tvenue.getText().trim();
                int capacityVal = Integer.parseInt(tcap.getText().trim());
                String detailsVal = tdetails.getText().trim();
                boolean wantsBudget = budgetReq.isSelected();
                String budgetStatus = tBudgetStatus.getText().trim();

                // Convert date from yyyyddmm and time to yyyy-MM-dd HH:mm
                if (!dateVal.matches("\\d{8}")) throw new Exception("Date must be in YYYYDDMM format.");
                if (!timeVal.matches("\\d{2}:\\d{2}")) throw new Exception("Time must be in HH:MM format.");

                String yyyy = dateVal.substring(0, 4);
                String dd = dateVal.substring(4, 6);
                String mm = dateVal.substring(6, 8);
                String dateTimeStr = yyyy + "-" + mm + "-" + dd + " " + timeVal;

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                java.util.Date parsed = df.parse(dateTimeStr);

                // Get club info
                int clubId = parent.getCurrentUser() != null
                        ? new ClubDAO().getClubForLeader(parent.getCurrentUser().getId()).getId()
                        : -1;
                if (clubId == -1) throw new Exception("No club assigned!");

                int eventId;
                try (Connection c = DriverManager.getConnection(Config.MYSQL_SERVER_URL + Config.DB_NAME, Config.MYSQL_USER, Config.MYSQL_PASSWORD)) {
                    // 1. Insert event
                    PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO events(club_id, title, date_time, venue, capacity, details, budget_requested, budget_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setInt(1, clubId);
                    ps.setString(2, titleVal);
                    ps.setTimestamp(3, new java.sql.Timestamp(parsed.getTime()));
                    ps.setString(4, venueVal);
                    ps.setInt(5, capacityVal);
                    ps.setString(6, detailsVal);
                    ps.setBoolean(7, wantsBudget);
                    ps.setString(8, budgetStatus);
                    ps.executeUpdate();
                    ResultSet rs = ps.getGeneratedKeys();
                    if (!rs.next()) throw new Exception("Could not get new event ID!");
                    eventId = rs.getInt(1);
                }

                // 2. If requesting budget, insert into budgets table
                if (wantsBudget) {
                    JTextField amountField = new JTextField();
                    JTextArea notesField = new JTextArea(3, 20);
                    JPanel budgetPanel = new JPanel(new GridLayout(0, 1, 0, 7));
                    budgetPanel.add(new JLabel("Amount Requested:"));
                    budgetPanel.add(amountField);
                    budgetPanel.add(new JLabel("Notes:"));
                    budgetPanel.add(new JScrollPane(notesField));
                    int res = JOptionPane.showConfirmDialog(this, budgetPanel, "Budget Request Details", JOptionPane.OK_CANCEL_OPTION);
                    if (res == JOptionPane.OK_OPTION) {
                        double amount = Double.parseDouble(amountField.getText().trim());
                        String notes = notesField.getText().trim();
                        int requestedBy = parent.getCurrentUser().getId();
                        try (Connection c = DriverManager.getConnection(Config.MYSQL_SERVER_URL + Config.DB_NAME, Config.MYSQL_USER, Config.MYSQL_PASSWORD)) {
                            PreparedStatement ps = c.prepareStatement(
                                "INSERT INTO budgets(event_id, requested_by, amount, status, notes) VALUES (?, ?, ?, ?, ?)"
                            );
                            ps.setInt(1, eventId);
                            ps.setInt(2, requestedBy);
                            ps.setDouble(3, amount);
                            ps.setString(4, "pending");
                            ps.setString(5, notes);
                            ps.executeUpdate();
                        }
                    }
                }

                JOptionPane.showMessageDialog(this, "Event created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Move cursor to new line after creation
                ttitle.setText(""); tdate.setText(""); ttime.setText(""); tvenue.setText(""); tcap.setText("50"); tdetails.setText(""); budgetReq.setSelected(false); tBudgetStatus.setText("pending");
                ttitle.requestFocus();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error creating event: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton back = new JButton("Back");
        back.setBackground(Color.WHITE);
        back.setForeground(accent);
        back.setFont(new Font("Segoe UI", Font.BOLD, 15));
        back.setFocusPainted(false);
        back.addActionListener(e -> parent.showCard("leader"));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 12));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(create);
        btnPanel.add(back);

        add(formPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }
}