package dao;

import model.Transaction;
import storage.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void record(String from, String to, String type, double amount, String note) {
        String sql = "INSERT INTO transactions " +
                     "(sender_phone,receiver_phone,type,amount,timestamp,note) VALUES (?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, from);  ps.setString(2, to);
            ps.setString(3, type);  ps.setDouble(4, amount);
            ps.setString(5, LocalDateTime.now().format(FMT));
            ps.setString(6, note);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Transaction> getByPhone(String phone) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions " +
                     "WHERE sender_phone=? OR receiver_phone=? ORDER BY timestamp DESC";
        try (Connection c = DatabaseConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, phone); ps.setString(2, phone);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Transaction> getAll() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY timestamp DESC";
        try (Connection c = DatabaseConnection.get();
             ResultSet rs = c.createStatement().executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Transaction map(ResultSet rs) throws SQLException {
        return new Transaction(
            rs.getInt("id"),
            rs.getString("sender_phone"),
            rs.getString("receiver_phone"),
            rs.getString("type"),
            rs.getDouble("amount"),
            rs.getString("timestamp"),
            rs.getString("note")
        );
    }
}
