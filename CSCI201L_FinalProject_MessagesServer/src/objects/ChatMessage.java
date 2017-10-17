package objects;


public class ChatMessage extends Message{
	
	private static final long serialVersionUID = 1L;
	
	private int uid;
	private String message;
	
	public ChatMessage(int uid, String message) {
		this.uid = uid;
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public int getUid() {
		return uid;
	}
}