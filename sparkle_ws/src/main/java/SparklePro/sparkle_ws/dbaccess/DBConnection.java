package SparklePro.sparkle_ws.dbaccess;

import java.sql.*;

public class DBConnection {

	public static Connection getConnection() throws Exception {

		// Step1: Load JDBC Driver
       		Class.forName("org.postgresql.Driver");

		// Step 2: Define Connection URL
		String connURL = "jdbc:postgresql://ep-odd-thunder-a8vkdz9p-pooler.eastus2.azure.neon.tech:5432/neondb?user=neondb_owner&password=npg_Mgiv6EFbrO4P&sslmode=require";

		// Step 3: Establish connection to URL
		Connection conn = DriverManager.getConnection(connURL);
		
		return conn;
	}
}
