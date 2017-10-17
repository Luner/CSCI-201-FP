package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

import objects.ChatMessage;
import objects.DataContainer;
import objects.User;

public class Server {

	private Vector<ServerThread> serverThreads;
	private DataContainer data;
	
	public Server(DataContainer data, int port) {
		
		this.data = data;
		
		ServerSocket ss = null;
		serverThreads = new Vector<ServerThread>();
		try {
			ss = new ServerSocket(port);
			while (true) {
				System.out.println("waiting for connection...");
				Socket s = ss.accept();
				System.out.println("connection from " + s.getInetAddress());
				ServerThread st = new ServerThread(s, this);
				serverThreads.add(st);
			}
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		} finally {
			if (ss != null) {
				try {
					ss.close();
				} catch (IOException ioe) {
					System.out.println("ioe closing ss: " + ioe.getMessage());
				}
			}
		}
	}
	
	public void sendMessageToAllClients(ChatMessage message) {
		for (ServerThread st : serverThreads) {
			st.sendMessage(message);
		}
	}
}









