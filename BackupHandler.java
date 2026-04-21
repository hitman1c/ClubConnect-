package clubconnect.db;

import clubconnect.util.Config;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Exports simple SQL INSERT representation of the database to a file.
 * This is portable and acceptable for assignment backups.
 */
public class BackupHandler {
    public static File exportDatabase() throws Exception {
        String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File dir = new File(Config.BACKUP_DIR);
        if (!dir.exists()) dir.mkdirs();
        File out = new File(dir, "backup_" + Config.DB_NAME + "_" + ts + ".sql");
        try (PrintWriter pw = new PrintWriter(new FileWriter(out))) {
            // connect to DB
            try (Connection c = DriverManager.getConnection(Config.MYSQL_SERVER_URL + Config.DB_NAME, Config.MYSQL_USER, Config.MYSQL_PASSWORD)) {
                DatabaseMetaData meta = c.getMetaData();
                ResultSet tables = meta.getTables(Config.DB_NAME, null, "%", new String[]{"TABLE"});
                while (tables.next()) {
                    String table = tables.getString("TABLE_NAME");
                    pw.println("-- Table: " + table);
                    Statement s = c.createStatement();
                    ResultSet rs = s.executeQuery("SELECT * FROM " + table);
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int cols = rsmd.getColumnCount();
                    while (rs.next()) {
                        StringBuilder colsSb = new StringBuilder();
                        StringBuilder valsSb = new StringBuilder();
                        for (int i = 1; i <= cols; i++) {
                            if (i > 1) { colsSb.append(","); valsSb.append(","); }
                            colsSb.append("`").append(rsmd.getColumnName(i)).append("`");
                            Object v = rs.getObject(i);
                            if (v == null) valsSb.append("NULL");
                            else valsSb.append("'").append(String.valueOf(v).replace("'","''")).append("'");
                        }
                        pw.println("INSERT INTO `" + table + "` (" + colsSb + ") VALUES (" + valsSb + ");");
                    }
                    rs.close(); s.close();
                    pw.println();
                }
                tables.close();
            }
        }
        return out;
    }
}
