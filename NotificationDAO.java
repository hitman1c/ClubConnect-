package clubconnect.dao;

import clubconnect.models.Notification;
import clubconnect.util.Config;

import java.sql.*;
import java.util.List;

public class NotificationDAO {
    public int getCountForUser(int userId) {
        try (Connection c = DriverManager.getConnection(
                Config.MYSQL_SERVER_URL + Config.DB_NAME,
                Config.MYSQL_USER,
                Config.MYSQL_PASSWORD)) {
            PreparedStatement ps = c.prepareStatement(
                    "SELECT COUNT(*) FROM notifications WHERE club_id IN (SELECT club_id FROM memberships WHERE user_id=?)");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
            rs.close();
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public List<Notification> getAll() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}