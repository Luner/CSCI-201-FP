package objects;

import server.ServerThread;

public class User {

	// Represents the username for the user
	private String username;

	// Represents the password for the user
	private String password;

	// User_id: used to identify a user
	private int uid;

	private String type;

	// ServerThread if user is active
	private transient ServerThread st;

	// Constructor: Initializes all variables
	public User(String username, String password, int uid) {
		this.username = username;
		this.password = password;
		this.uid = uid;
	}

	public void logOn(ServerThread st) {
		this.st = st;

	}

	public void logOff() {
		this.st = null;
	}

	public ServerThread getServerThread() {
		return this.st;
	}

	public String getType() {
		return this.type;
	}

	// Getter for username
	public String getUsername() {
		return this.username;
	}

	// Getter for password
	public String getPassword() {
		return this.password;
	}

	// Getter for user_id
	public int getUid() {
		return this.uid;
	}

	// Checks if an object is equal (by checking if each variable is the same)
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof User)) {
			return false;
		}

		User user = (User) o;

		return this.username.equals(user.getUsername()) && this.password.equals(user.getPassword()) && this.uid == user.getUid();
	}

	// Will verify a user based on a given username and password
	// returns true if 'verified'
	public boolean verify(String username, String password) {

		return this.username.equals(username) && this.password.equals(password);
	}
}
