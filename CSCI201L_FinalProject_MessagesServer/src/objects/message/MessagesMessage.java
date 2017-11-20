package objects.message;

import java.util.ArrayList;
import java.util.Map;

//A message meant to be sent to the server for verification
public class MessagesMessage extends Message {
	private static final long serialVersionUID = 1L;

	private Map<Integer, ArrayList<String>> messages;

	// Constructor: sets all of the variable
	public MessagesMessage(Map<Integer, ArrayList<String>> messages) {
		this.messages = messages;
	}
	
	public Map<Integer, ArrayList<String>> getMessage(){
		return messages;
	}
}
