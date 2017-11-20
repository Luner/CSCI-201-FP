package objects.message;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

//A message meant to be sent to the server for verification
public class MessagesMessage extends Message {
	private static final long serialVersionUID = 1L;

	private Map<Integer, ArrayList<String>> messages;

	// Constructor: sets all of the variable
	public MessagesMessage(Map<Integer, ArrayList<String>> messages) {
		this.messages = messages;
	}
	
	public Map<Integer, ArrayList<String>> getMessage(){
		for (Entry<Integer, ArrayList<String>> entry : messages.entrySet()) {
			for(String s : entry.getValue()) {
				System.out.println("Conversation: " + entry.getKey() + " Message: " + s);
			}
		}
		return messages;
	}
}
