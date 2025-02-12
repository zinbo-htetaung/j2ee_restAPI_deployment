package SparklePro.sparkle_ws.dbaccess;

import java.sql.*;

public class DBConnection {

	public static Connection getConnection() throws Exception {

		// Step1: Load JDBC Driver
		Class.forName("com.mysql.jdbc.Driver");

		// Step 2: Define Connection URL
		String connURL = "jdbc:mysql://localhost/cleaning_service?user=root&password=password&serverTimezone=UTC";

		// Step 3: Establish connection to URL
		Connection conn = DriverManager.getConnection(connURL);
		
		return conn;
	}
}
