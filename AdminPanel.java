package clubconnect.ui;

import clubconnect.service.UserService;
import clubconnect.models.User;
import clubconnect.models.Club;
import clubconnect.models.Membership;
import clubconnect.dao.ClubDAO;
import clubconnect.dao.UserDAO;
import clubconnect.dao.MembershipDAO;
import clubconnect.util.Config;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Extended Admin Dashboard for ClubConnect
 * - User Management (Approve, Delete, Promote)
 * - Assign Clubs to Leaders and Members (automatic and manual)
 * - Dashboard Statistics (live from DB)
 * - Event Table & Calendar
 * - Upload Minutes
 * - Sidebar navigation
 * - Auto-refresh & manual refresh
 */
public class AdminPanel extends JPanel {
    private final MainFrame parent;
    private JLabel lblMembersCount, lblEventsCount, lblFilesCount, lblUsersCount, lblClubsCount;
    private JTable eventTable, userTable;
    private DefaultTableModel eventModel, userModel;
    private Timer autoRefreshTimer;
    private UserService userService = new UserService();
    private ClubDAO clubDAO = new ClubDAO();
    private UserDAO userDAO = new UserDAO();
    private MembershipDAO membershipDAO = new MembershipDAO();

    public AdminPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // ===== Top Bar =====
        add(createTopBar(), BorderLayout.NORTH);

        // ===== Sidebar =====
        add(createSidebar(), BorderLayout.WEST);

        // ===== Main Content =====
        add(createMainContent(), BorderLayout.CENTER);

