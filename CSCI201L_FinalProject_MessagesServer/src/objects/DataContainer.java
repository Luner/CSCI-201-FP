package objects;

import java.util.ArrayList;

public class DataContainer {
	ArrayList<User> users;
	
	public DataContainer(ArrayList<User> users) {
		this.users = users;
	}
	
	public ArrayList<User> getUsers(){
		return this.users;
	}
	
	public User findUserByUid(int uid) {
		for(User user : users) {
			if(user.getUid() == uid) {
				return user;
			}
		}
		return null;
	}
}
