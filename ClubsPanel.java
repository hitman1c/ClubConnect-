package clubconnect.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;
import clubconnect.util.Config;

/**
 * ClubsPanel - Modern clubs page with animated card view and detail dialog, loading data from MySQL DB.
 * Shows all clubs, fully animated. Clicking a club card shows full details (from DB) in a nice dialog.
 */
public class ClubsPanel extends JPanel {

    private JTable clubsTable;
    private DefaultTableModel clubsModel;
    private JLabel statusLabel;
    private JPanel cardsPanel;

    public ClubsPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);

        loadClubs();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(128, 0, 0));
        header.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel title = new JLabel("CLUBS");
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        header.add(title, BorderLayout.WEST);
        return header;
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(20, 28, 20, 28));

        // Controls
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        controls.setBackground(Color.WHITE);
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        refreshBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> loadClubs());

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(Color.GRAY);
        controls.add(refreshBtn);
        controls.add(statusLabel);

        content.add(controls, BorderLayout.NORTH);

        // Panel for cards
        cardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 30));
        cardsPanel.setBackground(Color.WHITE);
        JScrollPane scroll = new JScrollPane(cardsPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(24);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setPreferredSize(new Dimension(820, 470));
        content.add(scroll, BorderLayout.CENTER);

        return content;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBorder(new EmptyBorder(14, 0, 7, 0));
        footer.setBackground(Color.WHITE);
        JLabel footerLabel = new JLabel(
                "© 2025 ClubConnect | Demo Clubs Panel | Botho University",
                SwingConstants.CENTER);
        footerLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        footerLabel.setForeground(Color.GRAY);
        footer.add(footerLabel, BorderLayout.CENTER);
        return footer;
    }

    private void loadClubs() {
        cardsPanel.removeAll();
        statusLabel.setText("Loading clubs...");
        SwingWorker<Vector<Vector<Object>>, Void> worker = new SwingWorker<>() {
            @Override
            protected Vector<Vector<Object>> doInBackground() {
                Vector<Vector<Object>> rows = new Vector<>();
                try (Connection conn = DriverManager.getConnection(
                        Config.MYSQL_SERVER_URL + Config.DB_NAME,
                        Config.MYSQL_USER,
                        Config.MYSQL_PASSWORD);
                     Statement st = conn.createStatement();
                     ResultSet rs = st.executeQuery("SELECT id, name, category, description FROM clubs ORDER BY name")) {
                    while (rs.next()) {
                        Vector<Object> r = new Vector<>();
                        r.add(rs.getInt("id"));
                        r.add(rs.getString("name"));
                        r.add(rs.getString("category"));
                        r.add(rs.getString("description"));
                        rows.add(r);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return rows;
            }

            @Override
            protected void done() {
                try {
                    Vector<Vector<Object>> rows = get();
                    if (rows.isEmpty()) {
                        cardsPanel.add(new JLabel("No clubs found in database."));
                    } else {
                        for (Vector<Object> row : rows) {
                            JPanel clubCard = createClubCard(row);
                            cardsPanel.add(clubCard);
                            animateFadeIn(clubCard);
                        }
                    }
                    statusLabel.setText("Loaded " + rows.size() + " club(s). Click a card for full details.");
                    cardsPanel.revalidate();
                    cardsPanel.repaint();
                } catch (Exception ex) {
                    statusLabel.setText("Failed to load clubs.");
                    cardsPanel.add(new JLabel("Error loading clubs: " + ex.getMessage()));
                    cardsPanel.revalidate();
                    cardsPanel.repaint();
                }
            }
        };
        worker.execute();
    }

    // Card with animation, click to show details
    private JPanel createClubCard(Vector<Object> row) {
        int id = (int)row.get(0);
        String name = (String)row.get(1);
        String cat = (String)row.get(2);
        String desc = (String)row.get(3);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(260, 220));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2, true),
                new EmptyBorder(20,20,18,20)
        ));

        JLabel icon = emojiIconForCat(cat);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 38));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLbl = new JLabel(name, SwingConstants.CENTER);
        nameLbl.setFont(new Font("Segoe UI Semibold", Font.BOLD, 17));
        nameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel catLbl = new JLabel(cat, SwingConstants.CENTER);
        catLbl.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        catLbl.setForeground(new Color(128,0,0));
        catLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLbl = new JLabel("<html><center>" + (desc == null ? "" : desc) + "</center></html>");
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLbl.setForeground(new Color(80,80,80));
        descLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(icon); card.add(Box.createVerticalStrut(6));
        card.add(nameLbl); card.add(Box.createVerticalStrut(4));
        card.add(catLbl); card.add(Box.createVerticalStrut(5));
        card.add(descLbl); card.add(Box.createVerticalStrut(18));

        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setToolTipText("View full details");
        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showClubDetails(id);
            }
            public void mouseEntered(MouseEvent e) { card.setBackground(new Color(255,238,238)); }
            public void mouseExited(MouseEvent e) { card.setBackground(Color.WHITE); }
        });

        return card;
    }

    private JLabel emojiIconForCat(String cat) {
        String icon = "🏛️";
        if (cat != null) {
            if (cat.contains("Art")) icon = "🎨";
            else if (cat.contains("Music")) icon = "🎼";
            else if (cat.contains("Sport") || cat.contains("Soccer") || cat.contains("Tennis")) icon = "⚽";
            else if (cat.contains("Tech") || cat.contains("Robotics")) icon = "🤖";
        }
        JLabel lbl = new JLabel(icon, SwingConstants.CENTER);
        return lbl;
    }

    // Details dialog with fade-in animation
    private void showClubDetails(int clubId) {
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override protected String doInBackground() {
                StringBuilder sb = new StringBuilder();
                try (Connection conn = DriverManager.getConnection(
                        Config.MYSQL_SERVER_URL + Config.DB_NAME,
                        Config.MYSQL_USER,
                        Config.MYSQL_PASSWORD)) {
                    try (PreparedStatement ps = conn.prepareStatement(
                            "SELECT name, category, description FROM clubs WHERE id = ?")) {
                        ps.setInt(1, clubId);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                sb.append("<h2>").append(rs.getString("name")).append("</h2>");
                                sb.append("<b>Category:</b> ").append(rs.getString("category")).append("<br>");
                                String desc = rs.getString("description");
                                if (desc != null && !desc.isBlank())
                                    sb.append("<b>Description:</b><br>").append(desc);
                            } else {
                                sb.append("<i>Club not found.</i>");
                            }
                        }
                    }
                } catch (Exception e) { throw new RuntimeException(e); }
                return sb.toString();
            }
            @Override protected void done() {
                try {
                    String html = get();
                    JEditorPane pane = new JEditorPane("text/html", "<html><body style='font-family:Segoe UI,Arial;font-size:15px;'>" + html + "</body></html>");
                    pane.setEditable(false);
                    pane.setPreferredSize(new Dimension(440,180));
                    JScrollPane sp = new JScrollPane(pane);

                    JDialog dialog = new JDialog((Frame)null, "Club Details", Dialog.ModalityType.APPLICATION_MODAL);
                    dialog.setLayout(new BorderLayout());
                    dialog.setSize(520, 300);
                    dialog.setLocationRelativeTo(ClubsPanel.this);
                    dialog.add(sp, BorderLayout.CENTER);

                    JButton close = new JButton("Close");
                    close.setBackground(new Color(128,0,0));
                    close.setForeground(Color.WHITE);
                    close.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    close.setFocusPainted(false);
                    close.addActionListener(e -> dialog.dispose());
                    dialog.add(close, BorderLayout.SOUTH);

                    animateDialogShow(dialog);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ClubsPanel.this,
                            "Error fetching club details:\n" + ex.getMessage(),
                            "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    // Fade-in animation for cards
    private void animateFadeIn(JPanel panel) {
        panel.setOpaque(false);
        Timer t = new Timer(18, null);
        final int[] step = {0};
        t.addActionListener(e -> {
            panel.setOpaque(true);
            panel.setBackground(new Color(255,255,255, Math.min(255, step[0]*18)));
            panel.repaint();
            step[0]++;
            if (step[0] > 14) t.stop();
        });
        t.start();
    }
    // Fade-in dialog animation
    private void animateDialogShow(JDialog dialog) {
        Timer t = new Timer(22, null);
        int total = 10;
        final int[] step = {0};
        dialog.setOpacity(0f);
        t.addActionListener(e -> {
            step[0]++;
            dialog.setOpacity(Math.min(1f, step[0]/(float)total));
            if (step[0]>=total) t.stop();
        });
        dialog.setVisible(true);
        t.start();
    }
}