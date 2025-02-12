package SparklePro.sparkle_ws.controller;

import java.util.ArrayList;
import SparklePro.sparkle_ws.dbaccess.*;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/api/booking")
public class BookingController {

	@PostMapping("/createBooking")
	public ResponseEntity<?> createBooking(@RequestBody BookingRequest request) {
		try {
			// Validate request
			if (request.getCart() == null || request.getCart().isEmpty()) {
				return ResponseEntity.badRequest().body("Cart is empty");
			}
			if (request.getPaymentMethod() == null || request.getPaymentMethod().trim().isEmpty()) {
				return ResponseEntity.badRequest().body("Payment method is required");
			}

			BookingDAO bookingDAO = new BookingDAO();
			int bookingId = bookingDAO.createBooking(request.getCustomerId(), request.getCart(),
					request.getSubtotalAmount(), request.getGstAmount(), request.getTotalAmount(),
					request.getPaymentMethod(), request.getTransactionRef());

			return ResponseEntity.ok().body(Map.of("bookingId", bookingId));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Failed to create booking: " + e.getMessage());
		}
	}

	@GetMapping("/getBookingDetails/{bookingId}")
	public List<BookingDetail> getBookingDetails(@PathVariable("bookingId") int bookingId) {
		List<BookingDetail> details = new ArrayList<>();
		try {
			BookingDAO bookingDAO = new BookingDAO();
			details = bookingDAO.getBookingDetails(bookingId);
		} catch (Exception e) {
			System.out.println("Error: " + e);
		}
		return details;
	}

	@GetMapping("/getBookings/{customerId}")
	public List<Booking> getBookings(@PathVariable("customerId") int customerId) {
		List<Booking> bookings = new ArrayList<>();
		try {
			BookingDAO bookingDAO = new BookingDAO();
			bookings = bookingDAO.getBookingsByCustomer(customerId);
		} catch (Exception e) {
			System.out.println("Error: " + e);
		}
		return bookings;
	}

	@PutMapping("/updateBookingDetails/{bookingDetailId}")
	public ResponseEntity<?> updateBookingDetails(@PathVariable("bookingDetailId") int bookingDetailId,
			@RequestBody Map<String, Object> updates) {
		try {
			BookingDAO bookingDAO = new BookingDAO();
			boolean updated = bookingDAO.updateBookingDetails(bookingDetailId, updates);
			if (updated) {
				return ResponseEntity.ok("Booking details updated successfully.");
			} else {
				return ResponseEntity.badRequest().body("Failed to update booking details.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Error updating booking details: " + e.getMessage());
		}
	}

	@DeleteMapping("/deleteBookingDetails/{bookingDetailId}")
	public ResponseEntity<?> deleteBookingDetails(@PathVariable("bookingDetailId") int bookingDetailId) {
		try {
			BookingDAO bookingDAO = new BookingDAO();
			boolean deleted = bookingDAO.deleteBookingDetails(bookingDetailId);
			if (deleted) {
				return ResponseEntity.ok("Booking details deleted successfully.");
			} else {
				return ResponseEntity.badRequest().body("Failed to delete booking details.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Error deleting booking details: " + e.getMessage());
		}
	}

	@RequestMapping(method = RequestMethod.GET, path = "/getFilteredBookings")
	public List<Map<String, Object>> getFilteredBookings(@RequestParam(required = false) String customerEmail,
			@RequestParam(required = false) String serviceName, @RequestParam(required = false) String status,
			@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
			@RequestParam(required = false) String bookingDateStart,
			@RequestParam(required = false) String bookingDateEnd) {

		List<Map<String, Object>> bookings = new ArrayList<>();
		try {
			BookingDAO db = new BookingDAO();
			bookings = db.getFilteredBookings(customerEmail, serviceName, status, startDate, endDate, bookingDateStart,
					bookingDateEnd);
		} catch (Exception e) {
			System.out.println("Error fetching filtered bookings: " + e);
		}
		return bookings;
	}
}