package objects;


//A StringMessage is a Message that only contains a string
public class StringMessage {
	
	//The contents of the message
	private String message;
	
	//Constructor: Sets the Message
	public StringMessage(String message) {
		this.message = message;
	}
	
	//getter for the message
	public String getMessage() {
		return this.message;
	}
}
