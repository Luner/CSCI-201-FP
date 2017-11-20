package server;

import botThread.BotThread;
import database.Database;
import objects.ClientConversation;
import objects.Conversation;
import objects.DataContainer;
import objects.User;
import objects.message.*;
import parsing.DataWriter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.Map.Entry;

public class Server extends Thread {

	private Map<Integer, Conversation> conversationMap;
	private Vector<ServerThread> serverThreads;
	private DataContainer data;
	private Map<Integer, ArrayList<String>> chatHistory;
	Scanner scan;
	DataWriter dataWriter;
	private Database db;

	public void initializeHistory() {
		chatHistory = db.getMessagesMap(data);
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
			conversationMap.get(1).addUser(user);
			conversationMap.get(1).addActiveUser(user);
			db.registerUser(user.getUsername(), user.getPassword());
			db.addUserToConversation(user, 1);
			Log.log("User Created");
		}
	}

	public void sendMessageToAllClients(Message message) {
		Log.sent(message);
		if (message instanceof ChatMessage) {

			/// Add Message
			Integer chatID = ((ChatMessage) message).getCid();
			String messageString = ((ChatMessage) message).getMessage();
			Integer userID = ((ChatMessage) message).getUid();

			chatHistory.get(chatID).add(getData().findUserByUid(userID).getUsername() + ": " + messageString);

			// Add to database
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
		String command = message.getCommand();
		Log.command(message);
		if (command.startsWith("/add bot")) {
			Integer number = Integer.parseInt(command.substring(9, 10));
			String statement = command.substring(10, command.length());

			new BotThread("localhost", 6789, number, statement);
		} else if (command.startsWith("/addUser")) {
			String username = command.substring(9, command.length() - 9);
			String chatID = command.substring(command.length() - 9, command.length());
			int cid = Integer.parseInt(chatID.substring(chatID.indexOf("J") + 1, chatID.length()));

			System.out.println("Adding User: " + data.findUserByUsername(username).getUid() + " to Conversation: " + cid);
			addUserToConversation(data.findUserByUsername(username), cid);
			 
		} else if (command.equals("/gamemode 0")) {
			st.sendStringMessage("You are now in Creative Mode!");
		} else if (command.equals("/gamemode 1")) {
			st.sendStringMessage("You are now in Survival Mode!");
		} else if (command.equals("/help")) {
			st.sendStringMessage(
					"--Help Menu--\n Commands:\n  - \"/add bot #\" : adds a bot of the type of the designated number\n");
		}
	}

	// Allows for Server Commands
	public void run() {
		scan = new Scanner(System.in);
		while (true) {
			String command = scan.nextLine();
			if (command.equals("help")) {
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
				result.add(new ClientConversation(entry.getValue().getConversationID(), entry.getValue().getName()));
			}
		}
		return result;
	}
	
	public void addUserToConversation(User user, Integer cid) {

		conversationMap.get(cid).addUser(user);
		conversationMap.get(cid).addActiveUser(user);
		
		db.addUserToConversation(user, cid);
	}

	public void createConversation(CreateConversationMessage message) {

		Integer chatID = conversationMap.size() + 1;

		ArrayList<User> newUsers = new ArrayList<User>();
		chatHistory.put(chatID, new ArrayList<String>());


		for (String username : message.getUsers()) {

			User temp = data.findUserByUsername(username);

			if (temp != null && !temp.getUsername().equals("Guest")) {
				newUsers.add(temp);
			}

		}

		db.createConversation(newUsers, ((CreateConversationMessage) message).getName(), chatID);

		conversationMap.put(chatID, new Conversation(newUsers, chatID, ((CreateConversationMessage) message).getName()));
		
		for (ServerThread st : serverThreads) {
			conversationMap.get(chatID).addActiveUser(st.getUser());
			st.updateConversation();
			st.sendMessage(new MessagesMessage(chatHistory));
		}
	}
}
