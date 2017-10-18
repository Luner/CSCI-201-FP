package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

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
	private ArrayList<User> users;
	String username; 
	int uid;
	
	public ServerThread(Socket s, Server cs) {
		users = cs.getData().getUsers();
		
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
					logReceivedMessage((Message)message);
					for(User user : users) {
						if(user.verify(((VerificationMessage) message).getUsername(), ((VerificationMessage) message).getPassword())) {
							this.username = user.getUsername();
							this.uid = user.getUid();
							
							//Send VerificationResponseMessage
							VerificationResponseMessage response;
							response = new VerificationResponseMessage(true, uid);
							try {
								oos.writeObject(response);
								oos.flush();
								logSentMessage(response);
								return;
							} catch (IOException ioe) {
								System.out.println("ioe: " + ioe.getMessage());
							}
						}
					}
					
					VerificationResponseMessage response;
					response = new VerificationResponseMessage(false, -1);
					try {
						oos.writeObject(response);
						oos.flush();
							logSentMessage(response);
					} catch (IOException ioe) {
						System.out.println("ioe: " + ioe.getMessage());
					}
						
				} else {
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
		try {
			StringMessage sMessage = new StringMessage(cs.getData().findUserByUid(message.getUid()).getUsername() + ": " + message.getMessage());
			oos.writeObject(sMessage);
			oos.flush();
			logSentMessage(sMessage);
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
	
	private void logSentMessage(Message message) {
		System.out.println("LOG:: Sent: " + message.toString());
	}
	
	private void logReceivedMessage(Message message) {
		System.out.println("LOG:: Recieved: " + message.toString());
	}

	public void run() {
		login();
		try {
			
			while(true) {
				Message message = (ChatMessage)ois.readObject();
				logReceivedMessage(message);
				cs.sendMessageToAllClients(message);
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("cnfe in run: " + cnfe.getMessage());
		} catch (IOException ioe) {
			System.out.println("ioe in run: " + ioe.getMessage());
		}
	}
}
