package clubconnect.ui;

import clubconnect.dao.ClubDAO;
import clubconnect.dao.UserDAO;
import clubconnect.dao.MembershipDAO;
import clubconnect.models.Club;
import clubconnect.models.User;
import clubconnect.models.Event;
import clubconnect.models.Membership;
import clubconnect.util.Config;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.List;

/**
 * Leader dashboard with clickable sidebar.
 * "Resources" tab opens ResourceBookingPanel as a separate view.
 * Fixes IndexOutOfBoundsException for tab selection.
 */
public class LeaderPanel extends JPanel {
    private final MainFrame parent;
    private final ClubDAO clubDAO = new ClubDAO();
    private final UserDAO userDAO = new UserDAO();
    private final MembershipDAO membershipDAO = new MembershipDAO();
    private Club myClub;
    private JTabbedPane tabbedPane;
    private JPanel sidebar;

    // Main theme colors
    private final Color sidebarBg = new Color(185, 36, 36);
    private final Color sidebarSelected = new Color(220, 0, 0);
    private final Color sidebarText = Color.WHITE;
    private final Color mainBg = new Color(245, 245, 245);
    private final Color cardBg = Color.WHITE;
    private final Color accent = new Color(175, 0, 0);

    public LeaderPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(mainBg);

        User leader = parent.getCurrentUser();
        myClub = clubDAO.getClubForLeader(leader.getId());

        if (myClub == null) {
            add(noClubPanel(leader), BorderLayout.CENTER);
            return;
        }

        // Sidebar
        sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Top bar
        add(createTopBar(leader), BorderLayout.NORTH);

