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
	private String instance;
	private Connection conn;

	public Database(String username, String password, String database, String instance) {
		this.username = username;
		this.password = password;
		this.database = database;
		this.instance = instance;
		if (connect()) {
			System.out.println("Database initialized.");
		} else {
			System.out.println("Database connection error.");
		}
	}

	public boolean connect() {
		try {
			String jdbcUrl = String.format("jdbc:mysql://google/%s?cloudSqlInstance=%s&"
					+ "socketFactory=com.google.cloud.sql.mysql.SocketFactory", database, instance);
			conn = DriverManager.getConnection(jdbcUrl, username, password);
			return true;
		} catch (SQLException sqle) {
			return false;
		}
	}

	public void registerUser(String username, String password) {
		String insertQuery = "INSERT users SET Username = ? , Password = ?";
		try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
			ps.setString(1, username);
			ps.setString(2, password);
			ps.executeUpdate();
		} catch (SQLException sqle) {
			System.out.println("Failed to create user with username: " + username);
		}
	}

	public int loginUser(String username, String password) {
		String selectQuery = "SELECT ID FROM users WHERE Username = ? , Password = ?";
		try (PreparedStatement ps = conn.prepareStatement(selectQuery)) {
			ps.setString(1, username);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();
			rs.next();
			return rs.getInt(1);
		} catch (SQLException sqle) {
			System.out.println("Failed to login user with username: " + username);
			return -1;
		}
	}

	public void updateUser(int userID) {
		String updateQuery = "UPDATE users";
		try (PreparedStatement ps = conn.prepareStatement(updateQuery)) {

		} catch (SQLException sqle) {
			System.out.println("Failed to update user with UID: " + userID);
		}
	}
}