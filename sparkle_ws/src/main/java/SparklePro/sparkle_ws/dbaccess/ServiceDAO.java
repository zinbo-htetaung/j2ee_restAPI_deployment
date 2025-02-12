package SparklePro.sparkle_ws.dbaccess;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceDAO {
	public List<Service> getAllServices() throws SQLException {
		List<Service> services = new ArrayList<>();
		try {
			Connection conn = DBConnection.getConnection();
			String sql = "SELECT * FROM service";

			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				int serviceId = rs.getInt("serviceId");
				String name = rs.getString("name");
				String description = rs.getString("description");
				int categoryId = rs.getInt("categoryId");
				double price = rs.getDouble("price");
				String image = rs.getString("image");
				String created_at = rs.getString("created_at");
				String updated_at = rs.getString("updated_at");

				Service service = new Service();
				service.setServiceId(serviceId);
				service.setName(name);
				service.setDescription(description);
				service.setCategoryId(categoryId);
				service.setPrice(price);
				service.setImage(image);
				service.setCreated_at(created_at);
				service.setUpdated_at(updated_at);
				services.add(service);
			}
			conn.close();
			return services;
		} catch (SQLException e) {
			System.err.println("SQL Error: " + e.getMessage());
			return null;
		} catch (Exception err) {
			System.out.println("Error fetching services: " + err.getMessage());
			return null;
		}
	}

	public List<Service> getFilteredServices(String name, Integer categoryId, Double minPrice, Double maxPrice) {
		List<Service> serviceList = new ArrayList<>();
		try {
			Connection conn = DBConnection.getConnection();

			// Build SQL query dynamically based on parameters
			String sql = "SELECT * FROM service WHERE 1=1"; // Base query

			if (name != null && !name.trim().isEmpty()) {
				sql += " AND name LIKE ?";
			}
			if (categoryId != null) {
				sql += " AND categoryId = ?";
			}
			if (minPrice != null) {
				sql += " AND price >= ?";
			}
			if (maxPrice != null) {
				sql += " AND price <= ?";
			}

			PreparedStatement pstmt = conn.prepareStatement(sql);

			int paramIndex = 1; // Keeps track of the parameter position

			if (name != null && !name.trim().isEmpty()) {
				pstmt.setString(paramIndex++, "%" + name + "%"); // Enables partial search
			}
			if (categoryId != null) {
				pstmt.setInt(paramIndex++, categoryId);
			}
			if (minPrice != null) {
				pstmt.setDouble(paramIndex++, minPrice);
			}
			if (maxPrice != null) {
				pstmt.setDouble(paramIndex++, maxPrice);
			}

			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Service service = new Service();
				service.setServiceId(rs.getInt("serviceId"));
				service.setName(rs.getString("name"));
				service.setDescription(rs.getString("description"));
				service.setCategoryId(rs.getInt("categoryId"));
				service.setPrice(rs.getDouble("price"));
				service.setImage(rs.getString("image"));
				service.setCreated_at(rs.getString("created_at"));
				service.setUpdated_at(rs.getString("updated_at"));
				serviceList.add(service);
			}
			rs.close();
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			System.out.println("Error fetching services with filters: " + e);
		}
		return serviceList;
	}

	// ✅ Get a single service by ID
	public Service getServiceById(int serviceId) {
		try {
			Connection conn = DBConnection.getConnection();
			String sql = "SELECT * FROM service WHERE serviceId = ?";

			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, serviceId);
			ResultSet rs = pstmt.executeQuery();

			Service service = new Service();

			if (rs.next()) {
				service.setServiceId(rs.getInt("serviceId"));
				service.setName(rs.getString("name"));
				service.setDescription(rs.getString("description"));
				service.setCategoryId(rs.getInt("categoryId"));
				service.setPrice(rs.getFloat("price"));
				service.setImage(rs.getString("image"));
				service.setCreated_at(rs.getString("created_at"));
				service.setUpdated_at(rs.getString("updated_at"));
			}
			conn.close();
			return service;
		} catch (Exception err) {
			System.out.println("Error fetching service by ID: " + err.getMessage());
			return null;
		}
	}

	// ✅ Create (Insert) a new service
	public int createService(Service service) {
		int rowsAffected = 0;
		try {
			Connection conn = DBConnection.getConnection();
			String sql = "INSERT INTO service (name, description, categoryId, price, image, created_at, updated_at) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

			PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, service.getName());
			pstmt.setString(2, service.getDescription());
			pstmt.setInt(3, service.getCategoryId());
			pstmt.setDouble(4, service.getPrice());
			pstmt.setString(5, service.getImage());

			rowsAffected = pstmt.executeUpdate();

			// Get the generated serviceId
			ResultSet rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				service.setServiceId(rs.getInt(1));
			}

			conn.close();
			return rowsAffected;
		} catch (Exception err) {
			System.out.println("Error creating service: " + err.getMessage());
			return rowsAffected;
		}
	}

	// ✅ Update a service
	public int updateService(int serviceId, Service service) {
		int rowsAffected = 0;
		try {
			Connection conn = DBConnection.getConnection();
			String sql = "UPDATE service SET name = ?, description = ?, categoryId = ?, price = ?, image = ?, updated_at = CURRENT_TIMESTAMP WHERE serviceId = ?";

			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, service.getName());
			pstmt.setString(2, service.getDescription());
			pstmt.setInt(3, service.getCategoryId());
			pstmt.setDouble(4, service.getPrice());
			pstmt.setString(5, service.getImage());
			pstmt.setInt(6, serviceId);

			rowsAffected = pstmt.executeUpdate();

			conn.close();
			return rowsAffected;
		} catch (Exception err) {
			System.out.println("Error updating service: " + err.getMessage());
			return rowsAffected;
		}
	}

	// ✅ Delete a service by ID
	public int deleteService(int serviceId) {
		int rowsAffected = 0;
		try {
			Connection conn = DBConnection.getConnection();
			String sql = "DELETE FROM service WHERE serviceId = ?";

			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, serviceId);

			rowsAffected = pstmt.executeUpdate();

			conn.close();
			return rowsAffected;
		} catch (Exception err) {
			System.out.println("Error deleting service: " + err.getMessage());
			return rowsAffected;
		}
	}

	public Map<String, Object> getPopularServices() {
		List<Service> popularServices = new ArrayList<>();
		Map<Integer, Integer> bookingCounts = new HashMap<>(); // Stores serviceId -> totalBookings

		try {
			Connection conn = DBConnection.getConnection();

			// SQL Query: Count bookings per service
			String sql = "SELECT s.serviceId, s.name, s.description, s.image, s.price, COUNT(bd.serviceId) AS totalBookings "
					+ "FROM service s " + "LEFT JOIN bookingDetail bd ON s.serviceId = bd.serviceId "
					+ "GROUP BY s.serviceId " + "ORDER BY totalBookings DESC " + "LIMIT 5";

			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Service service = new Service();
				service.setServiceId(rs.getInt("serviceId"));
				service.setName(rs.getString("name"));
				service.setDescription(rs.getString("description"));
				service.setImage(rs.getString("image"));
				service.setPrice(rs.getDouble("price"));

				int totalBookings = rs.getInt("totalBookings"); // ✅ Dynamically calculated
				bookingCounts.put(service.getServiceId(), totalBookings); // Store total bookings in a map

				popularServices.add(service);
			}

			rs.close();
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			System.out.println("Error fetching popular services: " + e);
		}

		// ✅ Return both the popular services list and the booking counts
		Map<String, Object> result = new HashMap<>();
		result.put("popularServices", popularServices);
		result.put("bookingCounts", bookingCounts);
		return result;
	}

	public Map<String, Object> getServicesFilterByRating(boolean ascending) {
		List<Map<String, Object>> sortedServices = new ArrayList<>();

		try (Connection conn = DBConnection.getConnection()) {

			// ✅ Corrected SQL Query: Include `averageRatings` directly in the output
			String sql = "SELECT s.serviceId, s.name, s.description, s.image, s.price, s.categoryId, s.created_at, s.updated_at, "
					+ "COALESCE(AVG(f.rating), 0) AS averageRatings " // Include avgRating inside the JSON structure
					+ "FROM service s " + "LEFT JOIN feedback f ON s.serviceId = f.serviceId "
					+ "GROUP BY s.serviceId, s.name, s.description, s.image, s.price, s.categoryId, s.created_at, s.updated_at "
					+ "ORDER BY averageRatings " + (ascending ? "ASC" : "DESC");

			try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

				while (rs.next()) {
					// ✅ Store service details in a HashMap (for structured JSON output)
					Map<String, Object> serviceData = new HashMap<>();
					serviceData.put("serviceId", rs.getInt("serviceId"));
					serviceData.put("name", rs.getString("name"));
					serviceData.put("description", rs.getString("description"));
					serviceData.put("categoryId", rs.getInt("categoryId"));
					serviceData.put("price", rs.getDouble("price"));
					serviceData.put("image", rs.getString("image"));
					serviceData.put("created_at", rs.getTimestamp("created_at"));
					serviceData.put("updated_at", rs.getTimestamp("updated_at"));
					serviceData.put("averageRatings", rs.getDouble("averageRatings")); // ✅ Directly inside JSON

					sortedServices.add(serviceData);
				}
			}

		} catch (Exception e) {
			System.err.println("❌ Error fetching services sorted by rating: " + e.getMessage());
			e.printStackTrace();
		}

		// ✅ Return services in the correct JSON format
		Map<String, Object> result = new HashMap<>();
		result.put("services", sortedServices);
		return result;
	}

	public double getAverageRating(int serviceId) {
		double averageRating = 0.0;
		try {
			Connection conn = DBConnection.getConnection();
			String sql = "SELECT AVG(rating) AS avgRating FROM feedback WHERE serviceId = ?";

			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, serviceId);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				averageRating = rs.getDouble("avgRating");
			}

			conn.close();
		} catch (Exception e) {
			System.out.println("Error fetching average rating: " + e.getMessage());
		}
		return averageRating;
	}

	public List<Map<String, Object>> getAllReviewsForService(int serviceId) {
		List<Map<String, Object>> reviews = new ArrayList<>();

		try {
			Connection conn = DBConnection.getConnection();
			String sql = "SELECT f.rating, f.review, u.username, f.created_at FROM feedback f "
					+ "JOIN users u ON f.userId = u.userId WHERE f.serviceId = ? " + "ORDER BY f.created_at DESC";

			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, serviceId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Map<String, Object> reviewData = new HashMap<>();
				reviewData.put("rating", rs.getInt("rating"));
				reviewData.put("review", rs.getString("review"));
				reviewData.put("username", rs.getString("username"));
				reviewData.put("date", rs.getTimestamp("created_at"));
				reviews.add(reviewData);
			}

			conn.close();
		} catch (Exception e) {
			System.out.println("Error fetching reviews: " + e.getMessage());
		}
		return reviews;
	}
}
