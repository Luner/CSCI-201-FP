package objects;

import objects.message.ChatMessage;
import objects.message.Message;

import java.util.ArrayList;

public class Conversation {
	private Integer conversationID;
	private String conversationName;
	private ArrayList<User> users;
	private ArrayList<User> activeUsers;

	// requires list of users and a Conversation id
	
	public Conversation(ArrayList<User> users, Integer conversationID, String conversationName) {
		this.conversationID = conversationID;
		this.users = users;
		activeUsers = new ArrayList<>();
		this.conversationName = conversationName;
	}
	
	public String getName() {
		return this.conversationName;
	}

	public void userOnline(User user) {
		if (users.contains(user)) {
			activeUsers.add(user);
		}
	}

	public void sendMessageToConversation(Message message) {
		for (User user : activeUsers) {
			user.getServerThread().sendChatStringMessage((ChatMessage) message);
		}
	}

	public ArrayList<User> getUsers() {
		return users;
	}

	public Integer getConversationID() {
		return this.conversationID;
	}

	public boolean addActiveUser(User user) {
		if (users.contains(user)) {
			if(!activeUsers.contains(user)) {
				activeUsers.add(user);
				return true;
			}
		}
		return false;
	}

	public boolean hasUser(User user) {
		if (users.contains(user)) {
			return true;
		}
		return false;
	}

	public void addUser(User user) {
		if (users.contains(user)) {
			return;
		}
		users.add(user);
	}

	public void removeActiveUser(User user) {
		if (users.contains(user)) {
			activeUsers.remove(user);
		}
	}
}
