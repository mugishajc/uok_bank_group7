package storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Fixed absolute path in user home — works regardless of where JAR is launched
    private static final String DB_DIR =
        System.getProperty("user.home") + java.io.File.separator + ".uokbank";
    private static final String URL =
        "jdbc:sqlite:" + DB_DIR + java.io.File.separator + "uok_bank.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found", e);
        }
    }

    public static String getDbDir()  { return DB_DIR; }
    public static Connection get()   throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
