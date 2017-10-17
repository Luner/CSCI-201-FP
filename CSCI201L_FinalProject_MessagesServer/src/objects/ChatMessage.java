package objects;

public class ChatMessage extends Message {
	public static final long serialVersionUID = 1;
	
	private String name;
	private String message;
	public ChatMessage(String name, String message) {
		this.name = name;
		this.message = message;
	}
	
	public String getName() {
		return name;
	}
	
	public String getMessage() {
		return message;
	}
}