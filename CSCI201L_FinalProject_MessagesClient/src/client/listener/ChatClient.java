package client.listener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import objects.ChatMessage;

public class ChatClient extends Thread {

	private ObjectInputStream ois;
	private ObjectOutputStream oos;

	public ChatClient(String hostname, int port) {
		Socket s = null;
		Scanner scan = null;

		try {
			s = new Socket(hostname, port);
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			this.start();
			scan = new Scanner(System.in);
			while(true) {
				String line = scan.nextLine();
				ChatMessage message = new ChatMessage("Jonathan Luner", line);
				oos.writeObject(message);
				oos.flush();
			}
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		} finally {
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
	
	public void run() {
		try {
			while(true) {
				ChatMessage message = (ChatMessage)ois.readObject();
				System.out.println(message.getName() + ": " + message.getMessage());
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("cnfe: " + cnfe.getMessage());
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
	
	
}
