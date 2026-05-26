package dao;

import model.Account;
import storage.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    public boolean create(String phone, String name, String pin, String type) {
        String sql = "INSERT INTO accounts (phone,full_name,pin,account_type) VALUES (?,?,?,?)";
        try (Connection c = DatabaseConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, phone); ps.setString(2, name);
            ps.setString(3, pin);   ps.setString(4, type);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public Account findByPhone(String phone) {
        String sql = "SELECT * FROM accounts WHERE phone=?";
        try (Connection c = DatabaseConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateBalance(String phone, double balance) {
        String sql = "UPDATE accounts SET balance=? WHERE phone=?";
        try (Connection c = DatabaseConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, balance); ps.setString(2, phone);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean setFrozen(String phone, boolean frozen) {
        String sql = "UPDATE accounts SET is_frozen=? WHERE phone=?";
        try (Connection c = DatabaseConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, frozen ? 1 : 0); ps.setString(2, phone);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Account> getAll() {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM accounts ORDER BY full_name";
        try (Connection c = DatabaseConnection.get();
             ResultSet rs = c.createStatement().executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Account map(ResultSet rs) throws SQLException {
        return new Account(
            rs.getInt("id"),
            rs.getString("phone"),
            rs.getString("full_name"),
            rs.getString("pin"),
            rs.getString("account_type"),
            rs.getDouble("balance"),
            rs.getString("role"),
            rs.getInt("is_frozen") == 1
        );
    }
}
