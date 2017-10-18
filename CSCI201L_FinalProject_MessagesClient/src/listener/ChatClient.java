package listener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import objects.message.ChatMessage;
import objects.message.Message;
import objects.message.StringMessage;
import objects.message.VerificationMessage;
import objects.message.VerificationResponseMessage;

public class ChatClient extends Thread {

	public static final Integer TIMEOUT_SECONDS = 10;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private int uid;
	Socket s;
	
	public ChatClient(String hostname, int port) {
		s = null;
		uid = -1;
		try {
			//Attempts to connect to the Socket
			s = new Socket(hostname, port);	
			//Creates a new scanner to receive information from the console
				
			/*If successful, will create ObjectStreams to allow for the sending of 
			  objects to and from the server*/
			oos = new ObjectOutputStream(s.getOutputStream()); 
			ois = new ObjectInputStream(s.getInputStream());
						
			//BEGIN CHATTING
	
			//Starts the thread and calls the run() method (receiver)
			//this.start();
			
			//Calls the sender, which handles the sending of data from the client to the server
			//sender();
			
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}	
	}
	
	//handles the sending of information to the Server
	/*public void sender() {

		
		//An infinite loop that will constantly look for a line from the console
		//And send a ChatMessage to the Server
		while(true) {
			try {
				String line = scan.nextLine();
				
				//Creates a ChatMessage with the input
				Message message = new ChatMessage(uid, line);
				
				//Sends the ChatMessage Object to the server
				oos.writeObject(message);
				oos.flush();
			} catch (IOException ioe) {
				System.out.println("ioe: " + ioe.getMessage());
			}	
		}
	} */
	
	public boolean login(String username, String password) {
		try {
			
			//Creates a VerificationMessage with the username and password inputs
			Message message = new VerificationMessage(username, password);
			
			//Sends the VerificationMessage Object to the server
			oos.writeObject(message);
			oos.flush();
			
			return verificationResponse();
			
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
		return false;
	}
	

	private boolean verificationResponse() {
		try {

			//Receives the object
			Object message = ois.readObject();
				
			//checks if the object is an instance of VerificationResponseMessage
			//If it is and user exists, set uid and return true
			if(message instanceof VerificationResponseMessage) {
				if(((VerificationResponseMessage) message).isVerified()) {
					uid = ((VerificationResponseMessage) message).getUid();
					return true;
				}
				System.out.println("verification failed");
				System.out.println(""); //used for formatting
				return false;
			} else {
				System.out.println("Exception in ChatClient verificationResponse(): Expecting VerificationResponseMessage");
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("cnfe: " + cnfe.getMessage());
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
		
	System.out.println("verification missed");
	return false;
}
	
	
	//Handles the receiving of information
	public void run() {
		try {

			//Loop consistently looking for an object to be sent from the server
			while(true) {
				//Receives the object
				Object message = ois.readObject();
				
				//checks if the object is an instance of StringMessage and prints out
				if(message instanceof StringMessage) {
					System.out.println(((StringMessage) message).getMessage());
				} else {
					System.out.println("Exception in ChatClient run(): Expecting StringMessage");
				}
				
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("cnfe: " + cnfe.getMessage());
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
	
	public void cleanUp() {
		try {
			if (s != null) {
				s.close();
			}
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
	
	
}