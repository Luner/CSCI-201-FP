package objects;

import java.util.ArrayList;

//A Container class that will contain and manage all of the data in use when running
public class DataContainer {

	// An ArrayList of all registered users
	private ArrayList<User> users;

	public DataContainer(ArrayList<User> users) {
		this.users = users;
	}

	// An Array of Users
	public ArrayList<User> getUsers() {
		return this.users;
	}

	// Returns a user given their user_id
	public User findUserByUid(int uid) {
		for (User user : users) {
			if (user.getUid() == uid) {
				return user;
			}
		}
		return null;
	}

	public boolean addUser(User user) {
		for (User user1 : users) {
			if (user.getUsername().equals(user1.getUsername())) {
				return false;
			}
		}
		users.add(user);
		return true;
	}

	public Integer getNextID() {
		return users.get(users.size() - 1).getUid() + 1;
	}

	public User findUserByUsername(String username) {
		for (User user : users) {
			if (user.getUsername().equals(username)) {
				return user;
			}
		}
		return null;
	}
}
