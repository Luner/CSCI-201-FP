package objects.message;

import java.util.Queue;

public class ChatHistoryReply extends Message{

	private static final long serialVersionUID = 1L;
	private Queue<ChatStringMessage> history;
	public ChatHistoryReply(Queue<ChatStringMessage> h) {
		history = h;
	}
	public Queue<ChatStringMessage> getHistory(){
		return history;
	}

}
