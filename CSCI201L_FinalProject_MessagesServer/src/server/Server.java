package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

import botThread.BotClient;
import objects.DataContainer;
import objects.message.ChatMessage;
import objects.message.CommandMessage;
import objects.message.Message;
import objects.message.StringMessage;

public class Server extends Thread{

	private Vector<ServerThread> serverThreads;
	private DataContainer data;
	Scanner scan;
	
	public Server(DataContainer data, int port)  {
		
		this.data = data;
		ServerSocket ss = null;
		serverThreads = new Vector<ServerThread>();
		this.start();
		
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

	public void removeServerThread(ServerThread st) {
		serverThreads.remove(st);
	}

	public void sendMessageToAllClients(Message message) {
		Log.sent(message);
		for (ServerThread st : serverThreads) {
			st.sendStringMessage((ChatMessage)message);
		}
	}
	
	public DataContainer getData() {
		return data;
	}
	
	public void receiveCommand(CommandMessage message, ServerThread st) {
		if(data.isAdmin(message.getUid())) {
			String command = message.getCommand();
			Log.command(message);
			if(command.startsWith("/add bot")) {
				Integer number = Integer.parseInt(command.substring(9, 10));
				new BotClient("localhost", 6789, number);
			} else if(command.equals("/gamemode 0")) {
				st.sendStringMessage("You are now in Creative Mode!");
			} else if(command.equals("/gamemode 1")) {
				st.sendStringMessage("You are now in Survival Mode!");
			} else if(command.equals("/help")) {
				st.sendStringMessage("--Help Menu--\n Commands:\n  - \"/add bot #\" : adds a bot of the type of the designated number\n");
			}
		}
	}
	
	
	//Allows for Server Commands
	public void run() {
		scan = new Scanner(System.in);
		while(true) {
			String command = scan.nextLine();
			if(command.equals("add bot")) {
				System.out.println("What Bot Number would you like?");
				Integer number = Integer.parseInt(scan.nextLine());
				new BotClient("localhost", 6789, number);
			} else if(command.equals("help")) {
				System.out.println("\n\n///HELP MENU///");
				System.out.println("Commands: ");
				System.out.println("\"add bot\" - begins the add bot process");
				System.out.println("\"help\" - brings up the help menu\n\n");
			} else {
				System.out.println("SERVER :: INVALID COMMAND");
			}
		}
	}
}









