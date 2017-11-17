package objects.message;


//A StringMessage is a Message that only contains a string
public class ChatStringMessage extends Message{
	
	private static final long serialVersionUID = 1L;
	//The contents of the message
	private String message;
	private Integer chatID;
	
	//Constructor: Sets the Message
	public ChatStringMessage(String message, Integer chatId) {
		this.message = message;
		this.chatID = chatId;
	}
	
	//getter for the message
	public String getMessage() {
		return this.message;
	}
	
	//getter for the chatId
		public Integer getChatID() {
			return this.chatID;
		}
	
	public String toString() { 
	    return "StringMessage| message: " + this.message;
	}
}
