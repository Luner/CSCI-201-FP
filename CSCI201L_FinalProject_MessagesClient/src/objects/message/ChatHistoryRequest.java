package objects.message;

public class ChatHistoryRequest extends Message{
	private static final long serialVersionUID = 1L;
	int value;
	public ChatHistoryRequest(int cid) {
		value = cid;
	}
	public int getCid() {
		return value;
	}
}
