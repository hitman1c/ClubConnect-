package clubconnect.db;

import clubconnect.util.Config;
import java.sql.*;

public class DatabaseManager {
    private static DatabaseManager instance;

    private DatabaseManager() throws Exception {
        ensureDatabaseExists();
        ConnectionPool.getInstance();
        ensureTables();
    }

    public static synchronized DatabaseManager getInstance() throws Exception {
        if (instance == null) instance = new DatabaseManager();
        return instance;
    }

    private void ensureDatabaseExists() throws Exception {
        try {
            Class.forName(Config.MYSQL_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new Exception("MySQL Driver not found. Place mysql-connector JAR into ./lib and add to classpath.", e);
        }
        try (Connection c = DriverManager.getConnection(Config.MYSQL_SERVER_URL, Config.MYSQL_USER, Config.MYSQL_PASSWORD)) {
            Statement s = c.createStatement();
            String dbName = Config.DB_NAME.replaceAll("[^a-zA-Z0-9_]", "Studentname_12345678");
            s.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + dbName + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            s.close();
        }
    }

    private void ensureTables() throws Exception {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(Config.MYSQL_SERVER_URL + Config.DB_NAME, Config.MYSQL_USER, Config.MYSQL_PASSWORD);
            Statement s = conn.createStatement();

            // users
            s.executeUpdate("CREATE TABLE IF NOT EXISTS users ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "username VARCHAR(100) UNIQUE, "
                    + "password VARCHAR(255), "
                    + "full_name VARCHAR(200), "
                    + "role VARCHAR(50), "
                    + "email VARCHAR(200), "
                    + "student_id VARCHAR(100)"
                    + ")");

            // clubs
            s.executeUpdate("CREATE TABLE IF NOT EXISTS clubs ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "name VARCHAR(200), "
                    + "category VARCHAR(100), "
                    + "description TEXT, "
                    + "created_by INT, "
                    + "archived TINYINT DEFAULT 0"
                    + ")");

            // memberships
            s.executeUpdate("CREATE TABLE IF NOT EXISTS memberships ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "user_id INT, "
                    + "club_id INT, "
                    + "role VARCHAR(50), "
                    + "status VARCHAR(50), "
                    + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, "
                    + "FOREIGN KEY (club_id) REFERENCES clubs(id) ON DELETE CASCADE"
                    + ")");

            // events
            s.executeUpdate("CREATE TABLE IF NOT EXISTS events ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "club_id INT, "
                    + "title VARCHAR(255), "
                    + "date_time DATETIME, "
                    + "venue VARCHAR(200), "
                    + "capacity INT, "
                    + "details TEXT, "
                    + "budget_requested TINYINT DEFAULT 0, "
                    + "budget_status VARCHAR(50), "
                    + "FOREIGN KEY (club_id) REFERENCES clubs(id) ON DELETE CASCADE"
                    + ")");

            // budgets
            s.executeUpdate("CREATE TABLE IF NOT EXISTS budgets ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "event_id INT, "
                    + "requested_by INT, "
                    + "amount DOUBLE, "
                    + "status VARCHAR(50), "
                    + "notes TEXT, "
                    + "FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE, "
                    + "FOREIGN KEY (requested_by) REFERENCES users(id) ON DELETE CASCADE"
                    + ")");

            // resources
            s.executeUpdate("CREATE TABLE IF NOT EXISTS resources ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "name VARCHAR(200), "
                    + "capacity INT, "
                    + "description TEXT, "
                    + "available TINYINT DEFAULT 1"
                    + ")");

            // attendance
            s.executeUpdate("CREATE TABLE IF NOT EXISTS attendance ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "event_id INT, "
                    + "user_id INT, "
                    + "present TINYINT, "
                    + "FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE, "
                    + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
                    + ")");

            // discussions
            s.executeUpdate("CREATE TABLE IF NOT EXISTS discussions ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "club_id INT, "
                    + "user_id INT, "
                    + "content TEXT, "
                    + "created_at DATETIME, "
                    + "FOREIGN KEY (club_id) REFERENCES clubs(id) ON DELETE CASCADE, "
                    + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
                    + ")");

            // notifications
            s.executeUpdate("CREATE TABLE IF NOT EXISTS notifications ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "club_id INT, "
                    + "title VARCHAR(255), "
                    + "message TEXT, "
                    + "created_at DATETIME, "
                    + "sent TINYINT DEFAULT 0, "
                    + "FOREIGN KEY (club_id) REFERENCES clubs(id) ON DELETE CASCADE"
                    + ")");

            // feedback
            s.executeUpdate("CREATE TABLE IF NOT EXISTS feedback ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "club_id INT, "
                    + "user_id INT, "
                    + "message TEXT, "
                    + "created_at DATETIME, "
                    + "FOREIGN KEY (club_id) REFERENCES clubs(id) ON DELETE CASCADE, "
                    + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
                    + ")");

            // comments (for event comments/discussion)
            s.executeUpdate("CREATE TABLE IF NOT EXISTS comments ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "event_id INT NOT NULL, "
                    + "user_id INT NOT NULL, "
                    + "message TEXT NOT NULL, "
                    + "created_at DATETIME NOT NULL, "
                    + "FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE, "
                    + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
                    + ")");

            // password resets for forgot password
            s.executeUpdate("CREATE TABLE IF NOT EXISTS password_resets ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "user_id INT NOT NULL, "
                    + "token VARCHAR(128) NOT NULL, "
                    + "expires_at DATETIME NOT NULL, "
                    + "used TINYINT DEFAULT 0, "
                    + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
                    + ")");

            s.close();
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
    }

    public void close() {
        try { ConnectionPool.getInstance().closeAll(); } catch (Exception ignored) {}
    }
}