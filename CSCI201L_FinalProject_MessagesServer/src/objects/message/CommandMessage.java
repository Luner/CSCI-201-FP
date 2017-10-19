package objects.message;


//As of right now meant to only be sent from the Client to the Server
public class CommandMessage extends Message{
	
	private static final long serialVersionUID = 1L;
	
	//The UserID
	private int uid;
	
	//The Message
	private String command;
	
	//Constructor for ChatMessages (Just sets variables)
	public CommandMessage(int uid, String message) {
		this.uid = uid;
		this.command = message;
	}
	
	//Getter for Message
	public String getCommand() {
		return this.command;
	}
	
	//Getter for Uid
	public int getUid() {
		return uid;
	}
	
	public String toString() { 
	    return "CommandMessage| uid: " + this.uid + ", Command: " + this.command;
	    
	} 
}