        // Tabs
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBackground(mainBg);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tabbedPane.addTab("Dashboard", dashboardTab());
        tabbedPane.addTab("My Club", myClubTab());
        tabbedPane.addTab("Members", membersTab());
        tabbedPane.addTab("Events", eventsTab());
        tabbedPane.addTab("Discussion", discussionTab());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel noClubPanel(User leader) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(mainBg);
        JLabel msg = new JLabel("<html><div style='text-align:center;padding:30px;'>"
                + "<h2 style='color:#bb2222;'>No club assigned!</h2>"
                + "<p style='font-size:14pt;'>"
                + "Dear " + leader.getFullName() + ",<br>No club is currently assigned to your account.<br>"
                + "Please contact the system administrator or wait for your club to be assigned."
                + "</p></div></html>", SwingConstants.CENTER);
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        panel.add(msg, BorderLayout.CENTER);
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(sidebarBg);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> parent.showCard("login"));
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(logoutBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    // Sidebar with icons and clickable list (syncs with tabs)
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(sidebarBg);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Only 5 tabs in tabbedPane! Handle Resources and Logout separately.
        String[] labels = {"Dashboard", "My Club", "Members", "Events", "Discussion", "Resources", "Logout"};
        String[] icons = {"\uD83D\uDCC8", "\uD83D\uDC65", "\uD83D\uDCDA", "\uD83D\uDCC5", "\uD83D\uDCAC", "\uD83D\uDCC2", "\u2B1B"};
        int tabCount = 5; // Only 5 tabs

        for (int i = 0; i < labels.length; i++) {
            JButton btn = sidebarButton(labels[i], icons[i], i == 0);
            int tabIdx = i;
            btn.addActionListener(e -> {
                if (labels[tabIdx].equals("Logout")) {
                    parent.showCard("login");
                } else if (labels[tabIdx].equals("Resources")) {
                    parent.showCard("resource_booking");
                } else {
                    // Only set tab index for tabs that exist!
                    if (tabIdx < tabCount) {
                        tabbedPane.setSelectedIndex(tabIdx);
                        highlightSidebarBtn(sidebar, btn);
                    }
                }
            });
            sidebar.add(btn);
            if (i < labels.length - 1) sidebar.add(Box.createVerticalStrut(1));
        }
        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private JButton sidebarButton(String label, String icon, boolean selected) {
        JButton btn = new JButton("  " + icon + "  " + label);
        btn.setMaximumSize(new Dimension(192, 48));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setBackground(selected ? sidebarSelected : sidebarBg);
        btn.setForeground(sidebarText);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 17));
        btn.setBorder(BorderFactory.createEmptyBorder(9, 32, 9, 8));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(sidebarSelected); }
            public void mouseExited(java.awt.event.MouseEvent evt) { if (!btn.getClientProperty("selected").equals(Boolean.TRUE)) btn.setBackground(sidebarBg); }
        });
        btn.putClientProperty("selected", selected);
        return btn;
    }

    private void highlightSidebarBtn(JPanel sidebar, JButton selectedBtn) {
        for (Component comp : sidebar.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                boolean isSel = (btn == selectedBtn);
                btn.setBackground(isSel ? sidebarSelected : sidebarBg);
                btn.putClientProperty("selected", isSel);
            }
        }
    }

    private JPanel createTopBar(User leader) {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(accent);
        topBar.setBorder(new EmptyBorder(0, 0, 0, 0));
        topBar.setPreferredSize(new Dimension(0, 60));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 10));
        left.setBackground(accent);
        JLabel logo = new JLabel("\u2261");
        logo.setFont(new Font("Segoe UI Symbol", Font.BOLD, 36));
        logo.setForeground(Color.WHITE);
        JLabel clubConnect = new JLabel("<html><span style='font-size:22px;font-weight:bold;'>ClubConnect</span></html>");
        clubConnect.setForeground(Color.WHITE);
        left.add(logo);
        left.add(clubConnect);
        left.add(Box.createHorizontalStrut(20));
        JTextField search = new JTextField();
        search.setPreferredSize(new Dimension(260, 30));
        search.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        search.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        left.add(search);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        right.setOpaque(false);
        JLabel leaderIcon = new JLabel("\uD83D\uDC64");
        leaderIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        JLabel leaderName = new JLabel(leader.getFullName() != null ? leader.getFullName() : "Leader");
        leaderName.setForeground(Color.WHITE);
        leaderName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        right.add(leaderIcon);
        right.add(leaderName);

        topBar.add(left, BorderLayout.WEST);
        topBar.add(right, BorderLayout.EAST);

        return topBar;
    }

    private JPanel dashboardTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(mainBg);
        panel.setBorder(new EmptyBorder(20, 20, 18, 20));
        JLabel greet = new JLabel("Hello Club Leader");
        greet.setFont(new Font("Segoe UI", Font.BOLD, 29));
        greet.setForeground(new Color(120, 0, 0));
        greet.setBorder(new EmptyBorder(0, 0, 18, 0));
        panel.add(greet, BorderLayout.NORTH);

        JPanel stats = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 8));
        stats.setOpaque(false);
        int memberCount = userDAO.getClubMembers(myClub.getId()).size();
        int eventCount = clubDAO.getEventsForClub(myClub.getId()).size();
        int fileCount = safeGetFilesCount(myClub.getId());
        stats.add(infoCard("\uD83D\uDC65", "Members", memberCount));
        stats.add(infoCard("\uD83D\uDCC5", "Events", eventCount));
        stats.add(infoCard("\uD83D\uDCC2", "Files", fileCount));
        panel.add(stats, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 8));
        actions.setOpaque(false);
        actions.add(actionBtn("\uD83D\uDCC5 Create Event", accent, Color.WHITE, () -> parent.showCard("event_mgmt")));
        actions.add(actionBtn("\uD83D\uDCCA View Attendance", accent, Color.WHITE, () -> parent.showCard("attendance")));
        actions.add(actionBtn("\uD83D\uDCAC Discussion", Color.WHITE, accent, () -> tabbedPane.setSelectedIndex(4)));
        panel.add(actions, BorderLayout.SOUTH);

        JPanel lower = new JPanel(new GridBagLayout());
        lower.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 18);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.62;
        gbc.gridx = 0;

        DefaultTableModel eventModel = new DefaultTableModel(new Object[]{"Event", "Date", "Location", "Description"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable eventTable = new JTable(eventModel);
        eventTable.setRowHeight(27);
        eventTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        List<Event> events = clubDAO.getEventsForClub(myClub.getId());
        for (Event ev : events) {
            eventModel.addRow(new Object[]{ev.getTitle(), ev.getDateTime(), ev.getVenue(), ev.getDetails()});
        }
        JScrollPane eventScroll = new JScrollPane(eventTable);
        eventScroll.setBorder(BorderFactory.createTitledBorder("Event Schedule"));
        gbc.gridy = 0; gbc.weighty = 1.0;
        lower.add(eventScroll, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.38;
        lower.add(makeCalendarPanel(), gbc);

        panel.add(lower, BorderLayout.PAGE_END);

        return panel;
    }

    private JPanel myClubTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(cardBg);
        panel.setBorder(new EmptyBorder(20, 28, 20, 28));
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(cardBg);

        JLabel clubHeader = new JLabel("<html><span style='font-size:18pt;font-weight:bold;'>"
                + myClub.getName() + "</span> <span style='color:#888;font-size:11pt;'>(" + myClub.getCategory() + ")</span></html>");
        clubHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
        JLabel clubDesc = new JLabel("<html><div style='max-width:650px;'>" +
                (myClub.getDescription() != null ? myClub.getDescription() : "") + "</div></html>");
        clubDesc.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        clubDesc.setForeground(new Color(60, 60, 60));
        infoPanel.add(clubHeader);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(clubDesc);

        // Edit, archive, delete
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        actions.setOpaque(false);
        JButton editBtn = actionBtn("Edit Club", accent, Color.WHITE, this::editClubDialog);
        JButton archiveBtn = actionBtn(myClub.isArchived() ? "Unarchive" : "Archive", accent, Color.WHITE, this::toggleArchiveClub);
        JButton deleteBtn = actionBtn("Delete Club", Color.WHITE, accent, this::deleteClubDialog);
        actions.add(editBtn);
        actions.add(archiveBtn);
        actions.add(deleteBtn);

        infoPanel.add(actions);
        panel.add(infoPanel, BorderLayout.NORTH);

        DefaultTableModel membersModel = new DefaultTableModel(new Object[]{"ID", "Full Name", "Role", "Status"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable membersTable = new JTable(membersModel);
        membersTable.setRowHeight(26);
        membersTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane memberScroll = new JScrollPane(membersTable);
        memberScroll.setBorder(BorderFactory.createTitledBorder("Club Members"));
        panel.add(memberScroll, BorderLayout.CENTER);

        List<Membership> memberships = membershipDAO.getMembershipsForClub(myClub.getId());
        for (Membership m : memberships) {
            User u = userDAO.findById(m.getUserId());
            membersModel.addRow(new Object[]{
                u != null ? u.getId() : m.getUserId(),
                u != null ? u.getFullName() : "(Unknown)",
                m.getRole(),
                m.getStatus()
            });
        }

        return panel;
    }

    private void editClubDialog() {
        JTextField nameField = new JTextField(myClub.getName());
        JTextField catField = new JTextField(myClub.getCategory());
        JTextArea descField = new JTextArea(myClub.getDescription(), 4, 28);
        JCheckBox archivedBox = new JCheckBox("Archived", myClub.isArchived());
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 6));
        panel.add(new JLabel("Club Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Category:"));
        panel.add(catField);
        panel.add(new JLabel("Description:"));
        panel.add(new JScrollPane(descField));
        panel.add(archivedBox);

        int res = JOptionPane.showConfirmDialog(this, panel, "Edit Club", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            myClub.setName(nameField.getText().trim());
            myClub.setCategory(catField.getText().trim());
            myClub.setDescription(descField.getText().trim());
            myClub.setArchived(archivedBox.isSelected());
            boolean ok = clubDAO.update(myClub);
            JOptionPane.showMessageDialog(this, ok ? "Club updated!" : "Failed to update club.");
        }
    }

    private void toggleArchiveClub() {
        myClub.setArchived(!myClub.isArchived());
        boolean ok = clubDAO.update(myClub);
        JOptionPane.showMessageDialog(this, ok ? (myClub.isArchived() ? "Club archived." : "Club unarchived.") : "Failed!");
    }

    private void deleteClubDialog() {
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this club?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = clubDAO.delete(myClub.getId());
            JOptionPane.showMessageDialog(this, ok ? "Club deleted." : "Failed to delete club.");
            parent.showCard("dashboard");
        }
    }

    private JPanel membersTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(cardBg);
        panel.setBorder(new EmptyBorder(20, 28, 20, 28));
        JLabel title = new JLabel("Club Members");
        title.setFont(new Font("Segoe UI", Font.BOLD, 21));
        panel.add(title, BorderLayout.NORTH);

        DefaultTableModel membersModel = new DefaultTableModel(new Object[]{"ID", "Full Name", "Role", "Status"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable membersTable = new JTable(membersModel);
        membersTable.setRowHeight(26);
        membersTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane memberScroll = new JScrollPane(membersTable);
        memberScroll.setBorder(BorderFactory.createTitledBorder("All Members"));
        panel.add(memberScroll, BorderLayout.CENTER);
        List<Membership> memberships = membershipDAO.getMembershipsForClub(myClub.getId());
        for (Membership m : memberships) {
            User u = userDAO.findById(m.getUserId());
            membersModel.addRow(new Object[]{
                u != null ? u.getId() : m.getUserId(),
                u != null ? u.getFullName() : "(Unknown)",
                m.getRole(),
                m.getStatus()
            });
        }
        return panel;
    }

    private JPanel eventsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(cardBg);
        panel.setBorder(new EmptyBorder(18, 24, 18, 24));
        JLabel title = new JLabel("Club Events");
        title.setFont(new Font("Segoe UI", Font.BOLD, 21));
        panel.add(title, BorderLayout.NORTH);

        DefaultTableModel eventModel = new DefaultTableModel(new Object[]{"Event", "Date", "Location", "Description"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable eventTable = new JTable(eventModel);
        eventTable.setRowHeight(27);
        eventTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane eventScroll = new JScrollPane(eventTable);
        eventScroll.setBorder(BorderFactory.createTitledBorder("Event Schedule"));
        panel.add(eventScroll, BorderLayout.CENTER);
        List<Event> events = clubDAO.getEventsForClub(myClub.getId());
        for (Event ev : events) {
            eventModel.addRow(new Object[]{
                ev.getTitle(),
                ev.getDateTime(),
                ev.getVenue(),
                ev.getDetails()
            });
        }
        JButton createBtn = actionBtn("\uD83D\uDCC5 Create Event", accent, Color.WHITE, () -> parent.showCard("event_mgmt"));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);
        btnPanel.add(createBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel discussionTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(cardBg);
        panel.setBorder(new EmptyBorder(18, 20, 18, 20));
        JLabel title = new JLabel("Club Discussion Board");
        title.setFont(new Font("Segoe UI", Font.BOLD, 21));
        panel.add(title, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new Object[]{"User", "Message", "Date"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(26);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Messages"));
        panel.add(scroll, BorderLayout.CENTER);

        loadClubDiscussions(model);

        JPanel inputPanel = new JPanel(new BorderLayout(8, 0));
        inputPanel.setOpaque(false);
        JTextField msgField = new JTextField();
        msgField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JButton sendBtn = actionBtn("Send", accent, Color.WHITE, () -> {
            String msg = msgField.getText().trim();
            if (!msg.isEmpty()) {
                saveDiscussionMessage(myClub.getId(), parent.getCurrentUser().getId(), msg);
                msgField.setText("");
                loadClubDiscussions(model);
            }
        });
        inputPanel.add(msgField, BorderLayout.CENTER);
        inputPanel.add(sendBtn, BorderLayout.EAST);
        panel.add(inputPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel infoCard(String icon, String title, int value) {
        JPanel card = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        card.setPreferredSize(new Dimension(180, 55));
        card.setBackground(cardBg);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(7, 18, 7, 18)
        ));
        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setBorder(new EmptyBorder(0, 10, 0, 0));
        JLabel lblValue = new JLabel(String.valueOf(value));
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblValue.setForeground(accent);
        card.add(lblIcon);
        card.add(lblTitle);
        card.add(lblValue);
        return card;
    }

    private JButton actionBtn(String text, Color bg, Color fg, Runnable onClick) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(180, 38));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        btn.addActionListener(e -> onClick.run());
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setBackground(bg.darker()); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setBackground(bg); }
        });
        return btn;
    }

    private JPanel makeCalendarPanel() {
        JPanel calPanel = new JPanel(new BorderLayout());
        calPanel.setBackground(cardBg);
        calPanel.setBorder(new EmptyBorder(12, 12, 8, 12));
        JLabel calTitle = new JLabel("October 2025");
        calTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        calTitle.setBorder(new EmptyBorder(0, 0, 8, 0));
        calPanel.add(calTitle, BorderLayout.NORTH);
        JTextArea calendar = new JTextArea(generateCalendar());
        calendar.setEditable(false);
        calendar.setFont(new Font("Consolas", Font.PLAIN, 13));
        calendar.setBackground(cardBg);
        calendar.setBorder(null);
        calPanel.add(calendar, BorderLayout.CENTER);
        JButton uploadBtn = new JButton("\u2B06 Upload Minutes");
        uploadBtn.setBackground(accent);
        uploadBtn.setForeground(Color.WHITE);
        uploadBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        uploadBtn.setFocusPainted(false);
        uploadBtn.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        uploadBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        uploadBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Upload Minutes clicked!"));
        calPanel.add(uploadBtn, BorderLayout.SOUTH);
        return calPanel;
    }

    private String generateCalendar() {
        StringBuilder sb = new StringBuilder();
        sb.append("Su Mo Tu We Th Fr Sa\n");
        int year = 2025, month = 10;
        java.time.LocalDate first = java.time.LocalDate.of(year, month, 1);
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

    private int safeGetFilesCount(int clubId) {
        try {
            List<String> files = clubDAO.getFilesForClub(clubId);
            return files == null ? 0 : files.size();
        } catch (Exception e) {
            return 0;
        }
    }

    private void saveDiscussionMessage(int clubId, int userId, String content) {
        try (Connection c = DriverManager.getConnection(Config.MYSQL_SERVER_URL + Config.DB_NAME, Config.MYSQL_USER, Config.MYSQL_PASSWORD)) {
            PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO discussions(club_id, user_id, content, created_at) VALUES (?, ?, ?, NOW())");
            ps.setInt(1, clubId);
            ps.setInt(2, userId);
            ps.setString(3, content);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadClubDiscussions(DefaultTableModel model) {
        model.setRowCount(0);
        try (Connection c = DriverManager.getConnection(Config.MYSQL_SERVER_URL + Config.DB_NAME, Config.MYSQL_USER, Config.MYSQL_PASSWORD)) {
            PreparedStatement ps = c.prepareStatement(
                    "SELECT d.content, d.created_at, u.full_name " +
                            "FROM discussions d LEFT JOIN users u ON d.user_id=u.id WHERE d.club_id=? ORDER BY d.created_at DESC");
            ps.setInt(1, myClub.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("full_name") != null ? rs.getString("full_name") : "(Unknown)",
                        rs.getString("content"),
                        rs.getTimestamp("created_at")
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}