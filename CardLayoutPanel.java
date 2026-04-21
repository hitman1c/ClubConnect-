package clubconnect.ui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * CardLayoutPanel - navigation manager for all app screens
 * Each screen is a JPanel registered with a unique name (card).
 */
public class CardLayoutPanel extends JPanel {
    private final CardLayout cardLayout;
    private final Map<String, JPanel> cards = new HashMap<>();
    private final MainFrame parent;

    public CardLayoutPanel(MainFrame parent) {
        this.parent = parent;
        this.cardLayout = new CardLayout();
        setLayout(cardLayout);

        // Register all main screens (cards)
        addCard("home", new HomePanel(parent));
        addCard("login", new LoginPanel(parent));
        addCard("register", new RegisterPanel(parent));
        addCard("admin", new AdminPanel(parent));
        addCard("member", new MemberPanel(parent));
        // LeaderPanel is always created fresh after login, so not added now

        addCard("club_mgmt", new ClubManagementPanel(parent));
        addCard("event_mgmt", new EventManagementPanel(parent));
        addCard("attendance", new AttendancePanel(parent));
        addCard("membership_approval", new MembershipApprovalPanel(parent));
        addCard("resource_booking", new ResourceBookingPanel(parent));
        addCard("budget_request", new BudgetRequestPanel(parent));
        addCard("reports", new ReportsPanel(parent));
        addCard("notifications", new NotificationsPanel(parent));

        // Custom tab-style landing pages for guests/anyone:
        addCard("about", new AboutPanel(parent)); // About tab (brand details, etc.)
        addCard("clubs", new ClubsPanel());       // Clubs guest view (shows DB-backed clubs, animated)
        addCard("events", new EventsPanel());     // Events guest view (shows DB-backed events, animated)

        // Default view
        show("home");
    }

    public void addCard(String name, JPanel panel) {
        cards.put(name, panel);
        add(panel, name);
    }

    /**
     * Show the named card, recreating leader or other dynamic panels if needed.
     */
    public void show(String name) {
        // For leader screen, always recreate with current session details
        if ("leader".equals(name)) {
            if (cards.containsKey("leader")) remove(cards.get("leader"));
            JPanel leaderPanel = new LeaderPanel(parent);
            addCard("leader", leaderPanel);
        }
        cardLayout.show(this, name);
    }

    public JPanel getCard(String name) {
        return cards.get(name);
    }
}