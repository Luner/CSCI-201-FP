package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import objects.ChatMessage;
import objects.Message;
import objects.User;
import objects.VerificationMessage;
import objects.VerificationResponseMessage;

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
							} catch (IOException ioe) {
								System.out.println("ioe: " + ioe.getMessage());
							}
							return;
						} else {
							VerificationResponseMessage response;
							response = new VerificationResponseMessage(false, -1);
							try {
								oos.writeObject(response);
								oos.flush();
							} catch (IOException ioe) {
								System.out.println("ioe: " + ioe.getMessage());
							}
						}
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
	
	public void sendMessage(Message message) {
		try {
			oos.writeObject(message);
			oos.flush();
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}

	public void run() {
		login();
		try {
			while(true) {
				Message message = (ChatMessage)ois.readObject();
				cs.sendMessageToAllClients(message);
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("cnfe in run: " + cnfe.getMessage());
		} catch (IOException ioe) {
			System.out.println("ioe in run: " + ioe.getMessage());
		}
	}
}
