package clubconnect.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Modern Home Page UI for ClubConnect with navigation to About, Clubs, Events panels.
 */
public class HomePanel extends JPanel {

    private final MainFrame parent;
    private boolean isLoggedIn = false; // Simplified login state (can be extended)
    private JTextField searchField;
    private JPanel searchResultsPanel;
    private JButton currentActiveButton; // Visual feedback
    private JPanel breadcrumbPanel;

    // ----- NEW: CardLayout for content area -----
    private final CardLayout contentCardLayout = new CardLayout();
    private final JPanel contentCardPanel = new JPanel(contentCardLayout);

    // Sample demo data
    private final List<Club> clubs = new ArrayList<>();
    private final List<Event> events = new ArrayList<>();
    private List<JPanel> allSearchResults = new ArrayList<>();
    private int currentPage = 0;
    private final int RESULTS_PER_PAGE = 5;

    public HomePanel(MainFrame parent) {
        this.parent = parent;
        initializeSampleData();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(createHeader(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
    }

    private void initializeSampleData() {
        clubs.add(new Club("🤖", "Soccer Club", "Explore soccer in our student-led club."));
        clubs.add(new Club("📖", "Literature Society", "Discuss books and write in a creative community."));
        clubs.add(new Club("🎨", "Art Club", "Express your creativity through art forms."));
        clubs.add(new Club("🎮", "Gaming Club", "Join us for tournaments and game nights."));

        events.add(new Event("Nov", "12", "Book Club Meeting", "LB108 • 5:00 pm"));
        events.add(new Event("Nov", "13", "Football Game", "SB204 • 2:00 pm"));
        events.add(new Event("Nov", "15", "Community Service", "Campus Center"));
        events.add(new Event("Dec", "01", "Winter Fest", "Main Hall • 6:00 pm"));
    }

    // ---------- HEADER ----------
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(128, 0, 0)); // Burgundy

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        left.setBackground(new Color(128, 0, 0));
        JLabel logo = new JLabel("🔴");
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        JLabel name = new JLabel("<html><b style='font-size:16px;color:white;'>CLUBCONNECT</b><br>"
                + "<span style='font-size:12px;color:white;'>University Clubs Management System</span></html>");
        left.add(logo);
        left.add(name);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        right.setBackground(new Color(128, 0, 0));
        String[] nav = {"Home", "Clubs", "Events", "About", "Login", "Register"};
        for (String n : nav) {
            JButton btn = new JButton(n);
            btn.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addHoverColor(btn);
            btn.addActionListener(e -> handleNavigation(n, btn));
            right.add(btn);
            if (n.equals("Home")) setActiveButton(btn);
        }
        JButton exitBtn = new JButton("Exit");
        exitBtn.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        exitBtn.setForeground(Color.WHITE);
        exitBtn.setFocusPainted(false);
        exitBtn.setBorderPainted(false);
        exitBtn.setContentAreaFilled(false);
        exitBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addHoverColor(exitBtn);
        exitBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to exit?", "Exit Confirmation",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION)
                System.exit(0);
        });
        right.add(exitBtn);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    // ---------- MAIN CONTENT ----------
    private JPanel createMainContent() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(40, 60, 40, 60));

        // Breadcrumb for navigation
        breadcrumbPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        breadcrumbPanel.setBackground(Color.WHITE);
        updateBreadcrumb("Home");
        content.add(breadcrumbPanel, BorderLayout.NORTH);

        // ----- NEW: CardPanel for internal navigation -----
        // Home panel layout (your original design):
        JPanel homeCard = new JPanel(new BorderLayout());
        homeCard.setBackground(Color.WHITE);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setBackground(Color.WHITE);
        JLabel title = new JLabel("Welcome to ClubConnect", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 32));
        JLabel subtitle = new JLabel("University Clubs Management System At Botho University Lesotho", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitle.setForeground(Color.GRAY);
        titlePanel.add(title);
        titlePanel.add(subtitle);
        homeCard.add(titlePanel, BorderLayout.NORTH);

        // Search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 20));
        searchPanel.setBackground(Color.WHITE);
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(300, 35));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(0, 10, 0, 10)
        ));
        JButton searchBtn = new JButton("🔍");
        searchBtn.setPreferredSize(new Dimension(45, 35));
        searchBtn.setBackground(new Color(128, 0, 0));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setBorderPainted(false);
        addHoverColor(searchBtn);
        searchBtn.addActionListener(e -> performSearch());
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        homeCard.add(searchPanel, BorderLayout.CENTER);

        // Search results
        searchResultsPanel = new JPanel();
        searchResultsPanel.setLayout(new BoxLayout(searchResultsPanel, BoxLayout.Y_AXIS));
        searchResultsPanel.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(searchResultsPanel);
        scrollPane.setBorder(null);
        scrollPane.setPreferredSize(new Dimension(700, 300));
        homeCard.add(scrollPane, BorderLayout.SOUTH);

        // Clubs and Events section (only in the welcome card)
        JPanel cardsPanel = new JPanel(new GridBagLayout());
        cardsPanel.setBackground(Color.WHITE);
        cardsPanel.setBorder(new EmptyBorder(30, 0, 0, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // Clubs
        JPanel clubContainer = new JPanel(new GridLayout(1, 2, 30, 0));
        clubContainer.setBackground(Color.WHITE);
        for (Club club : clubs) {
            clubContainer.add(createClubCard(club));
        }
        gbc.gridx = 0;
        gbc.gridy = 0;
        cardsPanel.add(clubContainer, gbc);

        // Events
        JPanel eventContainer = new JPanel();
        eventContainer.setLayout(new BoxLayout(eventContainer, BoxLayout.Y_AXIS));
        eventContainer.setBackground(Color.WHITE);

        JLabel eventTitle = new JLabel("Upcoming Events");
        eventTitle.setFont(new Font("Segoe UI Semibold", Font.BOLD, 22));
        eventTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        eventTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        eventContainer.add(eventTitle);

        for (Event event : events) {
            JPanel eventCard = createEventCard(event);
            eventContainer.add(eventCard);
            eventContainer.add(Box.createVerticalStrut(10));
        }
        animateUpcomingEvents(eventContainer);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.add(eventContainer, BorderLayout.CENTER);
        gbc.gridx = 1;
        gbc.gridy = 0;
        cardsPanel.add(rightPanel, gbc);

        homeCard.add(cardsPanel, BorderLayout.AFTER_LAST_LINE);

        // ----- CardLayout area -----
        contentCardPanel.add(homeCard, "Home");
        contentCardPanel.add(new ClubsPanel(), "Clubs");
        contentCardPanel.add(new EventsPanel(), "Events");
        contentCardPanel.add(new AboutPanel(parent), "About"); // for navigation back to home

        content.add(contentCardPanel, BorderLayout.CENTER);
        contentCardLayout.show(contentCardPanel, "Home");
        return content;
    }

    // ---------- FOOTER ----------
    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBorder(new EmptyBorder(20, 0, 20, 0));
        footer.setBackground(Color.WHITE);
        JLabel footerLabel = new JLabel(
                "© 2025 ClubConnect | University Clubs Management System by Seabata Sechaba 2333779 CALL +26656171110",
                SwingConstants.CENTER);
        footerLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        footerLabel.setForeground(Color.GRAY);
        footer.add(footerLabel, BorderLayout.CENTER);
        return footer;
    }

    // ---------- COMPONENT HELPERS ----------
    private JPanel createClubCard(Club club) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel iconLabel = new JLabel(club.icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(club.name, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel("<html><div style='text-align:center;color:gray;'>" + club.description + "</div></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton learnMore = new JButton("Learn More");
        learnMore.setBackground(new Color(128, 0, 0));
        learnMore.setForeground(Color.WHITE);
        learnMore.setFont(new Font("Segoe UI", Font.BOLD, 13));
        learnMore.setFocusPainted(false);
        learnMore.setAlignmentX(Component.CENTER_ALIGNMENT);
        learnMore.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        addHoverColor(learnMore);
        learnMore.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "More information about " + club.name + " coming soon!"));

        card.add(iconLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(descLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(learnMore);

        return card;
    }

    private JPanel createEventCard(Event event) {
        JPanel eventCard = new JPanel(new BorderLayout(10, 0));
        eventCard.setBackground(Color.WHITE);
        eventCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // Date box
        JPanel dateBox = new JPanel(new GridLayout(2, 1));
        dateBox.setBackground(new Color(128, 0, 0));
        dateBox.setPreferredSize(new Dimension(50, 45));
        JLabel m = new JLabel(event.month, SwingConstants.CENTER);
        m.setForeground(Color.WHITE);
        m.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel d = new JLabel(event.day, SwingConstants.CENTER);
        d.setForeground(Color.WHITE);
        d.setFont(new Font("Segoe UI", Font.BOLD, 16));
        dateBox.add(m);
        dateBox.add(d);

        JPanel textBox = new JPanel(new GridLayout(2, 1));
        textBox.setBackground(Color.WHITE);
        JLabel t = new JLabel(event.title);
        t.setFont(new Font("Segoe UI Semibold", Font.BOLD, 14));
        JLabel det = new JLabel(event.details);
        det.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        det.setForeground(Color.GRAY);
        textBox.add(t);
        textBox.add(det);

        eventCard.add(dateBox, BorderLayout.WEST);
        eventCard.add(textBox, BorderLayout.CENTER);
        return eventCard;
    }

    private void animateUpcomingEvents(JPanel eventContainer) {
        for (Component comp : eventContainer.getComponents()) {
            if (comp instanceof JPanel && comp != eventContainer.getComponent(0)) {
                animatePop((JPanel) comp);
            }
        }
    }

    private void animatePop(JPanel panel) {
        final int steps = 10;
        final int delay = 50;
        final Dimension originalSize = panel.getPreferredSize();
        Timer timer = new Timer(delay, null);
        final int[] currentStep = {0};
        timer.addActionListener(e -> {
            currentStep[0]++;
            float scale = 1 + 0.3f * (1 - Math.abs(steps / 2 - currentStep[0]) / (float) (steps / 2));
            int newWidth = (int) (originalSize.width * scale);
            int newHeight = (int) (originalSize.height * scale);
            panel.setPreferredSize(new Dimension(newWidth, newHeight));
            panel.revalidate();
            panel.repaint();
            if (currentStep[0] >= steps) {
                panel.setPreferredSize(originalSize);
                panel.revalidate();
                panel.repaint();
                ((Timer) e.getSource()).stop();
            }
        });
        timer.start();
    }

    private void addHoverColor(AbstractButton btn) {
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn != currentActiveButton) {
                    btn.setForeground(new Color(255, 180, 180));
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn != currentActiveButton) {
                    btn.setForeground(Color.WHITE);
                }
            }
        });
    }

    private void setActiveButton(JButton btn) {
        if (currentActiveButton != null) currentActiveButton.setForeground(Color.WHITE);
        currentActiveButton = btn;
        if (btn != null) currentActiveButton.setForeground(Color.YELLOW);
    }

    private void updateBreadcrumb(String currentPage) {
        breadcrumbPanel.removeAll();
        JLabel homeLabel = new JLabel("Home");
        homeLabel.setForeground(Color.BLUE);
        homeLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        homeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                contentCardLayout.show(contentCardPanel, "Home");
                setActiveButton(null);
                updateBreadcrumb("Home");
            }
        });
        breadcrumbPanel.add(homeLabel);
        if (!currentPage.equals("Home")) {
            breadcrumbPanel.add(new JLabel(" > "));
            breadcrumbPanel.add(new JLabel(currentPage));
        }
        breadcrumbPanel.revalidate();
        breadcrumbPanel.repaint();
    }

    private void handleNavigation(String link, JButton btn) {
        String lowerLink = link.toLowerCase();
        setActiveButton(btn);
        updateBreadcrumb(link);
        switch (lowerLink) {
            case "home" -> contentCardLayout.show(contentCardPanel, "Home");
            case "login" -> parent.showCard("login");
            case "register" -> parent.showCard("register");
            case "clubs" -> contentCardLayout.show(contentCardPanel, "Clubs");
            case "events" -> contentCardLayout.show(contentCardPanel, "Events");
            case "about" -> contentCardLayout.show(contentCardPanel, "About");
        }
    }

    // ---------- SEARCH ----------
    private void performSearch() {
        String query = searchField.getText().toLowerCase().trim();
        allSearchResults.clear();
        currentPage = 0;

        if (query.isEmpty()) {
            searchResultsPanel.removeAll();
            searchResultsPanel.add(new JLabel("Please enter a search term."));
            refreshSearchResults();
            return;
        }

        for (Club club : clubs) {
            if (club.name.toLowerCase().contains(query) ||
                club.description.toLowerCase().contains(query)) {
                allSearchResults.add(createClubCard(club));
            }
        }
        for (Event event : events) {
            if (event.title.toLowerCase().contains(query) ||
                event.details.toLowerCase().contains(query) ||
                event.month.toLowerCase().contains(query) ||
                event.day.toLowerCase().contains(query)) {
                allSearchResults.add(createEventCard(event));
            }
        }
        if (allSearchResults.isEmpty()) {
            searchResultsPanel.removeAll();
            searchResultsPanel.add(new JLabel("No results found for \"" + query + "\"."));
        } else {
            displaySearchPage();
        }
        refreshSearchResults();
    }

    private void displaySearchPage() {
        searchResultsPanel.removeAll();
        int start = currentPage * RESULTS_PER_PAGE;
        int end = Math.min(start + RESULTS_PER_PAGE, allSearchResults.size());
        for (int i = start; i < end; i++) {
            searchResultsPanel.add(allSearchResults.get(i));
            searchResultsPanel.add(Box.createVerticalStrut(10));
        }
        JPanel paginationPanel = new JPanel(new FlowLayout());
        if (currentPage > 0) {
            JButton prevBtn = new JButton("Previous");
            prevBtn.addActionListener(e -> {
                currentPage--;
                displaySearchPage();
                refreshSearchResults();
            });
            paginationPanel.add(prevBtn);
        }
        if (end < allSearchResults.size()) {
            JButton nextBtn = new JButton("Next");
            nextBtn.addActionListener(e -> {
                currentPage++;
                displaySearchPage();
                refreshSearchResults();
            });
            paginationPanel.add(nextBtn);
        }
        searchResultsPanel.add(paginationPanel);
    }

    private void refreshSearchResults() {
        searchResultsPanel.revalidate();
        searchResultsPanel.repaint();
    }

    // ---------- DATA CLASSES ----------
    private static class Club {
        String icon, name, description;
        Club(String icon, String name, String description) {
            this.icon = icon;
            this.name = name;
            this.description = description;
        }
    }

    private static class Event {
        String month, day, title, details;
        Event(String month, String day, String title, String details) {
            this.month = month;
            this.day = day;
            this.title = title;
            this.details = details;
        }
    }
}