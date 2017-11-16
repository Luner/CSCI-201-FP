package objects.message;

import java.util.ArrayList;

import objects.Conversation;

public class ConversationsMessage {
	private ArrayList<Conversation> chats;
	
	public ConversationsMessage(ArrayList<Conversation> chats) {
		this.chats = chats;
	}
	
	public ArrayList<Conversation> getChats(){
		return chats;
	}
}
