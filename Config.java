package clubconnect.util;

/**
 * Configuration settings for ClubConnect.
 * IMPORTANT: Update STUDENT_NAME and STUDENT_NUMBER to match your submission requirement.
 * Place the MySQL connector driver (mysql-connector-java-x.x.xx.jar) into ./lib and add it to your project's classpath.
 */
public class Config {
    // Set these to your student details (used for DB name requirement)
    public static final String STUDENT_NAME = "StudentName"; // e.g.
    public static final String STUDENT_NUMBER = "12345678"; //

    // Database name will be derived from STUDENT_NAME + STUDENT_NUMBER
    public static final String DB_NAME = STUDENT_NAME + "_" + STUDENT_NUMBER;

    // MySQL connection to server (used to create DB if missing)
    public static final String MYSQL_HOST = "localhost";
    public static final int MYSQL_PORT = 3306;
    public static final String MYSQL_SERVER_URL = "jdbc:mysql://" + MYSQL_HOST + ":" + MYSQL_PORT + "/";
    public static final String MYSQL_USER = "root";
    public static final String MYSQL_PASSWORD = "";

    // JDBC driver class name
    public static final String MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver";

    // Backup directory
    public static final String BACKUP_DIR = "backups";
}
