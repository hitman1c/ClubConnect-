package clubconnect.ui;

import clubconnect.dao.*;
import clubconnect.models.*;
import clubconnect.util.Config;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class MemberPanel extends JPanel {
    private final MainFrame parent;
    private final ClubDAO clubDAO = new ClubDAO();
    private final MembershipDAO membershipDAO = new MembershipDAO();
    private final EventDAO eventDAO = new EventDAO();
    private final UserDAO userDAO = new UserDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final CommentDAO commentDAO = new CommentDAO();

    private JPanel contentPanel;

    private final Color mainRed = new Color(175, 0, 0);
    private final Color accentRed = new Color(220, 0, 0);

    private List<Club> joinedClubs = new ArrayList<>();

    public MemberPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(createTopBar(), BorderLayout.NORTH);
        add(createSidebar(), BorderLayout.WEST);

        contentPanel = new JPanel(new CardLayout());
        add(contentPanel, BorderLayout.CENTER);

        reloadUserClubs();
        showDashboard();
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(mainRed);
        topBar.setBorder(new EmptyBorder(10, 20, 10, 20));

        User cur = parent.getCurrentUser();
        String name = (cur != null && cur.getFullName() != null) ? cur.getFullName() : "Member";
        JLabel title = new JLabel("Hello, " + name + " 👋");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JButton refreshBtn = new JButton("⟳ Refresh");
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBackground(Color.WHITE);
        refreshBtn.setForeground(mainRed);
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        refreshBtn.addActionListener(e -> {
            reloadUserClubs();
            showDashboard();
        });

        topBar.add(title, BorderLayout.WEST);
        topBar.add(refreshBtn, BorderLayout.EAST);
        return topBar;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(accentRed);
        sidebar.setBorder(new EmptyBorder(20, 10, 20, 10));

        sidebar.add(sidebarButton("Dashboard", this::showDashboard));
        sidebar.add(sidebarButton("Clubs Directory", this::showAllClubs)); // new feature
        sidebar.add(sidebarButton("My Clubs", this::showMyClubs));
        sidebar.add(sidebarButton("Events", this::showAllEvents));
        sidebar.add(sidebarButton("Discussions", this::showDiscussions));
        sidebar.add(sidebarButton("Notifications", this::showNotifications));
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(sidebarButton("Logout", () -> {
            parent.setCurrentUser(null);
            parent.showCard("login");
        }));
        return sidebar;
    }

    private JButton sidebarButton(String text, Runnable onClick) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(180, 42));
        btn.setBackground(accentRed);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> onClick.run());
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { btn.setBackground(mainRed); }
            public void mouseExited(MouseEvent evt) { btn.setBackground(accentRed); }
        });
        return btn;
    }

    private void switchCard(String name, JPanel card) {
        if (Arrays.stream(contentPanel.getComponents()).noneMatch(c -> c == card))
            contentPanel.add(card, name);
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, name);
    }

    private void reloadUserClubs() {
        joinedClubs.clear();
        User cur = parent.getCurrentUser();
        if (cur == null) return;
        List<Membership> mems = membershipDAO.getMembershipsForUser(cur.getId());
        List<Club> allClubs = clubDAO.listAll();
        for (Membership m : mems) {
            if ("approved".equalsIgnoreCase(m.getStatus())) {
                for (Club c : allClubs)
                    if (c.getId() == m.getClubId()) joinedClubs.add(c);
            }
        }
    }

    // ---- DASHBOARD ----
    private void showDashboard() {
        reloadUserClubs();
        JPanel panel = new JPanel(new BorderLayout(15, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(16, 25, 16, 25));

        JPanel stats = new JPanel(new GridLayout(1, 4, 18, 0));
        stats.add(makeStat("My Clubs", "" + joinedClubs.size()));
        stats.add(makeStat("Upcoming Events", "" + countUpcomingEvents()));
        stats.add(makeStat("Files", "" + getFilesCount()));
        stats.add(makeStat("Notifications", "" + getNotificationCount()));
        panel.add(stats, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new Object[]{"Club", "Event", "Date/Time", "Venue", "Is My Club?"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        List<Club> allClubs = clubDAO.listAll();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDate today = LocalDate.now();
        for (Club c : allClubs) {
            List<clubconnect.models.Event> evs = eventDAO.getEventsForClub(c.getId());
            for (clubconnect.models.Event e : evs) {
                boolean isPast = false;
                if (e.getDateTime() != null)
                    isPast = e.getDateTime().toInstant().atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate().isBefore(today);
                if (!isPast) {
                    boolean mine = joinedClubs.stream().anyMatch(jc -> jc.getId() == c.getId());
                    String dateStr = (e.getDateTime() != null) ? fmt.format(e.getDateTime().toInstant()
                            .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()) : "";
                    model.addRow(new Object[]{
                            c.getName(), e.getTitle(), dateStr, e.getVenue(),
                            mine ? "Yes" : "No"
                    });
                }
            }
        }
        JTable eventTable = new JTable(model);
        eventTable.setRowHeight(25);

        JTabbedPane eventsTabs = new JTabbedPane();
        eventsTabs.addTab("Upcoming Events", new JScrollPane(eventTable));

        panel.add(eventsTabs, BorderLayout.CENTER);

        DefaultListModel<String> notifModel = new DefaultListModel<>();
        for (String n : getLatestNotifications()) notifModel.addElement(n);
        JList<String> notifList = new JList<>(notifModel);
        notifList.setBackground(new Color(245,245,245));
        notifList.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JPanel notifPanel = new JPanel(new BorderLayout());
        notifPanel.setBorder(BorderFactory.createTitledBorder("Latest Notifications"));
        notifPanel.add(new JScrollPane(notifList), BorderLayout.CENTER);

        panel.add(notifPanel, BorderLayout.SOUTH);

        switchCard("dashboard", panel);
    }

    private JPanel makeStat(String title, String value) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)), new EmptyBorder(10,16,10,16)));
        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JLabel v = new JLabel(value, SwingConstants.CENTER);
        v.setFont(new Font("Segoe UI", Font.BOLD, 24));
        v.setForeground(mainRed);
        p.add(t, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    private int countUpcomingEvents() {
        int count = 0;
        List<Club> allClubs = clubDAO.listAll();
        LocalDate today = LocalDate.now();
        for (Club c : allClubs) {
            List<clubconnect.models.Event> evs = eventDAO.getEventsForClub(c.getId());
            for (clubconnect.models.Event e : evs) {
                if (e.getDateTime() != null) {
                    boolean isPast = e.getDateTime().toInstant().atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate().isBefore(today);
                    if (!isPast) count++;
                }
            }
        }
        return count;
    }

    private int getFilesCount() {
        int count = 0;
        try (Connection conn = DriverManager.getConnection(Config.MYSQL_SERVER_URL + Config.DB_NAME, Config.MYSQL_USER, Config.MYSQL_PASSWORD);
            Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM resources");
            if (rs.next()) count = rs.getInt(1);
        } catch (Exception ignored) {}
        return count;
    }

    private int getNotificationCount() {
        int count = 0;
        try (Connection conn = DriverManager.getConnection(Config.MYSQL_SERVER_URL + Config.DB_NAME, Config.MYSQL_USER, Config.MYSQL_PASSWORD);
            Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM notifications");
            if (rs.next()) count = rs.getInt(1);
        } catch (Exception ignored) {}
        return count;
    }

    private List<String> getLatestNotifications() {
        List<String> out = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(Config.MYSQL_SERVER_URL + Config.DB_NAME, Config.MYSQL_USER, Config.MYSQL_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT title, message, created_at FROM notifications ORDER BY created_at DESC LIMIT 5")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                out.add(rs.getString("title")+ ": " + rs.getString("message")+" ("+rs.getString("created_at")+")");
            }
        } catch (Exception ignored) {}
        return out;
    }

    // ---- My Clubs ----
    private void showMyClubs() {
        reloadUserClubs();
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(18, 26, 18, 26));

        DefaultTableModel model = new DefaultTableModel(new Object[]{"Club Name", "Category"}, 0){
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for(Club c : joinedClubs) model.addRow(new Object[]{c.getName(), c.getCategory()});
        JTable table = new JTable(model);
        table.setRowHeight(27);

        JButton aboutBtn = new JButton("About Club");
        aboutBtn.setBackground(mainRed);
        aboutBtn.setForeground(Color.WHITE);

        aboutBtn.addActionListener(e->{
            int row = table.getSelectedRow();
            if(row == -1){ JOptionPane.showMessageDialog(card, "Select a club."); return; }
            Club c = joinedClubs.get(row);
            showClubFullDetails(c);
        });

        JButton leaveBtn = new JButton("Leave Club");
        leaveBtn.setBackground(accentRed);
        leaveBtn.setForeground(Color.WHITE);
        leaveBtn.addActionListener(e->{
            int row = table.getSelectedRow();
            if(row == -1){ JOptionPane.showMessageDialog(card, "Select a club."); return; }
            Club c = joinedClubs.get(row);
            User cur = parent.getCurrentUser();
            if (cur != null) {
                List<Membership> mems = membershipDAO.getMembershipsForUser(cur.getId());
                for (Membership m : mems)
                    if ("approved".equalsIgnoreCase(m.getStatus()) && m.getClubId() == c.getId())
                        membershipDAO.deleteMembership(m.getId());
            }
            reloadUserClubs();
            showMyClubs();
        });

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnBar.add(aboutBtn);
        btnBar.add(leaveBtn);

        card.add(new JScrollPane(table), BorderLayout.CENTER);
        card.add(btnBar, BorderLayout.SOUTH);
        switchCard("myclubs", card);
    }

    // ---- Clubs Directory ----
    private void showAllClubs() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(18, 26, 18, 26));

        List<Club> allClubs = clubDAO.listAll();
        User cur = parent.getCurrentUser();

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Club Name", "Category", "Description", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (Club c : allClubs) {
            String status = "Not Joined";
            if (cur != null) {
                List<Membership> mems = membershipDAO.getMembershipsForUser(cur.getId());
                for (Membership m : mems) {
                    if (m.getClubId() == c.getId()) {
                        status = m.getStatus().substring(0, 1).toUpperCase() + m.getStatus().substring(1);
                        break;
                    }
                }
            }
            model.addRow(new Object[]{c.getName(), c.getCategory(), c.getDescription(), status});
        }

        JTable table = new JTable(model);
        table.setRowHeight(27);
        JScrollPane scroll = new JScrollPane(table);

        JButton joinBtn = new JButton("Request to Join");
        joinBtn.setBackground(mainRed);
        joinBtn.setForeground(Color.WHITE);

        joinBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(card, "Select a club to join.");
                return;
            }
            Club selected = allClubs.get(table.convertRowIndexToModel(row));
            String currentStatus = (String) model.getValueAt(row, 3);

            if ("Approved".equalsIgnoreCase(currentStatus) || "Pending".equalsIgnoreCase(currentStatus)) {
                JOptionPane.showMessageDialog(card, "You have already joined or requested this club.");
                return;
            }

            if (cur != null) {
                Membership m = new Membership(selected.getId(), cur.getId(), "pending", java.time.LocalDate.now().toString());
                membershipDAO.addMembership(m);
                JOptionPane.showMessageDialog(card, "Join request sent to club administrators.");
                showAllClubs();
            }
        });

        JButton detailsBtn = new JButton("View Details");
        detailsBtn.setBackground(accentRed);
        detailsBtn.setForeground(Color.WHITE);
        detailsBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(card, "Select a club to view details.");
                return;
            }
            Club selected = allClubs.get(table.convertRowIndexToModel(row));
            showClubFullDetails(selected);
        });

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnBar.add(joinBtn);
        btnBar.add(detailsBtn);

        card.add(new JLabel("All Clubs Directory", SwingConstants.CENTER), BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        card.add(btnBar, BorderLayout.SOUTH);

        switchCard("allclubs", card);
    }

    private void showClubFullDetails(Club c) {
        StringBuilder sb = new StringBuilder();
        sb.append("📛 Club Name: ").append(c.getName()).append("\n");
        sb.append("🏷 Category: ").append(c.getCategory()).append("\n\n");
        sb.append("📝 Description:\n").append(c.getDescription() != null ? c.getDescription() : "No description").append("\n\n");

        int members = 0;
        List<Membership> mems = membershipDAO.getMembershipsForClub(c.getId());
        for (Membership m : mems)
            if ("approved".equalsIgnoreCase(m.getStatus()))
                members++;

        List<clubconnect.models.Event> evs = eventDAO.getEventsForClub(c.getId());
        sb.append("👥 Members: ").append(members).append("\n");
        sb.append("📅 Total Events: ").append(evs.size()).append("\n");

        JOptionPane.showMessageDialog(this, sb.toString(), "Club Details", JOptionPane.INFORMATION_MESSAGE);
    }

    // ---- Events ----
    private void showAllEvents() {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(18, 26, 18, 26));
        List<Club> allClubs = clubDAO.listAll();

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Club", "Event", "Date/Time", "Venue", "Details"}, 0){
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for(Club c : allClubs){
            List<clubconnect.models.Event> events = eventDAO.getEventsForClub(c.getId());
            for (clubconnect.models.Event e : events){
                model.addRow(new Object[]{c.getName(),e.getTitle(),e.getDateTime(),e.getVenue(),e.getDetails()});
            }
        }
        JTable table = new JTable(model);
        table.setRowHeight(27);

                JButton commentBtn = new JButton("Comment on Event");
        commentBtn.setBackground(mainRed);
        commentBtn.setForeground(Color.WHITE);
        commentBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(card, "Select an event to comment on.");
                return;
            }
            User cur = parent.getCurrentUser();
            if (cur == null) {
                JOptionPane.showMessageDialog(card, "You must be logged in to comment.");
                return;
            }
            String clubName = (String) model.getValueAt(row, 0);
            String eventTitle = (String) model.getValueAt(row, 1);
            String comment = JOptionPane.showInputDialog(card, "Write your comment for \"" + eventTitle + "\":");
            if (comment != null && !comment.trim().isEmpty()) {
                Club targetClub = clubDAO.findByName(clubName);
                if (targetClub != null) {
                    clubconnect.models.Event ev = eventDAO.findEventByTitleAndClub(eventTitle, targetClub.getId());
                    if (ev != null) {
                        Comment cmt = new Comment(ev.getId(), cur.getId(), comment, java.time.LocalDate.now().toString());
                        commentDAO.addComment(cmt);
                        JOptionPane.showMessageDialog(card, "Comment added successfully.");
                    }
                }
            }
        });

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnBar.add(commentBtn);

        card.add(new JScrollPane(table), BorderLayout.CENTER);
        card.add(btnBar, BorderLayout.SOUTH);
        switchCard("allevents", card);
    }

    // ---- Discussions ----
    private void showDiscussions() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(18, 26, 18, 26));

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Event", "User", "Comment", "Date"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        List<Comment> comments = commentDAO.getAllComments();
        for (Comment c : comments) {
            clubconnect.models.Event ev = eventDAO.findEventById(c.getEventId());
            User u = userDAO.findById(c.getUserId());
            model.addRow(new Object[]{
                    ev != null ? ev.getTitle() : "Unknown",
                    u != null ? u.getFullName() : "Unknown",
                    c.getContent(),
                    c.getCreatedAt()
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(27);
        JScrollPane scroll = new JScrollPane(table);
        card.add(scroll, BorderLayout.CENTER);

        switchCard("discussions", card);
    }

    // ---- Notifications ----
    private void showNotifications() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(18, 26, 18, 26));

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Title", "Message", "Date"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        List<Notification> notifs = notificationDAO.getAll();
        for (Notification n : notifs) {
            model.addRow(new Object[]{n.getTitle(), n.getMessage(), n.getCreatedAt()});
        }

        JTable table = new JTable(model);
        table.setRowHeight(27);
        JScrollPane scroll = new JScrollPane(table);

        card.add(scroll, BorderLayout.CENTER);
        switchCard("notifications", card);
    }
}
