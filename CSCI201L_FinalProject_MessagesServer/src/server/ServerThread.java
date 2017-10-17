package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import objects.ChatMessage;
import objects.User;
import objects.VerificationMessage;

public class ServerThread extends Thread {

	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Server cs;
	private ArrayList<User> users;
	String username; 
	
	public ServerThread(Socket s, Server cs) {
		users = cs.getData().getUsers();
		try {
			this.cs = cs;
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			login();
			this.start();
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
	
	private void login() {
		boolean verified = false;
		try {
			while(!verified) {
				Object message = ois.readObject();
				if(message instanceof VerificationMessage) {
					for(User user : users) {
						if(user.verify(((VerificationMessage) message).getUsername(), ((VerificationMessage) message).getPassword())) {
							this.username = ((VerificationMessage) message).getUsername();
							verified = true;
							break;
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
	
	public void sendMessage(ChatMessage message) {
		try {
			oos.writeObject(message);
			oos.flush();
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}

	public void run() {
		try {
			while(true) {
				ChatMessage message = (ChatMessage)ois.readObject();
				cs.sendMessageToAllClients(message);
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("cnfe in run: " + cnfe.getMessage());
		} catch (IOException ioe) {
			System.out.println("ioe in run: " + ioe.getMessage());
		}
	}
}
