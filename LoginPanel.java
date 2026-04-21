package clubconnect.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import clubconnect.service.UserService;
import clubconnect.models.User;

/**
 * Custom border class for rounded corners.
 */
class RoundedBorder implements Border {
    private int radius;

    public RoundedBorder(int radius) { this.radius = radius; }

    public Insets getBorderInsets(Component c) { return new Insets(this.radius + 1, this.radius + 1, this.radius + 1, this.radius + 1); }
    public boolean isBorderOpaque() { return false; }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        g.setColor(c.getBackground());
        g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
    }
}

/**
 * Modern login form for ClubConnect
 */
public class LoginPanel extends JPanel {
    private MainFrame parent;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private UserService userService = new UserService();

    private static final Color PRIMARY_COLOR = new Color(175, 0, 0);
    private static final Color HOVER_COLOR = new Color(220, 50, 50);
    private static final Color BACKGROUND_COLOR = new Color(248, 248, 248);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font LINK_FONT = new Font("Segoe UI", Font.ITALIC, 12);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);

    public LoginPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(BACKGROUND_COLOR);

        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRoundRect(5, 5, getWidth() - 5, getHeight() - 5, 20, 20);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 20, 20);
            }
        };
        card.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        card.setOpaque(false);

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.CENTER;

        // Title
        JLabel title = new JLabel("ClubConnect", JLabel.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(PRIMARY_COLOR);
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        card.add(title, g);

        // Subtitle
        JLabel subtitle = new JLabel("Botho University", JLabel.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        subtitle.setForeground(Color.GRAY);
        g.gridy = 1;
        card.add(subtitle, g);

        // Username
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(LABEL_FONT);
        g.gridy = 2; g.gridwidth = 1; g.gridx = 0;
        card.add(userLabel, g);

        usernameField = new JTextField(18);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        g.gridx = 1;
        card.add(usernameField, g);

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(LABEL_FONT);
        g.gridx = 0; g.gridy = 3;
        card.add(passLabel, g);

        passwordField = new JPasswordField(18);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        g.gridx = 1;
        card.add(passwordField, g);

        // Login button
        JButton loginBtn = createStyledButton("Login");
        loginBtn.addActionListener(e -> handleLogin());
        g.gridy = 4; g.gridx = 0; g.gridwidth = 2;
        card.add(loginBtn, g);

        // Bottom links
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        bottomPanel.setBackground(Color.WHITE);

        JLabel createAccountLink = new JLabel("Don't have an account? Create one");
        createAccountLink.setForeground(PRIMARY_COLOR);
        createAccountLink.setFont(LINK_FONT);
        createAccountLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        createAccountLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                parent.showCard("register");
            }
        });
        bottomPanel.add(createAccountLink);

        JLabel forgotLink = new JLabel("Forgot Password?");
        forgotLink.setForeground(Color.BLUE);
        forgotLink.setFont(LINK_FONT);
        forgotLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                handleForgotPassword();
            }
        });
        bottomPanel.add(forgotLink);

        g.gridy = 5; g.gridwidth = 2;
        card.add(bottomPanel, g);

        // Back to Home
        JButton backHomeBtn = createStyledButton("Back to Home");
        backHomeBtn.addActionListener(e -> parent.showCard("home"));
        g.gridy = 6; g.gridwidth = 2;
        card.add(backHomeBtn, g);

        centerPanel.add(card);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(PRIMARY_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new RoundedBorder(10));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(HOVER_COLOR); }
            public void mouseExited(MouseEvent e) { btn.setBackground(PRIMARY_COLOR); }
        });

        return btn;
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all credentials");
            return;
        }

        User user = userService.authenticate(username, password);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid credentials or account not approved");
            return;
        }

        parent.setCurrentUser(user);

        switch (user.getRole().toLowerCase()) {
            case "admin" -> parent.showCard("admin");
            case "leader" -> parent.showCard("leader");
            default -> parent.showCard("member");
        }
    }

    private void handleForgotPassword() {
        String input = JOptionPane.showInputDialog(this, "Enter your email or username to reset password:");
        if (input != null && !input.trim().isEmpty()) {
            boolean success = userService.requestPasswordReset(input.trim());
            JOptionPane.showMessageDialog(this,
                    success ? "A reset link has been sent to your email." :
                            "Failed to send reset email. Check your email/username.");
        }
    }
}
