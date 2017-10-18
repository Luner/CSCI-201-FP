package objects;


//As of right now meant to only be sent from the Client to the Server
public class ChatMessage extends Message{
	
	private static final long serialVersionUID = 1L;
	
	//The UserID
	private int uid;
	
	//The Message
	private String message;
	
	//Constructor for ChatMessages (Just sets variables)
	public ChatMessage(int uid, String message) {
		this.uid = uid;
		this.message = message;
	}
	
	//Getter for Message
	public String getMessage() {
		return this.message;
	}
	
	//Getter for Uid
	public int getUid() {
		return uid;
	}
}