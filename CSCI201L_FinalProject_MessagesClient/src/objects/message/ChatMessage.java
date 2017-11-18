package objects.message;

//As of right now meant to only be sent from the Client to the Server
public class ChatMessage extends Message {

	private static final long serialVersionUID = 1L;

	// The UserID
	private int uid;

	// Chat ID
	private int cid;

	// The Message
	private String message;

	// Constructor for ChatMessages (Just sets variables)
	public ChatMessage(int uid, int cid, String message) {
		this.uid = uid;
		this.cid = cid;
		this.message = message;
	}

	// Getter for Message
	public String getMessage() {
		return this.message;
	}

	public int getCid() {
		return cid;
	}

	// Getter for Uid
	public int getUid() {
		return uid;
	}

	public String toString() {
		return "ChatMessage| uid: " + this.uid + ", message: " + this.message;

	}
}