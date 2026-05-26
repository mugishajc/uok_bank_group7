package dao;

import model.Loan;
import storage.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LoanDAO {
    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public boolean request(String phone, double amount) {
        String sql = "INSERT INTO loans (phone,amount,requested_at) VALUES (?,?,?)";
        try (Connection c = DatabaseConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, phone); ps.setDouble(2, amount);
            ps.setString(3, LocalDateTime.now().format(FMT));
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE loans SET status=? WHERE id=?";
        try (Connection c = DatabaseConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status); ps.setInt(2, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Loan> getByPhone(String phone) {
        List<Loan> list = new ArrayList<>();
        String sql = "SELECT * FROM loans WHERE phone=? ORDER BY requested_at DESC";
        try (Connection c = DatabaseConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Loan> getAll() {
        List<Loan> list = new ArrayList<>();
        String sql = "SELECT * FROM loans ORDER BY requested_at DESC";
        try (Connection c = DatabaseConnection.get();
             ResultSet rs = c.createStatement().executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Loan map(ResultSet rs) throws SQLException {
        return new Loan(rs.getInt("id"), rs.getString("phone"),
            rs.getDouble("amount"), rs.getString("status"), rs.getString("requested_at"));
    }
}