        // Initial load and auto-refresh
        refreshData();
        loadUsers();
        startAutoRefresh();
    }

    // ==================== Top Bar ====================
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(175, 0, 0));
        topBar.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("Hello, Admin 👋");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JButton refreshBtn = new JButton("⟳ Refresh");
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBackground(Color.WHITE);
        refreshBtn.setForeground(new Color(175, 0, 0));
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        refreshBtn.addActionListener(e -> {
            refreshData();
            loadUsers();
        });

        topBar.add(title, BorderLayout.WEST);
        topBar.add(refreshBtn, BorderLayout.EAST);
        return topBar;
    }

    // ==================== Sidebar ====================
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(200, 0, 0));
        sidebar.setBorder(new EmptyBorder(20, 10, 20, 10));

        sidebar.add(createSidebarButton("Dashboard", () -> parent.showCard("admin")));
        sidebar.add(createSidebarButton("Manage Clubs", () -> parent.showCard("club_mgmt")));
        sidebar.add(createSidebarButton("Manage Users", () -> parent.showCard("manage_users")));
        sidebar.add(createSidebarButton("Approve Budgets", () -> parent.showCard("budget_request")));
        sidebar.add(createSidebarButton("Reports", () -> parent.showCard("reports")));
        sidebar.add(createSidebarButton("Notifications", () -> parent.showCard("notifications")));
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(createSidebarButton("Logout", () -> parent.showCard("login")));

        return sidebar;
    }

    private JButton createSidebarButton(String text, Runnable onClick) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(180, 45));
        btn.setBackground(new Color(220, 0, 0));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (onClick != null) {
            btn.addActionListener(e -> onClick.run());
        }
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(255, 70, 70));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(220, 0, 0));
            }
        });
        return btn;
    }

    // ==================== Main Content ====================
    private JPanel createMainContent() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Stats cards
        JPanel statsPanel = new JPanel(new GridBagLayout());
        statsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 0.2;

        lblUsersCount = createStatCard("Total Users", "0", gbc, statsPanel, 0);
        lblClubsCount = createStatCard("Total Clubs", "0", gbc, statsPanel, 1);
        lblMembersCount = createStatCard("Club Members", "0", gbc, statsPanel, 2);
        lblEventsCount = createStatCard("Events Planned", "0", gbc, statsPanel, 3);
        lblFilesCount = createStatCard("Files Uploaded", "0", gbc, statsPanel, 4);

        JScrollPane scrollStats = new JScrollPane(statsPanel);
        scrollStats.setBorder(null);
        scrollStats.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollStats.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        // User management table
        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.setBorder(BorderFactory.createTitledBorder("User Management"));
        userModel = new DefaultTableModel(new Object[]{"ID", "Username", "Full Name", "Role", "Email", "Approved", "Club"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        userTable = new JTable(userModel);
        userTable.setRowHeight(24);
        JScrollPane userScroll = new JScrollPane(userTable);

        JPanel actions = new JPanel();
        JButton approveBtn = new JButton("Approve");
        JButton deleteBtn = new JButton("Delete");
        JButton promoteBtn = new JButton("Promote to Admin");
        JButton assignBtn = new JButton("Approve/Assign Club");

        approveBtn.addActionListener(this::handleApprove);
        deleteBtn.addActionListener(this::handleDelete);
        promoteBtn.addActionListener(this::handlePromote);
        assignBtn.addActionListener(e -> handleApproveOrAssignClub());

        actions.add(approveBtn);
        actions.add(deleteBtn);
        actions.add(promoteBtn);
        actions.add(assignBtn);

        userPanel.add(userScroll, BorderLayout.CENTER);
        userPanel.add(actions, BorderLayout.SOUTH);

        // Event table
        eventModel = new DefaultTableModel(new Object[]{"Event", "Date", "Location", "Description"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        eventTable = new JTable(eventModel);
        eventTable.setRowHeight(25);
        eventTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JScrollPane eventScroll = new JScrollPane(eventTable);
        eventScroll.setBorder(BorderFactory.createTitledBorder("Event Schedule"));

        // Calendar panel
        JPanel calendarPanel = new JPanel(new BorderLayout());
        calendarPanel.setBackground(new Color(248, 248, 248));
        calendarPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        JLabel calLabel = new JLabel("October " + LocalDate.now().getYear());
        calLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        calLabel.setBorder(new EmptyBorder(5, 5, 10, 5));
        calendarPanel.add(calLabel, BorderLayout.NORTH);
        JTextArea calendar = new JTextArea(generateCalendar());
        calendar.setEditable(false);
        calendar.setFont(new Font("Consolas", Font.PLAIN, 13));
        calendar.setBackground(new Color(248, 248, 248));
        calendarPanel.add(calendar, BorderLayout.CENTER);
        JButton uploadBtn = new JButton("Upload Minutes");
        uploadBtn.setBackground(new Color(175, 0, 0));
        uploadBtn.setForeground(Color.WHITE);
        uploadBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        uploadBtn.setFocusPainted(false);
        uploadBtn.setBorderPainted(false);
        uploadBtn.setPreferredSize(new Dimension(200, 35));
        uploadBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Upload clicked!"));
        calendarPanel.add(uploadBtn, BorderLayout.SOUTH);

        // Split lower main content (left: events+users, right: calendar)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.add(eventScroll);
        leftPanel.add(Box.createVerticalStrut(14));
        leftPanel.add(userPanel);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, calendarPanel);
        split.setDividerLocation(700);
        split.setResizeWeight(0.75);
        split.setContinuousLayout(true);
        split.setBorder(null);

        mainPanel.add(scrollStats, BorderLayout.NORTH);
        mainPanel.add(split, BorderLayout.CENTER);

        return mainPanel;
    }

    private JLabel createStatCard(String title, String value, GridBagConstraints gbc, JPanel parent, int gridx) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(250, 250, 250));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblValue.setForeground(new Color(175, 0, 0));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        gbc.gridx = gridx;
        gbc.gridy = 0;
        gbc.weightx = 1;
        parent.add(card, gbc);
        return lblValue;
    }

    // ==================== User Management ====================
    private void loadUsers() {
        userModel.setRowCount(0);
        List<User> users = userService.getAllUsers();
        for (User u : users) {
            // Find club for leader/member
            String clubName = "";
            if ("leader".equalsIgnoreCase(u.getRole())) {
                Club club = clubDAO.getClubForLeader(u.getId());
                clubName = (club != null) ? club.getName() : "";
            } else if ("member".equalsIgnoreCase(u.getRole())) {
                List<Club> clubs = clubDAO.getClubsForMember(u.getId());
                clubName = clubs.isEmpty() ? "" : clubs.get(0).getName();
            }
            userModel.addRow(new Object[]{
                    u.getId(), u.getUsername(), u.getFullName(), u.getRole(), u.getEmail(), u.isApproved(), clubName
            });
        }
    }

    /**
     * Approve user and optionally assign them to a club.
     *
     * Behavior:
     *  - Approves the user.
     *  - Shows a dialog listing all clubs by exact name.
     *  - If the user registered for a specific club (pending membership), that club is pre-selected and the "Assign" checkbox is checked by default.
     *  - If assign is chosen:
     *      - Leaders: update clubs.created_by
     *      - Members: approve existing pending membership for that club OR create an approved membership
     */
    private void handleApprove(ActionEvent e) {
        int row = userTable.getSelectedRow();
        if (row == -1) return;

        int userId = (int) userModel.getValueAt(row, 0);
        String role = (String) userModel.getValueAt(row, 3);

        // 1) Approve the user
        boolean approved = userService.approveUser(userId);
        if (!approved) {
            JOptionPane.showMessageDialog(this, "Failed to approve user.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2) Find pending membership (if any) that the user created during registration
        Membership pendingMembership = null;
        try {
            List<Membership> userMemberships = membershipDAO.getMembershipsForUser(userId);
            for (Membership m : userMemberships) {
                if ("pending".equalsIgnoreCase(m.getStatus())) {
                    pendingMembership = m;
                    break;
                }
            }
        } catch (Exception ex) {
            // ignore, membershipDAO may return empty list; continue without pending
            pendingMembership = null;
        }

        // 3) Prepare club chooser with exact names
        List<Club> clubs = clubDAO.listAll();
        if (clubs == null || clubs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "User approved, but no clubs are defined in the system to assign.", "Approved", JOptionPane.INFORMATION_MESSAGE);
            loadUsers();
            return;
        }
        Club[] clubArr = clubs.toArray(new Club[0]);

        JComboBox<Club> clubBox = new JComboBox<>(clubArr);
        clubBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Club) value = ((Club) value).getName();
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        // Pre-select pending club if present
        if (pendingMembership != null) {
            for (int i = 0; i < clubArr.length; i++) {
                if (clubArr[i].getId() == pendingMembership.getClubId()) {
                    clubBox.setSelectedIndex(i);
                    break;
                }
            }
        }

        JCheckBox assignChk = new JCheckBox("Assign to selected club", pendingMembership != null);

        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.add(new JLabel("User approved. Do you want to assign a club?"), BorderLayout.NORTH);
        JPanel center = new JPanel(new GridLayout(0, 1, 6, 6));
        center.add(assignChk);
        center.add(new JLabel("Select club (exact names shown):"));
        center.add(clubBox);
        panel.add(center, BorderLayout.CENTER);

        int res = JOptionPane.showConfirmDialog(this, panel, "Assign Club", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (res == JOptionPane.OK_OPTION && assignChk.isSelected()) {
            Club selectedClub = (Club) clubBox.getSelectedItem();
            if (selectedClub != null) {
                if ("leader".equalsIgnoreCase(role)) {
                    // assign as leader: update clubs.created_by
                    try (Connection c = DriverManager.getConnection(Config.MYSQL_SERVER_URL + Config.DB_NAME, Config.MYSQL_USER, Config.MYSQL_PASSWORD)) {
                        PreparedStatement ps = c.prepareStatement("UPDATE clubs SET created_by=? WHERE id=?");
                        ps.setInt(1, userId);
                        ps.setInt(2, selectedClub.getId());
                        int updated = ps.executeUpdate();
                        JOptionPane.showMessageDialog(this, updated > 0 ? "Leader assigned to club!" : "Failed to assign leader.");
                        refreshData();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else { // member
                    boolean done = false;
                    if (pendingMembership != null && pendingMembership.getClubId() == selectedClub.getId()) {
                        // approve the pending membership
                        done = membershipDAO.approveMembership(pendingMembership.getId());
                    } else {
                        // create an approved membership
                        done = membershipDAO.createMembership(userId, selectedClub.getId(), "member", "approved");
                    }
                    JOptionPane.showMessageDialog(this, done ? "Member assigned to club!" : "Failed to assign member.");
                }
            }
        }

        // refresh UI
        loadUsers();
    }

    private void handleDelete(ActionEvent e) {
        int row = userTable.getSelectedRow();
        if (row == -1) return;
        int userId = (int) userModel.getValueAt(row, 0);
        userService.deleteUser(userId);
        loadUsers();
    }

    private void handlePromote(ActionEvent e) {
        int row = userTable.getSelectedRow();
        if (row == -1) return;
        int userId = (int) userModel.getValueAt(row, 0);
        userService.updateUserRole(userId, "admin");
        loadUsers();
    }

    /**
     * Approve or assign club based on user's selected or registered club
     * If leader: assign as created_by in clubs
     * If member: assign membership in memberships
     * If already assigned, allows to re-assign
     */
    private void handleApproveOrAssignClub() {
        int row = userTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a user first.");
            return;
        }
        int userId = (int) userModel.getValueAt(row, 0);
        String role = (String) userModel.getValueAt(row, 3);

        // Approve user if not approved
        boolean isApproved = Boolean.TRUE.equals(userModel.getValueAt(row, 5));
        if (!isApproved) {
            userService.approveUser(userId);
        }

        // Get club choices
        List<Club> clubs = clubDAO.listAll();
        Club[] clubArr = clubs.toArray(new Club[0]);
        Club selectedClub = (Club) JOptionPane.showInputDialog(
                this, "Select club to assign:", "Assign Club",
                JOptionPane.QUESTION_MESSAGE, null, clubArr, clubArr.length > 0 ? clubArr[0] : null);
        if (selectedClub == null) return;

        if ("leader".equalsIgnoreCase(role)) {
            // Assign as leader of selected club (created_by)
            try (Connection c = DriverManager.getConnection(Config.MYSQL_SERVER_URL + Config.DB_NAME, Config.MYSQL_USER, Config.MYSQL_PASSWORD)) {
                PreparedStatement ps = c.prepareStatement("UPDATE clubs SET created_by=? WHERE id=?");
                ps.setInt(1, userId);
                ps.setInt(2, selectedClub.getId());
                int updated = ps.executeUpdate();
                JOptionPane.showMessageDialog(this, updated > 0 ? "Leader assigned to club!" : "Failed to assign leader.");
                refreshData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
            }
        } else if ("member".equalsIgnoreCase(role)) {
            // Assign as member of club (membership)
            boolean ok = membershipDAO.createMembership(userId, selectedClub.getId(), "member", "approved");
            JOptionPane.showMessageDialog(this, ok ? "Member assigned to club!" : "Failed to assign member.");
            refreshData();
        } else {
            JOptionPane.showMessageDialog(this, "Select a user with 'leader' or 'member' role to assign a club.");
        }
        loadUsers();
    }

    // ==================== Database ====================
    private void refreshData() {
        try (Connection conn = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER,
                Config.MYSQL_PASSWORD)) {
            Statement st = conn.createStatement();
            lblUsersCount.setText(getCount(st, "users"));
            lblClubsCount.setText(getCount(st, "clubs"));
            lblMembersCount.setText(getCount(st, "memberships"));
            lblEventsCount.setText(getCount(st, "events"));
            lblFilesCount.setText(getCount(st, "resources"));
            // Events table
            ResultSet rs = st.executeQuery("SELECT title, date_time, venue, details FROM events ORDER BY date_time ASC");
            eventModel.setRowCount(0);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            while (rs.next()) {
                String date = rs.getTimestamp("date_time").toLocalDateTime().format(fmt);
                eventModel.addRow(new Object[]{
                        rs.getString("title"),
                        date,
                        rs.getString("venue"),
                        rs.getString("details")
                });
            }
            st.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getCount(Statement st, String table) throws SQLException {
        ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + table);
        rs.next();
        return String.valueOf(rs.getInt(1));
    }

    // ==================== Auto Refresh ====================
    private void startAutoRefresh() {
        autoRefreshTimer = new Timer(true);
        autoRefreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    refreshData();
                    loadUsers();
                });
            }
        }, 0, 30000); // every 30 seconds
    }

    public void stopAutoRefresh() {
        if (autoRefreshTimer != null) {
            autoRefreshTimer.cancel();
        }
    }

    // ==================== Calendar ====================
    private String generateCalendar() {
        StringBuilder sb = new StringBuilder();
        sb.append("Su Mo Tu We Th Fr Sa\n");
        LocalDate first = LocalDate.now().withDayOfMonth(1);
        int indent = first.getDayOfWeek().getValue() % 7;
        for (int i = 0; i < indent; i++) sb.append("   ");
        int days = first.lengthOfMonth();
        for (int i = 1; i <= days; i++) {
            sb.append(String.format("%2d ", i));
            if ((i + indent) % 7 == 0) sb.append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }
}