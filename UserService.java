package clubconnect.service;

import clubconnect.dao.UserDAO;
import clubconnect.dao.PasswordResetDAO;
import clubconnect.dao.MembershipDAO;
import clubconnect.models.User;
import clubconnect.util.HashUtil;

import java.util.List;
import java.util.Properties;

import jakarta.mail.Session;
import jakarta.mail.Message;
import jakarta.mail.Transport;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.MessagingException;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;

public class UserService {

    private UserDAO dao = new UserDAO();
    private MembershipDAO membershipDAO = new MembershipDAO();
    private PasswordResetDAO resetDAO = new PasswordResetDAO();

    private static final String DEFAULT_DOMAIN = "https://7dffb79f50dc.ngrok-free.app";

    // --- Authentication ---
    public User authenticate(String username, String password) {
        User u = dao.findByUsername(username);
        if (u == null || !u.isApproved()) return null;
        if (u.getPassword().equals(HashUtil.sha256(password))) return u;
        return null;
    }

    // --- Registration ---
    public boolean register(String username, String password, String fullName, String email, String studentId, String role, int clubId) {
        User u = new User(username, HashUtil.sha256(password), fullName, role, email, studentId);
        u.setApproved(false);
        boolean userOk = dao.create(u);
        if (!userOk) return false;

        User created = dao.findByUsername(username);
        if (created == null) return false;

        return membershipDAO.createMembership(created.getId(), clubId, role, "pending");
    }

    public boolean register(String username, String password, String fullName, String email, String studentId) {
        return register(username, password, fullName, email, studentId, "member", -1);
    }

    // --- Admin Management ---
    public List<User> getAllUsers() { return dao.findAll(); }
    public boolean deleteUser(int id) { return dao.deleteById(id); }
    public boolean approveUser(int id) { return dao.updateApproval(id, true); }
    public boolean unapproveUser(int id) { return dao.updateApproval(id, false); }
    public boolean updateUserRole(int id, String newRole) { return dao.updateRole(id, newRole); }

    // --- Password Reset Request with default domain ---
    public boolean requestPasswordReset(String emailOrUsername) {
        return requestPasswordReset(emailOrUsername, DEFAULT_DOMAIN);
    }

    // --- Password Reset Request with custom domain ---
    public boolean requestPasswordReset(String emailOrUsername, String publicDomainUrl) {
        User u = dao.findByUsername(emailOrUsername);
        if (u == null) u = dao.findByEmail(emailOrUsername);
        if (u == null) return false;

        String token = resetDAO.createResetToken(u.getId());
        if (token == null) return false;

        String resetLink = publicDomainUrl + "/reset?token=" + token;

        String subject = "🔐 Sechaba ConnectClub Password Reset";

        String htmlMessage = "<!DOCTYPE html>" +
                "<html>" +
                "<body style='font-family:Arial,sans-serif; background-color:#f9f9f9; padding:20px;'>" +
                "<div style='max-width:600px; margin:auto; background-color:#ffffff; padding:30px; border-radius:10px; box-shadow:0 4px 8px rgba(0,0,0,0.1);'>" +
                "<h2 style='color:#2E86C1; text-align:center;'>Sechaba ConnectClub Password Reset</h2>" +
                "<p>Hello <strong>" + u.getFullName() + "</strong>,</p>" +
                "<p>We received a request to reset your password. Click the button below to securely reset it from any device:</p>" +
                "<p style='text-align:center;'><a href='" + resetLink + "' style='padding:12px 25px; background-color:#2E86C1; color:white; text-decoration:none; font-weight:bold; border-radius:8px;'>Reset Password</a></p>" +
                "<p>If you did not request this password reset, you can safely ignore this email.</p>" +
                "<br><p style='color:#555;'>Regards,<br>Sechaba ConnectClub Team</p>" +
                "</div>" +
                "</body>" +
                "</html>";

        return sendEmail(u.getEmail(), subject, htmlMessage, true);
    }

    // --- Execute Password Reset ---
    public boolean resetPassword(String token, String newPassword) {
        int userId = resetDAO.getUserIdByToken(token);
        if (userId == -1) return false;

        User u = dao.findById(userId);
        if (u == null) return false;

        u.setPassword(HashUtil.sha256(newPassword));
        boolean updated = dao.update(u);

        if (updated) resetDAO.markTokenUsed(token);
        return updated;
    }

    // --- Send Email via Gmail ---
    private boolean sendEmail(String to, String subject, String messageBody, boolean isHtml) {
        try {
            final String host = "smtp.gmail.com";
            final String from = "sechabajeremiah@gmail.com";
            final String password = "uacqcevxgpvobdpm"; // Gmail App Password

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            props.put("mail.smtp.timeout", "10000");
            props.put("mail.smtp.connectiontimeout", "10000");
            props.put("mail.smtp.writetimeout", "10000");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(from, password);
                }
            });

            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from, "Sechaba ConnectClub"));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject(subject, "UTF-8");

            msg.setHeader("X-Priority", "1");
            msg.setHeader("Importance", "High");
            msg.setHeader("Content-Transfer-Encoding", "8bit");
            msg.setReplyTo(new InternetAddress[]{new InternetAddress(from)});

            MimeMultipart multipart = new MimeMultipart("alternative");
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText("Please enable HTML to view this message.", "UTF-8");

            MimeBodyPart htmlPart = new MimeBodyPart();
            if (isHtml) {
                htmlPart.setContent(messageBody, "text/html; charset=UTF-8");
            } else {
                htmlPart.setText(messageBody, "UTF-8");
            }

            multipart.addBodyPart(textPart);
            multipart.addBodyPart(htmlPart);
            msg.setContent(multipart);

            Transport.send(msg);
            return true;

        } catch (MessagingException e) {
            System.err.println("Email send error: " + e.getMessage());
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
