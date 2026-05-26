package storage;

import java.io.File;
import java.sql.*;

public class DatabaseInitializer {

    public static void initialize() {
        new File("data").mkdirs();
        createTables();
        seedAdmin();
    }

    private static void createTables() {
        try (Connection c = DatabaseConnection.get(); Statement s = c.createStatement()) {
            s.execute(
                "CREATE TABLE IF NOT EXISTS accounts (" +
                "  id           INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  phone        TEXT UNIQUE NOT NULL," +
                "  full_name    TEXT NOT NULL," +
                "  pin          TEXT NOT NULL," +
                "  account_type TEXT NOT NULL DEFAULT 'MOMO'," +
                "  balance      REAL NOT NULL DEFAULT 0.0," +
                "  role         TEXT NOT NULL DEFAULT 'USER'," +
                "  is_frozen    INTEGER NOT NULL DEFAULT 0" +
                ")");
            s.execute(
                "CREATE TABLE IF NOT EXISTS transactions (" +
                "  id             INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  sender_phone   TEXT," +
                "  receiver_phone TEXT," +
                "  type           TEXT NOT NULL," +
                "  amount         REAL NOT NULL," +
                "  timestamp      TEXT NOT NULL," +
                "  note           TEXT" +
                ")");
            s.execute(
                "CREATE TABLE IF NOT EXISTS loans (" +
                "  id           INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  phone        TEXT NOT NULL," +
                "  amount       REAL NOT NULL," +
                "  status       TEXT NOT NULL DEFAULT 'PENDING'," +
                "  requested_at TEXT NOT NULL" +
                ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void seedAdmin() {
        try (Connection c = DatabaseConnection.get()) {
            ResultSet rs = c.createStatement()
                .executeQuery("SELECT COUNT(*) FROM accounts WHERE phone='0700000000'");
            if (rs.next() && rs.getInt(1) == 0) {
                PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO accounts (phone,full_name,pin,account_type,balance,role)" +
                    " VALUES (?,?,?,?,?,?)");
                ps.setString(1, "0700000000");
                ps.setString(2, "System Admin");
                ps.setString(3, "00000");
                ps.setString(4, "CURRENT");
                ps.setDouble(5, 50_000_000.0);
                ps.setString(6, "ADMIN");
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
