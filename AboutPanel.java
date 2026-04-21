package clubconnect.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class AboutPanel extends JPanel {
    private MainFrame parent;

    public AboutPanel(MainFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout());
        setBackground(new Color(247,247,249));

        add(createHeader(), BorderLayout.NORTH);
        add(createHeroSection(), BorderLayout.NORTH);
        add(createBody(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(128, 0, 0));
        header.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel title = new JLabel("ABOUT CLUBCONNECT");
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 19));
        title.setForeground(Color.WHITE);

        JButton closeBtn = new JButton("✖ Close");
        closeBtn.setFont(new Font("Segoe UI Semibold", Font.BOLD, 14));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setBackground(new Color(128, 0, 0));
        closeBtn.setFocusPainted(false);
        closeBtn.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> { if (parent != null) parent.showCard("home"); });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        right.add(closeBtn);

        header.add(title, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JPanel createHeroSection() {
        JPanel hero = new JPanel(new BorderLayout(12, 0));
        hero.setBackground(new Color(247,247,249));
        hero.setBorder(new EmptyBorder(26, 42, 14, 42));

        JLabel photo = new JLabel("🦁", SwingConstants.CENTER);
        photo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 82));
        photo.setBorder(null);

        JPanel heroTextPanel = new JPanel();
        heroTextPanel.setLayout(new BoxLayout(heroTextPanel, BoxLayout.Y_AXIS));
        heroTextPanel.setBackground(new Color(247,247,249));

        JLabel brand = new JLabel("Seabata Sechaba", SwingConstants.LEFT);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 30));
        brand.setForeground(new Color(120,0,0));
        JLabel brand2 = new JLabel("ClubConnect • Botho University", SwingConstants.LEFT);
        brand2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brand2.setForeground(new Color(180,20,20));
        JLabel mission = new JLabel("<html><i>Empowering every student at Botho to discover, join, and lead clubs.</i></html>", SwingConstants.LEFT);
        mission.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        mission.setForeground(new Color(60,60,60));

        heroTextPanel.add(brand);
        heroTextPanel.add(brand2);
        heroTextPanel.add(mission);

        hero.add(photo, BorderLayout.WEST);
        hero.add(heroTextPanel, BorderLayout.CENTER);

        return hero;
    }

    private JScrollPane createBody() {
        JPanel body = new JPanel();
        body.setBackground(new Color(247,247,249));
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(18,36,18,36));

        // Use modern cards style, left-aligned with icons
        body.add(sectionCard("⭐ Our Story",
            "Founded from my passion for student leadership and digital innovation. "
          + "At Botho, I saw how clubs unite people—but software could make it better! Thus ClubConnect was born, a platform to connect, share, and celebrate club life."));

        body.add(sectionCard("💡 Mission Statement",
            "<b>To build a collaborative, easy-to-use digital hub for university student clubs and activities, empowering connection, creativity, and leadership.</b>"));

        body.add(sectionCard("🎯 Values",
            "<ul><li>Inclusivity</li><li>Collaboration</li><li>Empowerment</li><li>Community Spirit</li></ul>"));

        body.add(sectionCard("🛠️ Skills & Services",
            "<b>Skills:</b> Java • SQL • UX Design • Event Strategy • Branding"
          + "<br><b>Services:</b> Club registration & approval • Event scheduling • Notifications • Feedback & engagement"));

        body.add(sectionCard("🏆 Achievements",
            "<ul><li>ClubConnect adopted by Botho Student Council</li>"
          + "<li>Led Robotics Club to Inter-University championship</li><li>Art Expo ‘25 platinum sponsor</li></ul>"));

        body.add(sectionCard("📈 Milestones",
            "<ul><li>2023 – ClubConnect prototype launched</li>"
          + "<li>2024 – First sponsor partnership</li><li>2025 – 50+ clubs onboarded</li></ul>"));

        body.add(sectionCard("🎉 Fun Facts",
            "• Code while listening to Lesotho jazz<br>• Can solve a Rubik’s cube in 70 seconds"));

        body.add(sectionCard("💬 Testimonials",
            "“ClubConnect made our club communication 10x easier!” – Sechaba Sechaba<br>"
          + "“Joining a club was never this quick.” – Sechaba Sechaba"));

        body.add(sectionCard("🌅 Vision",
            "<b>By 2030, every university in Southern Africa will have a student-run digital club hub powered by ClubConnect.</b>"));

        body.add(sectionCard("🖥️ Behind the Scenes",
            "Built in Java, hosted with MySQL; design refined with feedback from real club leaders.<br>"
          + "Workspace: Laptop, university library, and the occasional coffee shop."));

        body.add(sectionCard("👉 Call to Action",
            "<b>Contact or follow us – Join the club revolution!</b>"));

        body.add(sectionCard("🌐 Social & Media",
            "Follow <b>@clubconnectls</b> on Instagram, X, Facebook<br>Featured in Lesotho Student News, 2025"));

        body.add(sectionCard("📝 Blog / Portfolio",
            "See case studies and tips at <b>www.clubconnectls.com/blog</b>"));

        body.add(sectionCard("📞 Contact",
            "Email: <a href='mailto:sechaba@clubconnectls.com'>sechaba@clubconnectls.com</a><br>"
          + "WhatsApp: <b>+26656171110</b>"));

        body.add(sectionCard("❤️ Beliefs",
            "<b>We stand for enabling every student voice, creativity, and leadership.</b>"));

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(30);
        return scroll;
    }

    private JPanel sectionCard(String title, String htmlBody) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(new Color(255,255,255));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(240,240,240), 1, true),
            new EmptyBorder(16,18,16,18)
        ));

        JLabel lblTitle = new JLabel(title, SwingConstants.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(128,0,0));
        lblTitle.setBorder(new EmptyBorder(0,0,6,0));
        JLabel lblBody = new JLabel("<html>" + htmlBody + "</html>");
        lblBody.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblBody.setForeground(new Color(30,30,30));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblBody, BorderLayout.CENTER);
        return card;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(247,247,249));
        footer.setBorder(new EmptyBorder(12, 0, 4, 0));
        JLabel footerLabel = new JLabel(
                "© 2025 ClubConnect | Modern About Page – Designed by Seabata Sechaba | Botho University Lesotho",
                SwingConstants.CENTER);
        footerLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        footerLabel.setForeground(new Color(160,160,160));
        footer.add(footerLabel, BorderLayout.CENTER);
        return footer;
    }
}