package objects;

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Queue;

import objects.message.ChatMessage;
import objects.message.ChatStringMessage;
import objects.message.Message;

public class Conversation {
	Integer conversationID;
	String conversationName;
	ArrayList<User> users;
	ArrayList<User> activeUsers;
	ArrayDeque<ChatStringMessage> chatHistory;

	// requires list of users and a Conversation id
	public Conversation(ArrayList<User> users, Integer conversationID) {
		this.conversationID = conversationID;
		this.users = users;
		this.conversationName = "";
		this.chatHistory = new ArrayDeque<ChatStringMessage>();
		activeUsers = new ArrayList<User>();
	}
	
	public Conversation(ArrayList<User> users, Integer conversationID, String conversationName) {
		this.conversationID = conversationID;
		this.users = users;
		activeUsers = new ArrayList<User>();
		this.conversationName = conversationName;
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

	public Integer getConversationID() {
		return this.conversationID;
	}

	public boolean addActiveUser(User user) {
		if (users.contains(user)) {
			activeUsers.add(user);
			return true;
		}
		return false;
	}

	public boolean hasUser(User user) {
		if (users.contains(user)) {
			return true;
		}
		return false;
	}
	
	public void addMessage(ChatStringMessage cm) {
		chatHistory.add(cm);
	}
	public ArrayDeque<ChatStringMessage> getHistory(){
		return chatHistory;
	}
}
