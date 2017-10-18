package objects;

import java.util.ArrayList;

//A Container class that will contain and manage all of the data in use when running
public class DataContainer {
	
	//An ArrayList of all registered users
	ArrayList<User> users;
	
	public DataContainer(ArrayList<User> users) {
		this.users = users;
	}
	
	//An Array of Users
	public ArrayList<User> getUsers(){
		return this.users;
	}
	
	//Returns a user given their user_id
	public User findUserByUid(int uid) {
		for(User user : users) {
			if(user.getUid() == uid) {
				return user;
			}
		}
		return null;
	}
}
