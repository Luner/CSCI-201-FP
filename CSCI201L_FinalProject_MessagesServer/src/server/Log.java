package server;

import objects.message.Message;

public class Log {
	public static void sent(Message message) {
		System.out.println("LOG:: Sent: " + message.toString());
	}
	
	public static void recieved(Message message) {
		System.out.println("LOG:: Recieved: " + message.toString());
	}
}