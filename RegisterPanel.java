package clubconnect.ui;

import clubconnect.dao.ClubDAO;
import clubconnect.models.Club;
import clubconnect.service.UserService;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

/**
 * Polished RegisterPanel for ClubConnect.
 * - Async loads clubs from ClubDAO
 * - Multiline renderer for club dropdown
 * - Placeholder (hint) fields
 * - Confirm password + improved validation
 * - Styled card with gradient background and subtle shadow
 */
public class RegisterPanel extends JPanel {
    private final MainFrame parent;

    // Input fields
    private HintTextField usernameField;
    private HintTextField fullNameField;
    private HintTextField emailField;
    private HintTextField studentIdField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> roleComboBox;
    private JComboBox<Club> clubComboBox;

    private final ClubDAO clubDAO = new ClubDAO();

    // Theme
    private static final Color PRIMARY_COLOR = new Color(139, 0, 0); // deep red
    private static final Color ACCENT_COLOR = new Color(200, 0, 0);
    private static final Color BACKGROUND_LIGHT = new Color(250, 250, 253);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 26);

    // Email regex (reasonably permissive)
    private static final Pattern EMAIL_RE = Pattern.compile("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$");

    public RegisterPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_LIGHT);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        JPanel card = new RoundedGradientPanel();
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(26, 34, 26, 34));
        card.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 8, 10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;

        // Title
        JLabel title = new JLabel("Create Account", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(PRIMARY_COLOR);
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        card.add(title, c);

        // Club count placeholder while loading
        JLabel clubCountLabel = new JLabel("Available Clubs: loading...");
        clubCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        clubCountLabel.setForeground(PRIMARY_COLOR);
        c.gridy++;
        card.add(clubCountLabel, c);

        // Username
        c.gridwidth = 1;
        addLabel(card, "Username:", c, 1);
        usernameField = new HintTextField("e.g. seabata", 18);
        usernameField.setToolTipText("Choose a unique username");
        usernameField.setBorder(new RoundedBorder(10, new Color(200,200,200)));
        placeField(card, usernameField, c, 1);

        // Full name
        addLabel(card, "Full Name:", c, 2);
        fullNameField = new HintTextField("e.g. seabata sechaba", 18);
        fullNameField.setToolTipText("Your full name as it appears on university records");
        fullNameField.setBorder(new RoundedBorder(10, new Color(200,200,200)));
        placeField(card, fullNameField, c, 2);

        // Email
        addLabel(card, "Email:", c, 3);
        emailField = new HintTextField("seabata.sechaba@botho.ac.ls", 18);
        emailField.setToolTipText("Enter your student or personal email");
        emailField.setBorder(new RoundedBorder(10, new Color(200,200,200)));
        placeField(card, emailField, c, 3);

        // Student ID
        addLabel(card, "Student ID:", c, 4);
        studentIdField = new HintTextField("e.g. 2333779", 18);
        studentIdField.setToolTipText("Your Botho University student number");
        studentIdField.setBorder(new RoundedBorder(10, new Color(200,200,200)));
        placeField(card, studentIdField, c, 4);

        // Role
        addLabel(card, "Role:", c, 5);
        roleComboBox = new JComboBox<>(new String[]{"Member", "Leader"});
        roleComboBox.setBorder(new RoundedBorder(10, new Color(200,200,200)));
        roleComboBox.setToolTipText("Select your desired role in the club");
        c.gridx = 1;
        c.gridy = 5;
        card.add(roleComboBox, c);

        // Club selection
        addLabel(card, "Choose Club:", c, 6);
        clubComboBox = new JComboBox<>();
        clubComboBox.setRenderer(new ClubListRenderer());
        clubComboBox.setBorder(new RoundedBorder(10, new Color(200,200,200)));
        clubComboBox.setToolTipText("Pick which club you want to join");
        c.gridx = 1;
        c.gridy = 6;
        card.add(clubComboBox, c);

        // Password
        addLabel(card, "Password:", c, 7);
        passwordField = new JPasswordField(18);
        passwordField.setBorder(new RoundedBorder(10, new Color(200,200,200)));
        passwordField.setToolTipText("Use a strong password (8+ characters)");
        c.gridx = 1; c.gridy = 7;
        card.add(passwordField, c);

        // Confirm password
        addLabel(card, "Confirm Password:", c, 8);
        confirmPasswordField = new JPasswordField(18);
        confirmPasswordField.setBorder(new RoundedBorder(10, new Color(200,200,200)));
        confirmPasswordField.setToolTipText("Retype your password");
        c.gridx = 1; c.gridy = 8;
        card.add(confirmPasswordField, c);

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        buttons.setOpaque(false);

        JButton createBtn = styledButton("Create Account", Color.WHITE, PRIMARY_COLOR);
        JButton backBtn = styledButton("Back to Login", PRIMARY_COLOR, new Color(245, 245, 245));
        JButton homeBtn = styledButton("Home", PRIMARY_COLOR, new Color(245, 245, 245));

        createBtn.addActionListener(e -> onCreate(createBtn));
        backBtn.addActionListener(e -> parent.showCard("login"));
        homeBtn.addActionListener(e -> parent.showCard("home"));

        buttons.add(createBtn);
        buttons.add(backBtn);
        buttons.add(homeBtn);

        c.gridx = 0; c.gridy = 9; c.gridwidth = 2;
        card.add(buttons, c);

        centerWrapper.add(card);
        add(centerWrapper, BorderLayout.CENTER);

        // Load clubs asynchronously
        new ClubLoaderWorker(clubCountLabel, createBtn).execute();

        // Accessibility: label associations
        usernameField.getAccessibleContext().setAccessibleName("Username");
        fullNameField.getAccessibleContext().setAccessibleName("Full Name");
        emailField.getAccessibleContext().setAccessibleName("Email");
        studentIdField.getAccessibleContext().setAccessibleName("Student ID");
        passwordField.getAccessibleContext().setAccessibleName("Password");
        confirmPasswordField.getAccessibleContext().setAccessibleName("Confirm Password");
    }

    private void addLabel(JPanel card, String text, GridBagConstraints c, int row) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(LABEL_FONT);
        lbl.setForeground(Color.DARK_GRAY);
        c.gridx = 0;
        c.gridy = row;
        c.gridwidth = 1;
        card.add(lbl, c);
    }

    private void placeField(JPanel card, JComponent field, GridBagConstraints c, int row) {
        c.gridx = 1;
        c.gridy = row;
        card.add(field, c);
    }

    private JButton styledButton(String text, Color fg, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new RoundedBorder(12, bg.darker()));
        btn.setPreferredSize(new Dimension(150, 34));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if (bg.getRGB() != new Color(245,245,245).getRGB()) btn.setBackground(ACCENT_COLOR); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });
        return btn;
    }

    private void onCreate(JButton createBtn) {
        createBtn.setEnabled(false);

        String username = usernameField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String studentId = studentIdField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());
        String role = ((String) roleComboBox.getSelectedItem()).toLowerCase();
        Club club = (Club) clubComboBox.getSelectedItem();

        if (username.isEmpty() || fullName.isEmpty() || email.isEmpty() || studentId.isEmpty()
                || password.isEmpty() || confirm.isEmpty() || club == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Missing information", JOptionPane.ERROR_MESSAGE);
            createBtn.setEnabled(true);
            return;
        }

        if (!EMAIL_RE.matcher(email).matches()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Invalid email", JOptionPane.ERROR_MESSAGE);
            createBtn.setEnabled(true);
            return;
        }

        if (password.length() < 8) {
            JOptionPane.showMessageDialog(this, "Password must be at least 8 characters.", "Weak password", JOptionPane.ERROR_MESSAGE);
            createBtn.setEnabled(true);
            return;
        }

        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Mismatch", JOptionPane.ERROR_MESSAGE);
            createBtn.setEnabled(true);
            return;
        }

        SwingWorker<Boolean, Void> regWorker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                UserService us = new UserService();
                return us.register(username, password, fullName, email, studentId, role, club.getId());
            }

            @Override
            protected void done() {
                try {
                    boolean ok = get();
                    if (ok) {
                        JOptionPane.showMessageDialog(RegisterPanel.this, "Account created! Wait for admin approval.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        parent.showCard("login");
                    } else {
                        JOptionPane.showMessageDialog(RegisterPanel.this, "Failed to create account (username may already exist).", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(RegisterPanel.this, "An error occurred while creating account.", "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    createBtn.setEnabled(true);
                }
            }
        };
        regWorker.execute();
    }

    private class ClubLoaderWorker extends SwingWorker<List<Club>, Void> {
        private final JLabel clubCountLabel;
        private final JButton createButton;

        ClubLoaderWorker(JLabel clubCountLabel, JButton createButton) {
            this.clubCountLabel = clubCountLabel;
            this.createButton = createButton;
            createButton.setEnabled(false);
        }

        @Override
        protected List<Club> doInBackground() {
            return clubDAO.listAll();
        }

        @Override
        protected void done() {
            try {
                List<Club> clubs = get();
                if (clubs == null || clubs.isEmpty()) {
                    String[] demo = {"Soccer Club", "Basketball Club", "Debate Society", "Tech Innovators", "Drama Club", "Music Club", "Environmental Club", "Entrepreneurship Club"};
                    DefaultComboBoxModel<Club> model = new DefaultComboBoxModel<>();
                    for (int i = 0; i < demo.length; i++) {
                        model.addElement(new Club(1000 + i, demo[i], "General", demo[i] + " at Botho University", 1, false));
                    }
                    clubComboBox.setModel(model);
                    clubCountLabel.setText("Available Clubs: " + demo.length + " (demo)");
                } else {
                    DefaultComboBoxModel<Club> model = new DefaultComboBoxModel<>();
                    for (Club c : clubs) model.addElement(c);
                    clubComboBox.setModel(model);
                    clubCountLabel.setText("Available Clubs: " + clubs.size());
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                clubCountLabel.setText("Available Clubs: (failed to load)");
            } finally {
                createButton.setEnabled(true);
            }
        }
    }

    private static class RoundedGradientPanel extends JPanel {
        RoundedGradientPanel() { setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            int shadow = 8;
            int arc = 20;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            g2.setColor(new Color(0, 0, 0, 40));
            g2.fillRoundRect(shadow / 2, shadow / 2, w - shadow, h - shadow, arc, arc);
            GradientPaint gp = new GradientPaint(0, 0, Color.WHITE, 0, h, new Color(236, 236, 240));
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, w - shadow, h - shadow, arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;
        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius / 2, radius, radius / 2, radius);
        }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }

    private static class HintTextField extends JTextField {
        private final String hint;
        private final Color hintColor = new Color(150, 150, 150);
        HintTextField(String hint, int columns) {
            super(columns);
            this.hint = hint;
            setOpaque(false);
            setForeground(Color.BLACK);
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) { repaint(); }
                @Override
                public void focusLost(FocusEvent e) { repaint(); }
            });
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty() && !isFocusOwner()) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(hintColor);
                Insets in = getInsets();
                FontMetrics fm = g2.getFontMetrics();
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(hint, in.left + 6, textY);
                g2.dispose();
            }
        }
    }

    private static class ClubListRenderer implements ListCellRenderer<Club> {
        private final JPanel renderer = new JPanel(new BorderLayout(6, 3));
        private final JLabel nameLabel = new JLabel();
        private final JLabel infoLabel = new JLabel();
        ClubListRenderer() {
            renderer.setBorder(new EmptyBorder(6, 8, 6, 8));
            renderer.setOpaque(true);
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            infoLabel.setForeground(Color.DARK_GRAY);
            renderer.add(nameLabel, BorderLayout.NORTH);
            renderer.add(infoLabel, BorderLayout.SOUTH);
        }
        @Override
        public Component getListCellRendererComponent(JList<? extends Club> list, Club value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            if (value == null) {
                nameLabel.setText("");
                infoLabel.setText("");
            } else {
                nameLabel.setText(value.getName());
                String cat = value.getCategory() != null ? value.getCategory() : "General";
                String desc = value.getDescription() != null ? " — " + trim(value.getDescription(), 80) : "";
                infoLabel.setText("(" + cat + ")" + desc);
            }
            if (isSelected) {
                renderer.setBackground(new Color(245, 220, 220));
            } else {
                renderer.setBackground(Color.WHITE);
            }
            return renderer;
        }
        private String trim(String s, int max) {
            if (s.length() <= max) return s;
            return s.substring(0, max - 3) + "...";
        }
    }
}