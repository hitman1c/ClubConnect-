package clubconnect.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Vector;
import clubconnect.util.Config;
import java.util.Calendar;

/**

* Enhanced EventsPanel
* Features:
* * Loads events from the database dynamically
* * Shows event details in a modal dialog
* * Guests can submit feedback stored in the database
* * Past events displayed in red
* * Filter events by date: Today / Upcoming / All
    */
    public class EventsPanel extends JPanel {

  private JPanel cardsPanel;
  private JLabel statusLabel;
  private JComboBox<String> filterCombo;

  public EventsPanel() {
  setLayout(new BorderLayout());
  setBackground(Color.WHITE);

  add(createHeader(), BorderLayout.NORTH);
  add(createContent(), BorderLayout.CENTER);
  add(createFooter(), BorderLayout.SOUTH);

  loadEvents("All");

  }

  // ================= Header =================
  private JPanel createHeader() {
  JPanel header = new JPanel(new BorderLayout());
  header.setBackground(new Color(128, 0, 0));
  header.setBorder(new EmptyBorder(12, 16, 12, 16));
  JLabel title = new JLabel("CLUB EVENTS");
  title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 20));
  title.setForeground(Color.WHITE);
  header.add(title, BorderLayout.WEST);
  return header;
  }

  // ================= Content =================
  private JPanel createContent() {
  JPanel content = new JPanel(new BorderLayout());
  content.setBackground(Color.WHITE);
  content.setBorder(new EmptyBorder(20, 28, 20, 28));

  // Top controls: refresh + filter
  JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
  controls.setBackground(Color.WHITE);

  JButton refreshBtn = new JButton("Refresh");
  refreshBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
  refreshBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  refreshBtn.addActionListener(e -> loadEvents((String) filterCombo.getSelectedItem()));

  statusLabel = new JLabel(" ");
  statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
  statusLabel.setForeground(Color.GRAY);

  // Filter dropdown
  filterCombo = new JComboBox<>(new String[]{"All", "Today", "Upcoming"});
  filterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
  filterCombo.addActionListener(e -> loadEvents((String) filterCombo.getSelectedItem()));

  controls.add(refreshBtn);
  controls.add(new JLabel(" | Filter by: "));
  controls.add(filterCombo);
  controls.add(statusLabel);

  content.add(controls, BorderLayout.NORTH);

  // Cards panel inside scroll
  cardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 32, 32));
  cardsPanel.setBackground(Color.WHITE);
  JScrollPane scroll = new JScrollPane(cardsPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
  scroll.getVerticalScrollBar().setUnitIncrement(24);
  scroll.setBorder(BorderFactory.createEmptyBorder());
  scroll.setPreferredSize(new Dimension(820, 470));
  content.add(scroll, BorderLayout.CENTER);

  return content;

  }

  // ================= Footer =================
  private JPanel createFooter() {
  JPanel footer = new JPanel(new BorderLayout());
  footer.setBorder(new EmptyBorder(14, 0, 7, 0));
  footer.setBackground(Color.WHITE);
  JLabel footerLabel = new JLabel(
  "© 2025 ClubConnect | Interactive Events Panel | Botho University",
  SwingConstants.CENTER);
  footerLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
  footerLabel.setForeground(Color.GRAY);
  footer.add(footerLabel, BorderLayout.CENTER);
  return footer;
  }

  // ================= Load Events =================
  private void loadEvents(String filter) {
  cardsPanel.removeAll();
  statusLabel.setText("Loading events...");
  SwingWorker<Vector<Vector<Object>>, Void> worker = new SwingWorker<>() {
      @Override
      protected Vector<Vector<Object>> doInBackground() {
          Vector<Vector<Object>> rows = new Vector<>();
          String query = "SELECT id, title, date_time, venue, details FROM events ORDER BY date_time DESC";
          try (Connection conn = DriverManager.getConnection(
                  Config.MYSQL_SERVER_URL + Config.DB_NAME,
                  Config.MYSQL_USER,
                  Config.MYSQL_PASSWORD);
               Statement st = conn.createStatement();
               ResultSet rs = st.executeQuery(query)) {

              while (rs.next()) {
                  Timestamp ts = rs.getTimestamp("date_time");
                  boolean include = switch (filter) {
                      case "Today" -> ts != null && isToday(ts);
                      case "Upcoming" -> ts != null && ts.after(new Timestamp(System.currentTimeMillis()));
                      default -> true;
                  };
                  if (include) {
                      Vector<Object> r = new Vector<>();
                      r.add(rs.getInt("id"));
                      r.add(rs.getString("title"));
                      r.add(ts);
                      r.add(rs.getString("venue"));
                      r.add(rs.getString("details"));
                      rows.add(r);
                  }
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
                  cardsPanel.add(new JLabel("No events found."));
              } else {
                  for (Vector<Object> row : rows) {
                      JPanel eventCard = createEventCard(row);
                      cardsPanel.add(eventCard);
                      animateFadeIn(eventCard);
                  }
              }
              statusLabel.setText("Loaded " + rows.size() + " event(s). Click a card to view details & feedback.");
              cardsPanel.revalidate();
              cardsPanel.repaint();
          } catch (Exception ex) {
              statusLabel.setText("Failed to load events.");
              cardsPanel.add(new JLabel("Error loading events: " + ex.getMessage()));
              cardsPanel.revalidate();
              cardsPanel.repaint();
          }
      }
  };
  worker.execute();

  }

  // ================= Helper: check if timestamp is today =================
  private boolean isToday(Timestamp ts) {
  Calendar calEvent = Calendar.getInstance();
  calEvent.setTime(ts);
  Calendar calNow = Calendar.getInstance();
  return calEvent.get(Calendar.YEAR) == calNow.get(Calendar.YEAR) &&
  calEvent.get(Calendar.DAY_OF_YEAR) == calNow.get(Calendar.DAY_OF_YEAR);
  }

  // ================= Event Card =================
  private JPanel createEventCard(Vector<Object> row) {
  int id = (int) row.get(0);
  String title = (String) row.get(1);
  Timestamp ts = (Timestamp) row.get(2);
  String dateStr = (ts != null) ? new SimpleDateFormat("yyyy-MM-dd HH:mm").format(ts) : "TBA";
  String venue = (String) row.get(3);
  String details = (String) row.get(4);

  boolean isPast = ts != null && ts.before(new Timestamp(System.currentTimeMillis()));

  JPanel card = new JPanel();
  card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
  card.setBackground(isPast ? new Color(255, 200, 200) : new Color(255, 245, 245));
  card.setPreferredSize(new Dimension(270, 180));
  card.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createLineBorder(isPast ? new Color(200, 50, 50) : new Color(230, 200, 200), 2, true),
          new EmptyBorder(14, 14, 14, 14)
  ));

  JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
  titleLbl.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
  titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
  if (isPast) titleLbl.setForeground(new Color(150, 0, 0));

  JLabel dateLbl = new JLabel("Date/Time: " + dateStr, SwingConstants.CENTER);
  dateLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
  dateLbl.setForeground(isPast ? new Color(150, 0, 0) : new Color(100, 60, 60));
  dateLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

  JLabel venueLbl = new JLabel("Venue: " + venue, SwingConstants.CENTER);
  venueLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
  venueLbl.setForeground(isPast ? new Color(150, 0, 0) : new Color(80, 80, 80));
  venueLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

  JTextArea detailsLbl = new JTextArea(details == null ? "" : details);
  detailsLbl.setEditable(false);
  detailsLbl.setBackground(card.getBackground());
  detailsLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
  detailsLbl.setWrapStyleWord(true);
  detailsLbl.setLineWrap(true);
  detailsLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

  JButton detailsBtn = new JButton("View & Feedback");
  detailsBtn.setBackground(isPast ? new Color(180, 0, 0) : new Color(128, 0, 0));
  detailsBtn.setForeground(Color.WHITE);
  detailsBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
  detailsBtn.setFocusPainted(false);
  detailsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
  detailsBtn.addActionListener(e -> showEventDetails(id, title, dateStr, venue, details));

  card.add(titleLbl);
  card.add(Box.createVerticalStrut(7));
  card.add(dateLbl);
  card.add(Box.createVerticalStrut(2));
  card.add(venueLbl);
  card.add(Box.createVerticalStrut(6));
  card.add(detailsLbl);
  card.add(Box.createVerticalStrut(10));
  if (isPast) {
      JLabel pastLbl = new JLabel("Event Completed", SwingConstants.CENTER);
      pastLbl.setForeground(new Color(150, 0, 0));
      pastLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
      pastLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
      card.add(pastLbl);
  }
  card.add(detailsBtn);

  return card;

  }

  // ================= Event Details + Feedback =================
  private void showEventDetails(int eventId, String title, String dateStr, String venue, String details) {
  JPanel panel = new JPanel(new BorderLayout());
  panel.setBackground(Color.WHITE);
  panel.setBorder(new EmptyBorder(22, 22, 18, 22));
  JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
  titleLbl.setFont(new Font("Segoe UI Semibold", Font.BOLD, 20));
  JLabel dateLbl = new JLabel("Date/Time: " + dateStr, SwingConstants.CENTER);
  dateLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
  JLabel venueLbl = new JLabel("Venue: " + venue, SwingConstants.CENTER);
  venueLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));

  JTextArea detailsLbl = new JTextArea(details == null ? "" : details);
  detailsLbl.setEditable(false);
  detailsLbl.setBackground(Color.WHITE);
  detailsLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
  detailsLbl.setWrapStyleWord(true);
  detailsLbl.setLineWrap(true);

  panel.add(titleLbl, BorderLayout.NORTH);

  JPanel infoPanel = new JPanel();
  infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
  infoPanel.setBackground(Color.WHITE);
  infoPanel.add(dateLbl);
  infoPanel.add(venueLbl);
  infoPanel.add(Box.createVerticalStrut(10));
  infoPanel.add(detailsLbl);

  panel.add(infoPanel, BorderLayout.CENTER);

  JButton feedbackBtn = new JButton("Submit Feedback");
  feedbackBtn.setBackground(new Color(0, 128, 0));
  feedbackBtn.setForeground(Color.WHITE);
  feedbackBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
  feedbackBtn.setFocusPainted(false);
  feedbackBtn.setBorderPainted(false);
  feedbackBtn.addActionListener(e -> submitFeedback(eventId, panel));

  JButton closeBtn = new JButton("Close");
  closeBtn.setBackground(new Color(128, 0, 0));
  closeBtn.setForeground(Color.WHITE);
  closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
  closeBtn.setFocusPainted(false);
  closeBtn.addActionListener(e -> {
      Window win = SwingUtilities.getWindowAncestor(panel);
      if (win != null) win.dispose();
  });

  JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER));
  actions.setBackground(Color.WHITE);
  actions.add(feedbackBtn);
  actions.add(closeBtn);

  JDialog dialog = new JDialog((Frame) null, "Event Details", Dialog.ModalityType.APPLICATION_MODAL);
  dialog.setLayout(new BorderLayout());
  dialog.add(panel, BorderLayout.CENTER);
  dialog.add(actions, BorderLayout.SOUTH);
  dialog.setSize(550, 380);
  dialog.setLocationRelativeTo(this);
  dialog.setVisible(true);

  }

  // ================= Feedback Submission =================
  private void submitFeedback(int eventId, JPanel parent) {
  JTextArea feedbackArea = new JTextArea(6, 30);
  feedbackArea.setLineWrap(true);
  feedbackArea.setWrapStyleWord(true);
  JScrollPane scroll = new JScrollPane(feedbackArea);

  int result = JOptionPane.showConfirmDialog(parent, scroll, "Enter your feedback", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
  if (result == JOptionPane.OK_OPTION) {
      String feedback = feedbackArea.getText().trim();
      if (!feedback.isEmpty()) {
          try (Connection conn = DriverManager.getConnection(
                  Config.MYSQL_SERVER_URL + Config.DB_NAME,
                  Config.MYSQL_USER,
                  Config.MYSQL_PASSWORD);
               PreparedStatement ps = conn.prepareStatement(
                       "INSERT INTO event_feedback(event_id, feedback_text, created_at) VALUES (?, ?, NOW())")) {
              ps.setInt(1, eventId);
              ps.setString(2, feedback);
              ps.executeUpdate();
              JOptionPane.showMessageDialog(parent, "Thank you for your feedback!", "Feedback Submitted", JOptionPane.INFORMATION_MESSAGE);
          } catch (Exception ex) {
              JOptionPane.showMessageDialog(parent, "Failed to submit feedback: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
          }
      }
  }

  }

  // ================= Fade-in Animation =================
  private void animateFadeIn(JPanel panel) {
  panel.setOpaque(false);
  Timer t = new Timer(18, null);
  final int[] step = {0};
  t.addActionListener(e -> {
  panel.setOpaque(true);
  panel.setBackground(new Color(255, 245, 245, Math.min(255, step[0] * 18)));
  panel.repaint();
  step[0]++;
  if (step[0] > 14) t.stop();
  });
  t.start();
  }
  }
