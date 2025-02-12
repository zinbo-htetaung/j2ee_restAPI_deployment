package SparklePro.sparkle_ws.dbaccess;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.connector.Response;

public class UserDAO {

	// Retrieve user details by user ID
	public User getUserDetails(int userId) throws Exception {
		try {
			Connection conn = DBConnection.getConnection();
			String sql = "SELECT * FROM users WHERE userId = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userId);
			ResultSet rs = pstmt.executeQuery();

			User user = null;
			if (rs.next()) {
				user = new User();
				user.setUserId(rs.getInt("userId"));
				user.setUsername(rs.getString("username"));
				user.setPassword(rs.getString("password")); // Store securely
				user.setEmail(rs.getString("email"));
				user.setRole(rs.getString("role"));
				user.setCreated_ad(rs.getString("created_at"));
				user.setUpdated_at(rs.getString("updated_at"));
			}
			conn.close();
			return user;

		} catch (SQLException err) {
			System.out.println("Error fetching user details: " + err);
			return null;
		}
	}

	// Retrieve all users
	public ArrayList<User> listAllUsers() throws Exception {
		ArrayList<User> userList = new ArrayList<>();
		try {
			Connection conn = DBConnection.getConnection();
			String sql = "SELECT * FROM users";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				User user = new User();
				user.setUserId(rs.getInt("userId"));
				user.setUsername(rs.getString("username"));
				user.setEmail(rs.getString("email"));
				user.setRole(rs.getString("role"));
				user.setCreated_ad(rs.getString("created_at"));
				user.setUpdated_at(rs.getString("updated_at"));
				userList.add(user);
			}
			conn.close();
		} catch (SQLException e) {
			System.err.println("SQL Error in listAllUsers: " + e.getMessage());
		}
		return userList;
	}

	// Insert new user
	public int insertUser(User user) throws Exception {
		try {
			Connection conn = DBConnection.getConnection();
			String sql = "INSERT INTO users (username, password, email, role, created_at, updated_at) VALUES (?, ?, ?, ?, NOW(), NOW())";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, user.getUsername());
			pstmt.setString(2, user.getPassword()); // Ensure password hashing
			pstmt.setString(3, user.getEmail());
			pstmt.setString(4, user.getRole());

			int rowsInserted = pstmt.executeUpdate();
			conn.close();
			return rowsInserted;

		} catch (SQLException err) {
			System.out.println("Error inserting user: " + err);
			return 0;
		}
	}

	// Update user details
	public int updateUser(int userId, User user) throws Exception {
		try {
			Connection conn = DBConnection.getConnection();
			String sql = "UPDATE users SET username = ?, email = ?, updated_at = NOW() WHERE userId = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, user.getUsername());
			pstmt.setString(2, user.getEmail());
			pstmt.setInt(3, userId);

			int rowsUpdated = pstmt.executeUpdate();
			conn.close();
			return rowsUpdated;

		} catch (SQLException err) {
			System.out.println("Error updating user: " + err);
			return 0;
		}
	}

	// Delete a user
	public int deleteUser(int userId) throws Exception {
		try {
			Connection conn = DBConnection.getConnection();
			String sql = "DELETE FROM users WHERE userId = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, userId);

			int rowsDeleted = pstmt.executeUpdate();
			conn.close();
			return rowsDeleted;

		} catch (SQLException err) {
			System.out.println("Error deleting user: " + err);
			return 0;
		}
	}

	// Check if email exists (useful for registration validation)
	public boolean emailExists(String email) throws Exception {
		try {
			Connection conn = DBConnection.getConnection();
			String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, email);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next() && rs.getInt(1) > 0) {
				return true;
			}
			conn.close();
		} catch (SQLException err) {
			System.out.println("Error checking email existence: " + err);
		}
		return false;
	}

	public List<User> getFilteredUsers(String username, String email, String role, String sortTimestamp) {
		List<User> userList = new ArrayList<>();
		String sql = "SELECT * FROM users WHERE 1=1"; // Base query

		try (Connection conn = DBConnection.getConnection()) {

			// Dynamically add filters
			if (username != null && !username.trim().isEmpty()) {
				sql += " AND username LIKE ?";
			}
			if (email != null && !email.trim().isEmpty()) {
				sql += " AND email LIKE ?";
			}
			if (role != null && !role.trim().isEmpty() && !"all".equals(role)) {
				sql += " AND role = ?";
			}

			// Apply sorting
			if (sortTimestamp != null) {
				sql += " ORDER BY created_at " + ("oldest".equalsIgnoreCase(sortTimestamp) ? "ASC" : "DESC");
			}

			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
				int paramIndex = 1;

				// Set values dynamically
				if (username != null && !username.trim().isEmpty()) {
					pstmt.setString(paramIndex++, "%" + username + "%"); // Partial match
				}
				if (email != null && !email.trim().isEmpty()) {
					pstmt.setString(paramIndex++, "%" + email + "%"); // Partial match
				}
				if (role != null && !role.trim().isEmpty() && !"all".equals(role)) {
					pstmt.setString(paramIndex++, role);
				}

				try (ResultSet rs = pstmt.executeQuery()) {
					while (rs.next()) {
						User user = new User();
						user.setUserId(rs.getInt("userId"));
						user.setUsername(rs.getString("username"));
						user.setEmail(rs.getString("email"));
						user.setRole(rs.getString("role"));
						user.setCreated_ad(rs.getString("created_at"));
						user.setUpdated_at(rs.getString("updated_at"));
						userList.add(user);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Error fetching users with filters: " + e);
		}
		return userList;
	}

	public List<Map<String, Object>> getTopCustomers() throws Exception {
		List<Map<String, Object>> topCustomers = new ArrayList<>();

		String sql = "SELECT u.username, u.email, SUM(b.totalAmount) AS totalSpent, "
				+ "TIMESTAMPDIFF(MONTH, u.created_at, NOW()) AS membershipMonths " + "FROM users u "
				+ "JOIN booking b ON u.userId = b.customerId " + "GROUP BY u.userId, u.username, u.email, u.created_at "
				+ "ORDER BY totalSpent DESC " + "LIMIT 10";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				Map<String, Object> customer = new HashMap<>();
				customer.put("username", rs.getString("username"));
				customer.put("email", rs.getString("email"));
				customer.put("totalSpent", rs.getDouble("totalSpent"));
				customer.put("membershipMonths", rs.getInt("membershipMonths"));

				topCustomers.add(customer);
			}
		} catch (SQLException e) {
			System.err.println("Error fetching top customers: " + e.getMessage());
			e.printStackTrace(); // Replace with logging in production
			return Collections.emptyList(); // Return empty list instead of `null`
		}

		return topCustomers;
	}

}