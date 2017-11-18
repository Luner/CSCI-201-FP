package objects.message;

import java.util.ArrayList;

public class CreateConversationMessage extends Message {
	private static final long serialVersionUID = 1L;

	//User who created the conversation
	private String user0;
	
	//User who supposed to enter the chat
	private String user1;
	private String user2;
	private String user3;
	private String user4;

	public CreateConversationMessage(String user0, String user1, String user2, String user3, String user4) {
		this.user0 = user0;
		this.user1 = user1;
		this.user2 = user2;
		this.user3 = user3;
		this.user4 = user4;
	}

	public ArrayList<String> getUsers(){
		ArrayList<String> result = new ArrayList<String>();
		result.add(user0);
		result.add(user1);
		result.add(user2);
		result.add(user3);
		result.add(user4);
		return result;
	}
}
