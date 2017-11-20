package objects.message;

import objects.ClientConversation;

import java.util.ArrayList;

public class ConversationsMessage extends Message {
	private static final long serialVersionUID = 1L;

	private ArrayList<ClientConversation> chats;

	public ConversationsMessage(ArrayList<ClientConversation> chats) {
		this.chats = chats;
	}

	public ArrayList<ClientConversation> getChats() {
		return chats;
	}
}
