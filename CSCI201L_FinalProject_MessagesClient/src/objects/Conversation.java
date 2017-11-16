package objects;

import java.util.ArrayList;

import objects.message.ChatMessage;
import objects.message.Message;

public class Conversation {
	Integer conversationID;
	String conversationName;
	ArrayList<User> users;
	ArrayList<User> activeUsers;
	
	public Conversation(ArrayList<User> users) {
		this.users = users;
		activeUsers = new ArrayList<User>();
	}
	
	public void userOnline(User user) {
		if(users.contains(user)) {
			activeUsers.add(user);
		}
	}
	
	public void sendMessageToConversation(Message message) {
		for(User user : activeUsers) {
			user.getServerThread().sendStringMessage((ChatMessage) message);
		}
	}
	
	public Integer getConversationID() {
		return this.conversationID;
	}
	
	public void addActiveUser(User user) {
		activeUsers.add(user);
	}
}
