package objects;


public class ChatMessage extends Message{
	
	private static final long serialVersionUID = 1L;
	
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