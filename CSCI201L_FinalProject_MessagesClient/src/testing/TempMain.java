package testing;

import client.listener.ChatClient;

public class TempMain {
	
	public static void main(String [] args) {
		new ChatClient("localhost", 6789);
	}
	
}