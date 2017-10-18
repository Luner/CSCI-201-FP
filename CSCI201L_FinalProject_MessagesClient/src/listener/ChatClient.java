package listener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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

			
			//Calls the sender, which handles the sending of data from the client to the server
			//sender();
			
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}	
	}
	
	public void startChatThread() {
		//BEGIN CHATTING
		
		//Starts the thread and calls the run() method (receiver)
		this.start();
	}
	
	//handles the sending of information to the Server
	public void send(String text) {
	
		//And send a ChatMessage to the Server
		
		try {	
			//Creates a ChatMessage with the input
			Message message = new ChatMessage(uid, text);
			
			//Sends the ChatMessage Object to the server
			oos.writeObject(message);
			oos.flush();
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}	
		
	} 
	
	public boolean login(String username, String password) {
		try {
			
			//Creates a VerificationMessage with the username and password inputs
			Message message = new VerificationMessage(username, password);
			
			//Sends the VerificationMessage Object to the server
			oos.writeObject(message);
			oos.flush();
			
			boolean response = verificationResponse();
			System.out.println("response: " + response);
			return response;
			
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
			System.out.println("checking response: " + message);

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
					//add to 
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
