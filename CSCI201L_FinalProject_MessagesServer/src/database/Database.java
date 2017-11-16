package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import objects.User;

public class Database {
	private String hostname;
	private int port;
	private String username;
	private String password;
	private String database;
	private Connection conn;

	public Database(String hostname, int port, String username, String password, String database) {
		this.hostname = hostname;
		this.port = port;
		this.username = username;
		this.password = password;
		this.database = database;
		if (connect()) {
			System.out.println("Database initialized.");
		} else {
			System.out.println("Database connection error.");
		}
	}

	public boolean connect() {
		try {
			conn = DriverManager.getConnection("jdbc:mysql://" + hostname + ":" + port + "/" + database
					+ "?useSSL=false&user=" + username + "&password=" + password);
			return true;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			return false;
		}
	}

	public void registerUser(String username, String password) {
		String insertQuery = "INSERT users SET Username = ? , UserPassword = ?";
		try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
			ps.setString(1, username);
			ps.setString(2, password);
			ps.executeUpdate();
		} catch (SQLException sqle) {
			System.out.println("Failed to create user with username: " + username);
		}
	}

	public int loginUser(String username, String password) {
		String selectQuery = "SELECT UserID FROM CSCI201.users WHERE Username = ? AND UserPassword = ?";
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

	public ArrayList<User> getUsers() {
		String selectQuery = "SELECT * FROM CSCI201.users";
		ArrayList<User> foundUsers = new ArrayList<User>();
		try (PreparedStatement ps = conn.prepareStatement(selectQuery)) {
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int userID = rs.getInt(1);
				String username = rs.getString(2);
				String password = rs.getString(6);
				foundUsers.add(new User(username, password, userID));
			}
			return foundUsers;
		} catch (SQLException sqle) {
			System.out.println("Failed to fetch users.");
			return foundUsers;
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