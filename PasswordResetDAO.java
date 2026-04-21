package clubconnect.dao;

import clubconnect.util.Config;
import java.sql.*;
import java.util.UUID;

public class PasswordResetDAO {

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER,
                Config.MYSQL_PASSWORD
        );
    }

    // Create a password reset token for a user, valid for 1 hour
    public String createResetToken(int userId) {
        String token = UUID.randomUUID().toString();
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO password_resets(user_id, token, expires_at, used) VALUES (?, ?, DATE_ADD(NOW(), INTERVAL 1 HOUR), 0)"
            );
            ps.setInt(1, userId);
            ps.setString(2, token);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return token;
    }

    // Get userId by token if token is valid and not used
    public int getUserIdByToken(String token) {
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                    "SELECT user_id FROM password_resets WHERE token=? AND expires_at > NOW() AND used=0"
            );
            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("user_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Mark token as used after password reset
    public void markTokenUsed(String token) {
        try (Connection c = getConnection()) {
            PreparedStatement ps = c.prepareStatement(
                    "UPDATE password_resets SET used=1 WHERE token=?"
            );
            ps.setString(1, token);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
