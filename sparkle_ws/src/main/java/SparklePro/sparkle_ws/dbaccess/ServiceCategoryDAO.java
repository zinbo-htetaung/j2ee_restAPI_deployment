package SparklePro.sparkle_ws.dbaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ServiceCategoryDAO {
	public List<ServiceCategory> getAllServiceCategories() throws SQLException {
		List<ServiceCategory> serviceCategories = new ArrayList<>();
		try {
			Connection conn = DBConnection.getConnection();
			String sql = "SELECT * FROM serviceCategory";

			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				int serviceCategoryId = rs.getInt("serviceCategoryId");
				String name = rs.getString("name");
				String description = rs.getString("description");
				String created_at = rs.getString("created_at");
				String updated_at = rs.getString("updated_at");

				ServiceCategory serviceCategory = new ServiceCategory();
				serviceCategory.setServiceCategoryId(serviceCategoryId);
				serviceCategory.setName(name);
				serviceCategory.setDescription(description);
				serviceCategory.setCreated_at(created_at);
				serviceCategory.setUpdated_at(updated_at);
				serviceCategories.add(serviceCategory);
			}
			conn.close();
			return serviceCategories;
		} catch (SQLException e) {
			System.err.println("SQL Error: " + e.getMessage());
			return null;
		} catch (Exception err) {
			System.out.println("Error fetching serviceCategory: " + err.getMessage());
			return null;
		}
	}

	// ✅ Get a single serviceCategory by ID
	public ServiceCategory getServiceCategoryById(int serviceCategoryId) {
		try {
			Connection conn = DBConnection.getConnection();
			String sql = "SELECT * FROM serviceCategory WHERE serviceCategoryId = ?";

			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, serviceCategoryId);
			ResultSet rs = pstmt.executeQuery();

			ServiceCategory serviceCategory = new ServiceCategory();

			if (rs.next()) {
				serviceCategory.setServiceCategoryId(rs.getInt("serviceCategoryId"));
				serviceCategory.setName(rs.getString("name"));
				serviceCategory.setDescription(rs.getString("description"));
				serviceCategory.setCreated_at(rs.getString("created_at"));
				serviceCategory.setUpdated_at(rs.getString("updated_at"));
			}
			conn.close();
			return serviceCategory;
		} catch (Exception err) {
			System.out.println("Error fetching serviceCategory by ID: " + err.getMessage());
			return null;
		}
	}
	
	// ✅ Create (Insert) a new serviceCategory
	public int createServiceCategory(ServiceCategory serviceCategory) {
	    int rowsAffected = 0;
	    try {
	        Connection conn = DBConnection.getConnection();
	        String sql = "INSERT INTO serviceCategory (name, description, created_at, updated_at) VALUES (?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

	        PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        pstmt.setString(1, serviceCategory.getName());
	        pstmt.setString(2, serviceCategory.getDescription());

	        rowsAffected = pstmt.executeUpdate();
	        
	        // Get the generated serviceCategoryId
	        ResultSet rs = pstmt.getGeneratedKeys();
	        if (rs.next()) {
	        	serviceCategory.setServiceCategoryId(rs.getInt(1));
	        }

	        conn.close();
	        return rowsAffected;
	    } catch (Exception err) {
	        System.out.println("Error creating serviceCategory: " + err.getMessage());
	        return rowsAffected;
	    }
	}

	// ✅ Update a serviceCategory
	public int updateServiceCategory(int serviceCategoryId, ServiceCategory serviceCategory) {
		int rowsAffected = 0;
		try {
			Connection conn = DBConnection.getConnection();
			String sql = "UPDATE serviceCategory SET name = ?, description = ?, updated_at = CURRENT_TIMESTAMP WHERE serviceCategoryId = ?";

			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, serviceCategory.getName());
			pstmt.setString(2, serviceCategory.getDescription());
			pstmt.setInt(3, serviceCategoryId);
			
			rowsAffected = pstmt.executeUpdate();
			
			conn.close();
			return rowsAffected;
		} catch (Exception err) {
			System.out.println("Error updating serviceCategory: " + err.getMessage());
			return rowsAffected;
		}
	}

	// ✅ Delete a serviceCategory by ID
	public int deleteServiceCategory(int serviceCategoryId) {
		int rowsAffected = 0;
		try {
			Connection conn = DBConnection.getConnection();
			String sql = "DELETE FROM serviceCategory WHERE serviceCategoryId = ?";

			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, serviceCategoryId);

			rowsAffected = pstmt.executeUpdate();
			
			conn.close();
			return rowsAffected;
		} catch (Exception err) {
			System.out.println("Error deleting serviceCategory: " + err.getMessage());
			return rowsAffected;
		}
	}
}
