package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
	private String username;
	private String password;
	private String database;
	private Connection conn;

	public Database(String username, String password, String database) {
		this.username = username;
		this.password = password;
		this.database = database;
	}

	public boolean connect() {
		try {
			conn = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/" + database + "?user=" + username + "&password=" + password);
			return true;
		} catch (SQLException sqle) {
			System.out.println("Unable to connect to database with specified paramters.");
			return false;
		}
	}

	public void registerUser(String username, String password) {
		String insertQuery = "INSERT users SET username = ? , password = ?";
		try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
			ps.setString(1, username);
			ps.setString(2, password);
			ps.executeUpdate();
		} catch (SQLException sqle) {
			System.out.println("Failed to create user with username: " + username);
		}
	}

	public void loginUser(String uername, String password) {
		
	}
}