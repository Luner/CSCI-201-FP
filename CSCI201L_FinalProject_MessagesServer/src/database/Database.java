package database;

import objects.Conversation;
import objects.DataContainer;
import objects.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
			System.out.println("Database initialized.\n");
		} else {
			System.out.println("Database connection error.");
			System.exit(0);
		}
	}

	public boolean connect() {
		try {
			conn = DriverManager.getConnection("jdbc:mysql://" + hostname + ":" + port + "/" + database
					+ "?useSSL=false&user=" + username + "&password=" + password);
			return true;
		} catch (SQLException sqle) {
			return false;
		}
	}

	public void registerUser(String username, String password) {
		String insertQuery = "INSERT users SET Username = ?, UserPassword = ?";
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

	public HashMap<Integer, Conversation> getConversations(DataContainer dc) {
		String selectQuery = "SELECT * FROM CSCI201.conversations";
		HashMap<Integer, Conversation> conversationMap = new HashMap<Integer, Conversation>();
		try {
			PreparedStatement ps = conn.prepareStatement(selectQuery);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int conversationID = rs.getInt(1);
				boolean active = rs.getBoolean(2);
				String conversationName = rs.getString(3);
				ArrayList<User> conversationUsers = new ArrayList<User>();
				Conversation conversation = new Conversation(conversationUsers, conversationID, conversationName);
				// Set active status and topic here!
				conversationMap.put(conversationID, conversation);
			}
			Iterator it = conversationMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				selectQuery = "SELECT UserID FROM CSCI201.conversation_members WHERE ConversationID = ?";
				ps = conn.prepareStatement(selectQuery);
				ps.setInt(1, ((Conversation) pair.getValue()).getConversationID());
				rs = ps.executeQuery();
				while (rs.next()) {
					((Conversation) pair.getValue()).getUsers().add(dc.findUserByUid(rs.getInt(1)));
				}
			}
			return conversationMap;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			System.out.println("Failed to fetch conversations.");
			sqle.printStackTrace();
			return conversationMap;
		}
	}

	public void createConversation(ArrayList<User> users, String topic, int conversationID) {
		String insertQuery = "INSERT conversations SET Topic = ?, ConversationID = ?;";
		try {
			PreparedStatement ps = conn.prepareStatement(insertQuery);
			ps.setString(1, topic);
			ps.setInt(2, conversationID);
			ps.execute();
			for (User u : users) {
				System.out.println("User: " + u.getUsername() + " with id: " + u.getUid() + " adding to converstaion "
						+ conversationID);
				insertQuery = "INSERT conversation_members SET ConversationID = ?, UserID = ?;";
				ps = conn.prepareStatement(insertQuery);
				ps.setInt(1, conversationID);
				ps.setInt(2, u.getUid());
				ps.execute();
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			System.out.println("Failed to create conversation.");
		}
	}

	public ResultSet getMessages(int conversationID) {
		String selectQuery = "SELECT * from messages WHERE ConversationID = ?";
		ResultSet rs = null;
		try (PreparedStatement ps = conn.prepareStatement(selectQuery)) {
			ps.setInt(1, conversationID);
			rs = ps.executeQuery();
			while (rs.next()) {
				int messageID = rs.getInt(1);
				int userID = rs.getInt(3);
				String message = rs.getString(4);
				boolean file = rs.getBoolean(5);
			}
		} catch (SQLException e) {
			System.out.println("Failed to retrieve messages for conversation.");
		}
		return rs;
	}

	public Map<Integer, ArrayList<String>> getMessagesMap(DataContainer dc) {
		Map<Integer, ArrayList<String>> chatHistory = new HashMap<Integer, ArrayList<String>>();
		String selectQuery = "SELECT ConversationID FROM CSCI201.conversations";
		try (PreparedStatement ps = conn.prepareStatement(selectQuery)) {
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int conversationID = rs.getInt(1);
				chatHistory.put(conversationID, new ArrayList<String>());
			}
		} catch (SQLException e) {
			System.out.println("Failed to retrieve conversations to generate message list.");
		}
		selectQuery = "SELECT ConversationID,UserID,Message from messages;";
		ResultSet rs = null;
		try (PreparedStatement ps = conn.prepareStatement(selectQuery)) {
			rs = ps.executeQuery();
			while (rs.next()) {
				int conversationID = rs.getInt(1);
				int userID = rs.getInt(2);
				String message = rs.getString(3);
				chatHistory.get(conversationID).add(dc.findUserByUid(userID).getUsername() + ": " + message);
			}
		} catch (SQLException e) {
			System.out.println("Failed to retrieve message map.");
		}
		return chatHistory;
	}

	public void addMessage(int chatID, int userID, String message) {
		String insertQuery = "INSERT messages SET ConversationID = ?, UserID = ?, Message = ?;";
		try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
			ps.setInt(1, chatID);
			ps.setInt(2, userID);
			ps.setString(3, message);
			ps.execute();
		} catch (SQLException e) {
			System.out.println("Failed to insert message: " + message);
		}
	}
	
	public void addUserToConversation(User u, Conversation c) {
		String insertQuery = "INSERT conversation_members SET ConversationID = ?, UserID = ?;";
		try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
			ps.setInt(1, u.getUid());
			ps.setInt(2, u.getUid());
			ps.execute();
		} catch (SQLException e) {
			System.out.println("Failed to add user " + u.getUsername() + " to a conversation.");
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