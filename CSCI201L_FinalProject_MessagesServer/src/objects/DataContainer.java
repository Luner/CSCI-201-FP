package objects;

import java.util.ArrayList;

//A Container class that will contain and manage all of the data in use when running
public class DataContainer {

	// An ArrayList of all registered users
	ArrayList<User> users;

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

	public boolean isAdmin(int uid) {
		User user = findUserByUid(uid);
		if (user.getType().equals("Admin")) {
			return true;
		}
		return false;
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
		Integer id = users.get(users.size() - 1).getUid() + 1;
		return id;
	}
}
