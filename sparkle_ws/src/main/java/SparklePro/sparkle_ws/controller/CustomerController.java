package SparklePro.sparkle_ws.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import SparklePro.sparkle_ws.dbaccess.Customer;
import SparklePro.sparkle_ws.dbaccess.CustomerDAO;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {
    
    @GetMapping("/{userId}")
    public ResponseEntity<?> getCustomer(@PathVariable("userId") int userId) {
        try {
            CustomerDAO customerDAO = new CustomerDAO();
            Customer customer = customerDAO.getCustomerById(userId);
            
            if (customer != null) {
                return ResponseEntity.ok(customer);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                .body("Error retrieving customer: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateCustomer(@RequestBody Customer customer) {
        try {
            CustomerDAO customerDAO = new CustomerDAO();
            boolean updated = customerDAO.updateCustomer(customer);
            
            if (updated) {
                return ResponseEntity.ok("Customer updated successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to update customer");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                .body("Error updating customer: " + e.getMessage());
        }
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, Object> passwordData) {
        try {
            int userId = (Integer) passwordData.get("userId");
            String oldPassword = (String) passwordData.get("oldPassword");
            String newPassword = (String) passwordData.get("newPassword");

            if (oldPassword == null || newPassword == null) {
                return ResponseEntity.badRequest().body("Both old and new passwords are required");
            }

            CustomerDAO customerDAO = new CustomerDAO();
            boolean changed = customerDAO.changePassword(userId, oldPassword, newPassword);
            
            if (changed) {
                return ResponseEntity.ok("Password changed successfully");
            } else {
                return ResponseEntity.badRequest().body("Invalid current password");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                .body("Error changing password: " + e.getMessage());
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteCustomer(@PathVariable("userId") int userId) {
        try {
            CustomerDAO customerDAO = new CustomerDAO();
            boolean deleted = customerDAO.deleteCustomer(userId);
            
            if (deleted) {
                return ResponseEntity.ok("Customer deleted successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to delete customer");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                .body("Error deleting customer: " + e.getMessage());
        }
    }
}
