package objects;

import java.io.Serializable;

public class ClientConversation implements Serializable {
	private static final long serialVersionUID = 1L;
	Integer conversationID;
	String conversationName;

	public ClientConversation(Integer conversationID, String conversationName) {
		this.conversationID = conversationID;
		this.conversationName = conversationName;
	}

	public void userOnline(User user) {
	}

	public Integer getConversationID() {
		return this.conversationID;
	}
	
	public String getName() {
		return conversationName;
	}
}
