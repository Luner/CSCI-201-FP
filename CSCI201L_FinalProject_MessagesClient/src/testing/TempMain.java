package testing;

import listener.ChatClient;

public class TempMain {
	
	//Just creates a new client with the desired port
	public static void main(String [] args) {
		new ChatClient("localhost", 6789);
	}
}
