package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Vector;

import botThread.BotThread;
import database.Database;
import objects.ClientConversation;
import objects.Conversation;
import objects.DataContainer;
import objects.User;
import objects.message.ChatMessage;
import objects.message.CommandMessage;
import objects.message.CreateConversationMessage;
import objects.message.Message;
import objects.message.MessagesMessage;
import parsing.DataWriter;

public class Server extends Thread {

	private Map<Integer, Conversation> conversationMap;
	private Vector<ServerThread> serverThreads;
	private DataContainer data;
	private Map<Integer, ArrayList<String>> chatHistory;
	Scanner scan;
	DataWriter dataWriter;
	private Database db;

	public void initializeHistory() {
		chatHistory = new HashMap<Integer, ArrayList<String>>();
		for (int i = 1; i <= 11; i++) {
			chatHistory.put(i, new ArrayList<String>());
		}
	}

	public Server(int port) {
		db = new Database("localhost", 3306, "demo", "demo", "CSCI201");
		dataWriter = new DataWriter();
		ArrayList<User> foundUsers = db.getUsers();
		this.data = new DataContainer(foundUsers);

		for (User user : this.data.getUsers()) {
			System.out.println("UID: " + user.getUid() + "  Username: " + user.getUsername() + "  Password: "
					+ user.getPassword());
		}
		ServerSocket ss = null;
		serverThreads = new Vector<ServerThread>();
		InitializeConversations(data.getUsers());
		initializeHistory();
		this.start();

		try {
			ss = new ServerSocket(port);
			while (true) {
				System.out.println("\nWaiting for connection...");
				Socket s = ss.accept();
				System.out.println("connection from " + s.getInetAddress());
				ServerThread st = new ServerThread(s, this, db);
				serverThreads.add(st);
			}
		} catch (IOException ioe) {
			System.out.println("\nioe: " + ioe.getMessage());
		} finally {
			if (ss != null) {
				try {
					ss.close();
				} catch (IOException ioe) {
					System.out.println("\nioe closing ss: " + ioe.getMessage());
				}
			}
		}
	}

	public void InitializeConversations(ArrayList<User> users) {
		conversationMap = db.getConversations(data);
	}

	// Constructor for TempMain
	public Server(int port, DataContainer data) {
		chatHistory = new HashMap<Integer, ArrayList<String>>();
		dataWriter = new DataWriter();
		this.data = data;

		for (User user : this.data.getUsers()) {
			System.out.println("UID: " + user.getUid() + "  Username: " + user.getUsername() + "  Password: "
					+ user.getPassword());
		}
		ServerSocket ss = null;
		serverThreads = new Vector<ServerThread>();
		InitializeConversations(data.getUsers());
		this.start();

		try {
			ss = new ServerSocket(port);
			while (true) {
				System.out.println("\nWaiting for connection...");
				Socket s = ss.accept();
				System.out.println("connection from " + s.getInetAddress());
				ServerThread st = new ServerThread(s, this, db);
				serverThreads.add(st);
			}
		} catch (IOException ioe) {
			System.out.println("\nioe: " + ioe.getMessage());
		} finally {
			if (ss != null) {
				try {
					ss.close();
				} catch (IOException ioe) {
					System.out.println("\nioe closing ss: " + ioe.getMessage());
				}
			}
		}
	}

	public void removeServerThread(ServerThread st) {
		serverThreads.remove(st);
	}

	public void addUser(User user, ServerThread st) {
		if (data.addUser(user)) {
			System.out.println(data);
			dataWriter.saveData(data, "JSON/Input.json");
			st.sendStringMessage("User Created, Please Log In");
			Log.log("User Created");
		} else {
			st.sendStringMessage("User Already Exists");
		}
	}

	public void sendMessageToAllClients(Message message) {
		Log.sent(message);
		if (message instanceof ChatMessage) {
			
			///Add Message
			Integer chatID = ((ChatMessage) message).getCid();
			String messageString = ((ChatMessage) message).getMessage();
			Integer userID = ((ChatMessage) message).getUid();
			
			chatHistory.get(chatID).add(getData().findUserByUid(userID).getUsername() + ": " + messageString);
			
			//Add to database
			db.addMessage(chatID, userID, messageString);
			
			for (ServerThread st : serverThreads) {
				Message messages = new MessagesMessage(chatHistory);
				st.sendMessage(messages);
			}
			
			Conversation conversation = conversationMap.get(((ChatMessage) message).getCid());
			conversation.sendMessageToConversation(message);
		}
	}

	public DataContainer getData() {
		return data;
	}

	public void receiveCommand(CommandMessage message, ServerThread st) {
		if (data.isAdmin(message.getUid())) {
			String command = message.getCommand();
			Log.command(message);
			if (command.startsWith("/add bot")) {
				Integer number = Integer.parseInt(command.substring(9, 10));
				new BotThread("localhost", 6789, number);
			} else if (command.equals("/gamemode 0")) {
				st.sendStringMessage("You are now in Creative Mode!");
			} else if (command.equals("/gamemode 1")) {
				st.sendStringMessage("You are now in Survival Mode!");
			} else if (command.equals("/help")) {
				st.sendStringMessage(
						"--Help Menu--\n Commands:\n  - \"/add bot #\" : adds a bot of the type of the designated number\n");
			}
		}
	}

	// Allows for Server Commands
	public void run() {
		scan = new Scanner(System.in);
		while (true) {
			String command = scan.nextLine();
			if (command.equals("add bot")) {
				System.out.println("What Bot Number would you like?");
				Integer number = Integer.parseInt(scan.nextLine());
				new BotThread("localhost", 6789, number);
			} else if (command.equals("help")) {
				System.out.println("\n\n///HELP MENU///");
				System.out.println("Commands: ");
				System.out.println("\"add bot\" - begins the add bot process");
				System.out.println("\"help\" - brings up the help menu\n\n");

			} else if (command.equals("shutdown")) {
				System.out.println("The server is now shutting down...");
				// PUSH NEW DATA
				System.exit(0);
			} else {
				System.out.println("SERVER :: INVALID COMMAND");
			}
		}
	}

	public void logOn(User user, ServerThread st) {
		user.logOn(st);

		Message messages = new MessagesMessage(chatHistory);
		st.sendMessage(messages);
		
		for (Entry<Integer, Conversation> entry : conversationMap.entrySet()) {
			entry.getValue().addActiveUser(user);
		}
	}

	public ArrayList<ClientConversation> getUserConversations(User user) {
		ArrayList<ClientConversation> result = new ArrayList<ClientConversation>();
		for (Entry<Integer, Conversation> entry : conversationMap.entrySet()) {
			if (entry.getValue().hasUser(user)) {
				result.add(new ClientConversation(entry.getValue().getConversationID()));
			}
		}
		return result;
	}

	public void createConversation(CreateConversationMessage message) {

		Integer chatID = conversationMap.size() + 1;

		ArrayList<User> newUsers = new ArrayList<User>();

		for (String username : message.getUsers()) {

			User temp = data.findUserByUsername(username);

			if (temp != null && !temp.getUsername().equals("Guest")) {
				newUsers.add(temp);
			}

		}

		db.createConversation(newUsers, "", chatID);

		conversationMap.put(chatID, new Conversation(newUsers, chatID));

		for (ServerThread st : serverThreads) {
			conversationMap.get(chatID).addActiveUser(st.getUser());
			st.updateConversation();
		}
	}
}
