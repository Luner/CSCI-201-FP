package client.listener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import objects.ChatMessage;
import objects.Message;
import objects.StringMessage;
import objects.VerificationMessage;
import objects.VerificationResponseMessage;

public class ChatClient extends Thread {

	public static final Integer TIMEOUT_SECONDS = 10;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private int uid;
	Socket s;
	Scanner scan;

	public ChatClient(String hostname, int port) {
		s = null;
		scan = null;
		uid = -1;
		try {
			//Attempts to connect to the Socket
			s = new Socket(hostname, port);	
				
			/*If successful, will create ObjectStreams to allow for the sending of 
			  objects to and from the server*/
			oos = new ObjectOutputStream(s.getOutputStream()); 
			ois = new ObjectInputStream(s.getInputStream());
			
			
			//first get the user to login and then set the uid
			login();
						
			//BEGIN CHATTING
	
			//Starts the thread and calls the run() method (receiver)
			this.start();
			
			//Calls the sender, which handles the sending of data from the client to the server
			sender();
			
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}	
	}
	
	//handles the sending of information to the Server
	public void sender() {

		//Creates a new scanner to receive information from the console
		scan = new Scanner(System.in);
		
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
	} 
	
	private void login() {
		boolean verified = false;
		
		while(!verified) {
			try {
				String usernameInput;
				String passwordInput;

				//Ask for username and password
				System.out.println("Please enter username: ");
				usernameInput = scan.nextLine();
				System.out.println("Please enter password: ");
				passwordInput = scan.nextLine();
				
				//Creates a VerificationMessage with the username and password inputs
				Message message = new VerificationMessage(usernameInput, passwordInput);
				
				//Sends the VerificationMessage Object to the server
				oos.writeObject(message);
				oos.flush();
				
				verified = verificationResponse();
				
			} catch (IOException ioe) {
				System.out.println("ioe: " + ioe.getMessage());
			}	
		}
	}
	
	private boolean verificationResponse() {
		for(int i = 0; i < TIMEOUT_SECONDS; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
					System.out.println("\n"); //used for formatting
					return false;
				} else {
					System.out.println("Exception in ChatClient verificationResponse(): Expecting VerificationResponseMessage");
				}
			} catch (ClassNotFoundException cnfe) {
				System.out.println("cnfe: " + cnfe.getMessage());
			} catch (IOException ioe) {
				System.out.println("ioe: " + ioe.getMessage());
			}
		}
	System.out.println("verification timed out!");
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
					System.out.println(message + ": " +((StringMessage) message).getMessage());
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
			if (scan != null) {
				scan.close();
			}
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
	
	
}
