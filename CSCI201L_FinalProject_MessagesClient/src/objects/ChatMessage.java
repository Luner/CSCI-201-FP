package objects;


public class ChatMessage extends Message{
	
	private static final long serialVersionUID = 1L;
	
	private int uid;
	
	public ChatMessage(int uid, String message) {
		this.uid = uid;
	}
	
	public int getUid() {
		return uid;
	}
}