package objects.message;

import java.util.ArrayDeque;

public class ChatHistoryReply extends Message{

	private static final long serialVersionUID = 1L;
	private ArrayDeque<ChatStringMessage> history;
	public ChatHistoryReply(ArrayDeque<ChatStringMessage> h) {
		history = h;
	}
	public ArrayDeque<ChatStringMessage> getHistory(){
		return history;
	}

}
