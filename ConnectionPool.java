package clubconnect.db;

import clubconnect.util.Config;
import java.sql.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Very simple Connection Pool. For production, prefer HikariCP or similar.
 */
public class ConnectionPool {
    private static ConnectionPool instance;
    private BlockingQueue<Connection> pool;
    private int size = 8;

    private ConnectionPool() throws SQLException, ClassNotFoundException {
        Class.forName(Config.MYSQL_DRIVER);
        pool = new ArrayBlockingQueue<>(size);
        for (int i = 0; i < size; i++) {
            Connection c = DriverManager.getConnection(Config.MYSQL_SERVER_URL + Config.DB_NAME, Config.MYSQL_USER, Config.MYSQL_PASSWORD);
            pool.offer(c);
        }
    }

    public static synchronized ConnectionPool getInstance() throws SQLException, ClassNotFoundException {
        if (instance == null) instance = new ConnectionPool();
        return instance;
    }

    public Connection getConnection() throws InterruptedException {
        return pool.take();
    }

    public void release(Connection c) {
        if (c == null) return;
        pool.offer(c);
    }

    public void closeAll() {
        for (Connection c : pool) {
            try { c.close(); } catch (Exception ignored) {}
        }
    }
}
