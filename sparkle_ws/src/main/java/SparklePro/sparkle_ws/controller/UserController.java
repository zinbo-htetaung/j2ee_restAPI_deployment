package SparklePro.sparkle_ws.controller;

import SparklePro.sparkle_ws.dbaccess.*;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

	@RequestMapping(method = RequestMethod.GET, path = "/getUser/{uid}") // use with (*A)
	public User getUser(@PathVariable("uid") int uid) {
		User user = new User();
		try {
			UserDAO db = new UserDAO();
			user = db.getUserDetails(uid);
		} catch (Exception e) {
			System.out.println("Error :" + e);
		}
		return user;
	}

	@RequestMapping(method = RequestMethod.GET, path = "/getAllUsers")
	public ArrayList<User> getAllUser() {
		ArrayList<User> MyList = null;
		try {
			UserDAO db = new UserDAO();
			MyList = db.listAllUsers();
		} catch (Exception e) {
			System.out.println("Error :" + e);
		}
		return MyList;
	}

	@RequestMapping(path = "/createUser", consumes = "application/json", method = RequestMethod.POST)
	public int createUser(@RequestBody User user) {
		int rec = 0;
		try {
			UserDAO db = new UserDAO();
			rec = db.insertUser(user);
			System.out.print("...done create user.." + rec);
		} catch (Exception e) {
			System.out.print(e.toString());
		}
		return rec;
	}

	@RequestMapping(path = "/deleteUser/{uid}", method = RequestMethod.DELETE)
	public int deleteUser(@PathVariable int uid) {
		int rec = 0;
		try {
			UserDAO db = new UserDAO();
			rec = db.deleteUser(uid);
			System.out.print("...in UserController-done deleting user..." + rec);
		} catch (Exception e) {
			System.out.print(e.toString());
		}
		return rec;
	}

	@RequestMapping(method = RequestMethod.GET, path = "/getFilteredUsers")
	public List<User> getFilteredServices(@RequestParam(required = false) String username,
			@RequestParam(required = false) String email, @RequestParam(required = false) String role,
			@RequestParam(required = false) String sortTimestamp // newest / oldest
	) {
		List<User> users = new ArrayList<>();
		try {
			UserDAO db = new UserDAO();
			users = db.getFilteredUsers(username, email, role, sortTimestamp);
		} catch (Exception e) {
			System.out.println("Error fetching filtered services: " + e);
		}
		return users;
	}

	@RequestMapping(path = "/createAdmin", consumes = "application/json", method = RequestMethod.POST)
	public String createAdmin(@RequestBody User user) {
		try {
			UserDAO db = new UserDAO();

			// Validate Required Fields
			if (user.getUsername() == null || user.getUsername().isEmpty() || user.getEmail() == null
					|| user.getEmail().isEmpty() || user.getPassword() == null || user.getPassword().isEmpty()) {
				return "Error: Missing required fields.";
			}

			// Check if Email Already Exists
			if (db.emailExists(user.getEmail())) {
				return "Error: Email already in use.";
			}
			// Insert into Database
			int result = db.insertUser(user);
			if (result > 0) {
				return "Success: Admin Created!";
			} else {
				return "Error: Failed to create admin.";
			}
		} catch (Exception e) {
			System.out.println("Error creating admin: " + e);
			return "Error: Internal Server Error.";
		}
	}

	@RequestMapping(path = "/updateUser/{uid}", consumes = "application/json", method = RequestMethod.PUT)
	public String updateUser(@PathVariable int uid, @RequestBody User user) {
		try {
			UserDAO db = new UserDAO();

			// Validate Required Fields
			if (user.getUsername() == null || user.getUsername().isEmpty() || user.getEmail() == null
					|| user.getEmail().isEmpty()) {
				return "Error: Missing required fields.";
			}

			// Check if Email Already Exists
			if (db.emailExists(user.getEmail())) {
				return "Error: Email already in use.";
			}
			// Insert into Database
			int result = db.updateUser(uid, user);
			if (result > 0) {
				return "Success: User Updated";
			} else {
				return "Error: Failed to update user.";
			}
		} catch (Exception e) {
			System.out.println("Error updating user: " + e);
			return "Error: Internal Server Error.";
		}
	}

	@RequestMapping(method = RequestMethod.GET, path = "/getTopCustomers")
	public List<Map<String, Object>> getTopCustomers() {
		try {
			UserDAO db = new UserDAO();
			return db.getTopCustomers();
		} catch (Exception e) {
			System.out.println("Error fetching top customers: " + e);
			return new ArrayList<>();
		}
	}
}