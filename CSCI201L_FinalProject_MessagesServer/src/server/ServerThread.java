package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import objects.User;
import objects.message.ChatMessage;
import objects.message.Message;
import objects.message.StringMessage;
import objects.message.VerificationMessage;
import objects.message.VerificationResponseMessage;

public class ServerThread extends Thread {

	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Server cs;
	String username; 
	int uid;
	
	public ServerThread(Socket s, Server cs) {
		//Initialize the Object streams for the socket
		try {
			this.cs = cs;
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			this.start();			
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
	
	private void login() {
		try {
			//loops until connected user is verified
			while(true) {
				Object message = ois.readObject();
				if(message instanceof VerificationMessage) {
					//if the message is a VerificationMessagessage, Log the message received
					Log.recieved((Message) message);
					
					//Check the information sent against every user that exists
					for(User user : cs.getData().getUsers()) {
						if(user.verify(((VerificationMessage) message).getUsername(), ((VerificationMessage) message).getPassword())) {
							this.username = user.getUsername();
							this.uid = user.getUid();
							
							//Send VerificationResponseMessage
							VerificationResponseMessage response;
							
							//Send a verificationResponseMessage with true and the corresponding User ID
							response = new VerificationResponseMessage(true, uid);
							try {
								oos.writeObject(response);
								oos.flush();
								Log.sent(response);
								return;
							} catch (IOException ioe) {
								System.out.println("ioe: " + ioe.getMessage());
							}
						}
					}
					//If the user was not found, send a verificationResponseMessage with false and a User ID or -1
					VerificationResponseMessage response;
					response = new VerificationResponseMessage(false, -1);
					try {
						oos.writeObject(response);
						oos.flush();
						
						//Log the message that was sent
						Log.sent(response);
					} catch (IOException ioe) {
						System.out.println("ioe: " + ioe.getMessage());
					}
						
				} else {
					//If the Message recieved was not an instance of Verification Messages
					System.out.println("Exception: Expecting an instanceof VerificationMessage!");
				}
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("cnfe in run: " + cnfe.getMessage());
		} catch (IOException ioe) {
			System.out.println("ioe in run: " + ioe.getMessage());
		}
	}
	
	public void sendStringMessage(ChatMessage message) {
		//Send out a StringMessage to the user
		try {
			StringMessage sMessage = new StringMessage(cs.getData().findUserByUid(message.getUid()).getUsername() + ": " + message.getMessage());
			oos.writeObject(sMessage);
			oos.flush();
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
		

	//Handles consistently listening for chatMessages from client
	public void run() {
		login();
		try {
			
			while(true) {
				Message message = (ChatMessage)ois.readObject();
				
				//Log the received Message
				Log.recieved(message);
				cs.sendMessageToAllClients(message);
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("cnfe in run: " + cnfe.getMessage());
		} catch (IOException ioe) {
			System.out.println("ioe in run: " + ioe.getMessage());
		}
	}
}
