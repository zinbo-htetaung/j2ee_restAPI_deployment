package SparklePro.sparkle_ws.dbaccess;

import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class CustomerDAO {
    public Customer getCustomerById(int userId) throws Exception {
        Connection conn = DBConnection.getConnection();
        Customer customer = null;

        String sql = "SELECT * FROM users WHERE userId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                customer = new Customer();
                customer.setUserId(rs.getInt("userId"));
                customer.setUsername(rs.getString("username"));
                customer.setEmail(rs.getString("email"));
                customer.setCreatedAt(rs.getString("created_at"));
                customer.setUpdatedAt(rs.getString("updated_at"));
            }
        } finally {
            conn.close();
        }
        return customer;
    }

    public boolean updateCustomer(Customer customer) throws Exception {
        Connection conn = DBConnection.getConnection();

        String sql = "UPDATE users SET username = ?, email = ?, updated_at = NOW() WHERE userId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getUsername());
            stmt.setString(2, customer.getEmail());
            stmt.setInt(3, customer.getUserId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } finally {
            conn.close();
        }
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) throws Exception {
        Connection conn = DBConnection.getConnection();

        // First, verify the old password
        String verifySql = "SELECT password FROM users WHERE userId = ?";
        try (PreparedStatement verifyStmt = conn.prepareStatement(verifySql)) {
            verifyStmt.setInt(1, userId);
            ResultSet rs = verifyStmt.executeQuery();

            if (!rs.next()) {
                return false; // User not found
            }

            String storedHashedPassword = rs.getString("password");

            // Check if old password matches the hashed password
            if (!BCrypt.checkpw(oldPassword, storedHashedPassword)) {
                return false; // Old password is incorrect
            }
        }

        // Hash the new password before updating
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

        // Update to new hashed password
        String updateSql = "UPDATE users SET password = ?, updated_at = NOW() WHERE userId = ?";
        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            updateStmt.setString(1, hashedPassword);
            updateStmt.setInt(2, userId);

            int rowsAffected = updateStmt.executeUpdate();
            return rowsAffected > 0;
        } finally {
            conn.close();
        }
    }

    public boolean deleteCustomer(int userId) throws Exception {
        Connection conn = DBConnection.getConnection();
        conn.setAutoCommit(false);

        try {
            // Delete related records first (assuming foreign key constraints)
            String[] deleteQueries = {
                "DELETE FROM bookingDetail WHERE bookingId IN (SELECT bookingId FROM booking WHERE customerId = ?)",
                "DELETE FROM checkoutHistory WHERE userId = ?",
                "DELETE FROM booking WHERE customerId = ?",
                "DELETE FROM users WHERE userId = ?"
            };

            for (String sql : deleteQueries) {
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, userId);
                    stmt.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }
}
