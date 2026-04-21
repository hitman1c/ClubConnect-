package clubconnect.util;

import clubconnect.db.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Seeds sample admin user and sample club/resources.
 */
public class SampleData {
    public static void seed() {
        try {
            DatabaseManager.getInstance(); // ensures DB exists
            try (Connection c = java.sql.DriverManager.getConnection(Config.MYSQL_SERVER_URL + Config.DB_NAME, Config.MYSQL_USER, Config.MYSQL_PASSWORD)) {
                PreparedStatement ps = c.prepareStatement("INSERT IGNORE INTO users(username,password,full_name,role,email,student_id) VALUES(?,?,?,?,?,?)");
                ps.setString(1,"admin");
                ps.setString(2,clubconnect.util.HashUtil.sha256("admin123"));
                ps.setString(3,"Administrator");
                ps.setString(4,"admin");
                ps.setString(5,"admin@university.edu");
                ps.setString(6,"ADMIN000");
                ps.executeUpdate();
                ps.close();
                // resources
                ps = c.prepareStatement("INSERT IGNORE INTO resources(id,name,capacity,description,available) VALUES(1,?,?,?,1)");
                ps.setString(1,"Main Hall");
                ps.setInt(2,200);
                ps.setString(3,"Large venue");
                ps.executeUpdate();
                ps.close();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}
