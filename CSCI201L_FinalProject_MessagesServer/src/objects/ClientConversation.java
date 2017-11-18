package objects;

import java.io.Serializable;

public class ClientConversation implements Serializable {
	private static final long serialVersionUID = 1L;
	Integer conversationID;
	String conversationName;

	public ClientConversation(Integer conversationID) {
		this.conversationID = conversationID;
	}

	public void userOnline(User user) {
	}

	public Integer getConversationID() {
		return this.conversationID;
	}
}
