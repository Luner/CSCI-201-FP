package server;

import objects.message.Message;

public class Log {
	public static void sent(Message message) {
		System.out.println("LOG:: Sent: " + message.toString());
	}

	public static void recieved(Message message) {
		System.out.println("LOG:: Recieved: " + message.toString());
	}

	public static void command(Message message) {
		System.out.println("LOG:: Command: " + message.toString());
	}

	public static void log(String message) {
		System.out.println("LOG:: " + message);
	}
}