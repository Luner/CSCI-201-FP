package client.listener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import objects.ChatMessage;
import objects.Message;
import objects.StringMessage;

public class ChatClient extends Thread {

	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private int uid;

	public ChatClient(String hostname, int port) {
		Socket s = null;
		Scanner scan = null;
		uid = -1;
		

		try {
			//Attempts to connect to the Socket
			s = new Socket(hostname, port);	
			
			/*If successful, will create ObjectStreams to allow for the sending of 
			  objects to and from the server*/
			oos = new ObjectOutputStream(s.getOutputStream()); 
			ois = new ObjectInputStream(s.getInputStream());
			
			//Starts the thread and calls the run() method
			this.start();
			
			//Handles the Sending of information
			//May want to call a separate function in future
			
			//Creates a new scanner to receive information from the console
			scan = new Scanner(System.in);
			
			/*//////////////////////////////////////////////////////////
			 *TODO : MUST UPDATE UID AND PREFORM A USER VERIFICATION FIRST  
			 *///////////////////////////////////////////////////////////
			
			//An infinite loop that will constantly look for a line from the console
			while(true) {
				String line = scan.nextLine();
				
				//Creates a ChatMessage with the input
				Message message = new ChatMessage(uid, line);
				
				//Sends the ChatMessage Object to the server
				oos.writeObject(message);
				oos.flush();
			}
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		} finally {
			//Checks if the sockets and the scanner still exist and closes them
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
	
	
	//Handles the receiving of information
	public void run() {
		try {

			/*//////////////////////////////////////////////////////////
			 *TODO : MUST BE PREPAIRED TO RECIEVE A MESSAGE WITH UID TO SET  
			 *///////////////////////////////////////////////////////////
		
			
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
	
	
}
