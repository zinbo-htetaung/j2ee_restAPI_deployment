package SparklePro.sparkle_ws.dbaccess;

import java.sql.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingDAO {
	public List<Booking> getBookingsByCustomer(int customerId) throws Exception {
		List<Booking> bookings = new ArrayList<>();
		Connection conn = DBConnection.getConnection();

		String sql = "SELECT * FROM booking WHERE customerId = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, customerId);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				Booking booking = new Booking();
				booking.setBookingId(rs.getInt("bookingId"));
				booking.setCustomerId(rs.getInt("customerId"));
				booking.setTotalAmount(rs.getFloat("totalAmount"));
				booking.setStatus(rs.getString("status"));
				booking.setPaymentStatus(rs.getString("paymentStatus"));
				booking.setCreatedAt(rs.getString("created_at"));
				booking.setUpdatedAt(rs.getString("updated_at"));

				bookings.add(booking);
			}
		} finally {
			conn.close();
		}
		return bookings;
	}

	public List<BookingDetail> getBookingDetails(int bookingId) throws Exception {
		List<BookingDetail> details = new ArrayList<>();
		Connection conn = DBConnection.getConnection();

		String sql = "SELECT bd.*, s.name AS serviceName FROM bookingDetail bd "
				+ "JOIN service s ON bd.serviceId = s.serviceId " + "WHERE bd.bookingId = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, bookingId);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				BookingDetail detail = new BookingDetail();
				detail.setBookingDetailId(rs.getInt("bookingDetailId"));
				detail.setBookingId(rs.getInt("bookingId"));
				detail.setServiceId(rs.getInt("serviceId"));
				detail.setCustomerReq(rs.getString("customerReq"));
				detail.setBookingDate(rs.getTimestamp("bookingDate"));
				detail.setAddress(rs.getString("address"));
				detail.setStatus(rs.getString("status"));
				detail.setCreatedAt(rs.getTimestamp("created_at"));
				detail.setUpdatedAt(rs.getTimestamp("updated_at"));
				detail.setServiceName(rs.getString("serviceName"));

				details.add(detail);
			}
		} finally {
			conn.close();
		}
		return details;
	}

	public boolean updateBookingDetails(int bookingDetailId, Map<String, Object> updates) throws Exception {
		Connection conn = DBConnection.getConnection();

		StringBuilder sql = new StringBuilder("UPDATE bookingDetail SET ");
		List<Object> values = new ArrayList<>();

		for (Map.Entry<String, Object> entry : updates.entrySet()) {
			sql.append(entry.getKey()).append(" = ?, ");
			values.add(entry.getValue());
		}

		sql.append("updated_at = NOW() WHERE bookingDetailId = ?");
		values.add(bookingDetailId);

		try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
			for (int i = 0; i < values.size(); i++) {
				stmt.setObject(i + 1, values.get(i));
			}
			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0;
		} finally {
			conn.close();
		}
	}

	public boolean deleteBookingDetails(int bookingDetailId) throws Exception {
		Connection conn = DBConnection.getConnection();
		conn.setAutoCommit(false); // Start transaction

		try {
			// First, get the bookingId and service price for the booking detail
			String getDetailsSQL = "SELECT bd.bookingId, s.price " + "FROM bookingDetail bd "
					+ "JOIN service s ON bd.serviceId = s.serviceId " + "WHERE bd.bookingDetailId = ?";

			int bookingId = 0;
			float servicePrice = 0;

			try (PreparedStatement getStmt = conn.prepareStatement(getDetailsSQL)) {
				getStmt.setInt(1, bookingDetailId);
				ResultSet rs = getStmt.executeQuery();

				if (rs.next()) {
					bookingId = rs.getInt("bookingId");
					servicePrice = rs.getFloat("price");
				} else {
					return false; // Booking detail not found
				}
			}

			// Delete the booking detail
			String deleteDetailSQL = "DELETE FROM bookingDetail WHERE bookingDetailId = ?";
			try (PreparedStatement deleteStmt = conn.prepareStatement(deleteDetailSQL)) {
				deleteStmt.setInt(1, bookingDetailId);
				deleteStmt.executeUpdate();
			}

			// Update the total amount in the booking table
			String updateBookingSQL = "UPDATE booking " + "SET totalAmount = totalAmount - ?, " + "updated_at = NOW() "
					+ "WHERE bookingId = ?";

			try (PreparedStatement updateStmt = conn.prepareStatement(updateBookingSQL)) {
				updateStmt.setFloat(1, servicePrice);
				updateStmt.setInt(2, bookingId);
				updateStmt.executeUpdate();
			}

			// Check if there are any remaining booking details
			String checkRemainingSQL = "SELECT COUNT(*) as count FROM bookingDetail WHERE bookingId = ?";
			boolean shouldDeleteBooking = false;

			try (PreparedStatement checkStmt = conn.prepareStatement(checkRemainingSQL)) {
				checkStmt.setInt(1, bookingId);
				ResultSet rs = checkStmt.executeQuery();

				if (rs.next() && rs.getInt("count") == 0) {
					shouldDeleteBooking = true;
				}
			}

			// If no booking details remain, delete the booking
			if (shouldDeleteBooking) {
				String deleteBookingSQL = "DELETE FROM booking WHERE bookingId = ?";
				try (PreparedStatement deleteBookingStmt = conn.prepareStatement(deleteBookingSQL)) {
					deleteBookingStmt.setInt(1, bookingId);
					deleteBookingStmt.executeUpdate();
				}
			}

			conn.commit(); // Commit transaction
			return true;

		} catch (SQLException e) {
			conn.rollback(); // Rollback on error
			throw e;
		} finally {
			conn.setAutoCommit(true); // Reset auto-commit
			conn.close();
		}
	}

	public int createBooking(int customerId, List<Map<String, Object>> cart, double subtotalAmount, double gstAmount,
			double totalAmount, String paymentMethod, String transactionRef) throws Exception {
		Connection conn = DBConnection.getConnection();
		try {
			conn.setAutoCommit(false);

// Insert into booking table
			String bookingQuery = "INSERT INTO booking (customerId, totalAmount, status, paymentStatus, created_at, updated_at) "
					+ "VALUES (?, ?, ?, ?, NOW(), NOW())";

			int bookingId;
			try (PreparedStatement bookingStmt = conn.prepareStatement(bookingQuery, Statement.RETURN_GENERATED_KEYS)) {
				bookingStmt.setInt(1, customerId);
				bookingStmt.setDouble(2, totalAmount);
				bookingStmt.setString(3, "Pending");
				bookingStmt.setString(4, "Unpaid");
				bookingStmt.executeUpdate();

				ResultSet generatedKeys = bookingStmt.getGeneratedKeys();
				if (!generatedKeys.next()) {
					throw new SQLException("Failed to retrieve booking ID.");
				}
				bookingId = generatedKeys.getInt(1);
			}

// Insert booking details
			String detailQuery = "INSERT INTO bookingDetail (bookingId, serviceId, customerReq, bookingDate, address, status, created_at, updated_at) "
					+ "VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW())";

			try (PreparedStatement detailStmt = conn.prepareStatement(detailQuery)) {
				for (Map<String, Object> item : cart) {
					detailStmt.setInt(1, bookingId);
					detailStmt.setInt(2, ((Number) item.get("serviceId")).intValue());
					detailStmt.setString(3, (String) item.get("customerReq"));
					detailStmt.setString(4, (String) item.get("bookingDate"));
					detailStmt.setString(5, (String) item.get("address"));
					detailStmt.setString(6, "Pending");
					detailStmt.addBatch();
				}
				detailStmt.executeBatch();
			}

// Insert checkout history
			String checkoutQuery = "INSERT INTO checkoutHistory (userId, bookingId, subtotalAmount, gstAmount, "
					+ "totalAmount, paymentMethod, paymentStatus, transactionRef, created_at) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";

			try (PreparedStatement checkoutStmt = conn.prepareStatement(checkoutQuery)) {
				checkoutStmt.setInt(1, customerId);
				checkoutStmt.setInt(2, bookingId);
				checkoutStmt.setDouble(3, subtotalAmount);
				checkoutStmt.setDouble(4, gstAmount);
				checkoutStmt.setDouble(5, totalAmount);
				checkoutStmt.setString(6, paymentMethod);
				checkoutStmt.setString(7, "Pending");
				checkoutStmt.setString(8, transactionRef);
				checkoutStmt.executeUpdate();
			}

			conn.commit();
			return bookingId;

		} catch (Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
			conn.close();
		}
	}

	public List<Map<String, Object>> getFilteredBookings(String customerEmail, String serviceName, String status,
			String startDate, String endDate, String bookingDateStart, String bookingDateEnd) {
		List<Map<String, Object>> bookingList = new ArrayList<>();
		try {
			Connection conn = DBConnection.getConnection();

			// Build SQL query dynamically based on parameters
			String sql = "SELECT bd.bookingId AS booking_id, bd.bookingDetailId AS bookingDetailId, u.username AS customer_name, u.email AS customer_email, s.name AS service_name, "
					+ "bd.status AS booking_detail_status, bd.bookingDate, bd.created_at AS booking_detail_created_at, "
					+ "bd.updated_at AS booking_detail_updated_at " + "FROM bookingDetail bd "
					+ "INNER JOIN booking b ON bd.bookingId = b.bookingId "
					+ "INNER JOIN service s ON bd.serviceId = s.serviceId "
					+ "INNER JOIN users u ON b.customerId = u.userId " + "WHERE 1=1";

			// Dynamic Conditions
			if (customerEmail != null && !customerEmail.trim().isEmpty()) {
				sql += " AND u.email LIKE ?";
			}
			if (serviceName != null && !serviceName.trim().isEmpty()) {
				sql += " AND s.name LIKE ?";
			}
			if (status != null && !status.trim().isEmpty()) {
				sql += " AND bd.status = ?";
			}
			if (startDate != null && !startDate.trim().isEmpty()) {
				sql += " AND DATE(bd.created_at) = ?";
			}
			if (endDate != null && !endDate.trim().isEmpty()) {
				sql += " AND DATE(bd.created_at) = ?";
			}
			if (bookingDateStart != null && !bookingDateStart.trim().isEmpty()) {
				sql += " AND DATE(bd.bookingDate) = ?";
			}
			if (bookingDateEnd != null && !bookingDateEnd.trim().isEmpty()) {
				sql += " AND DATE(bd.bookingDate) = ?";
			}

			PreparedStatement pstmt = conn.prepareStatement(sql);
			int paramIndex = 1; // Keeps track of the parameter position

			// ✅ Set the parameters dynamically
			if (customerEmail != null && !customerEmail.trim().isEmpty()) {
				pstmt.setString(paramIndex++, "%" + customerEmail + "%");
			}
			if (serviceName != null && !serviceName.trim().isEmpty()) {
				pstmt.setString(paramIndex++, "%" + serviceName + "%");
			}
			if (status != null && !status.trim().isEmpty()) {
				pstmt.setString(paramIndex++, status);
			}
			if (startDate != null && !startDate.trim().isEmpty()) {
				pstmt.setDate(paramIndex++, java.sql.Date.valueOf(startDate)); // ✅ Convert String to SQL Date
			}
			if (endDate != null && !endDate.trim().isEmpty()) {
				pstmt.setDate(paramIndex++, java.sql.Date.valueOf(endDate)); // ✅ Convert String to SQL Date
			}
			if (bookingDateStart != null && !bookingDateStart.trim().isEmpty()) {
				pstmt.setDate(paramIndex++, java.sql.Date.valueOf(bookingDateStart)); // ✅ Convert String to SQL Date
			}
			if (bookingDateEnd != null && !bookingDateEnd.trim().isEmpty()) {
				pstmt.setDate(paramIndex++, java.sql.Date.valueOf(bookingDateEnd)); // ✅ Convert String to SQL Date
			}

			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				Map<String, Object> booking = new HashMap<>();
				booking.put("customer_name", rs.getString("customer_name"));
				booking.put("customer_email", rs.getString("customer_email"));
				booking.put("service_name", rs.getString("service_name"));
				booking.put("booking_detail_status", rs.getString("booking_detail_status"));
				booking.put("booking_date", rs.getString("bookingDate"));
				booking.put("updated_at", rs.getString("booking_detail_updated_at"));
				booking.put("booking_id", rs.getString("booking_id"));
				booking.put("bookingDetailId", rs.getString("bookingDetailId"));


				bookingList.add(booking);
			}
			rs.close();
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			System.out.println("Error fetching filtered bookings: " + e);
		}
		return bookingList;
	}
}
