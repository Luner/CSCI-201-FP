package server;

import botThread.BotThread;
import database.Database;
import objects.ClientConversation;
import objects.Conversation;
import objects.DataContainer;
import objects.User;
import objects.message.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.Map.Entry;

public class Server extends Thread {

	private Map<Integer, Conversation> conversationMap;
	private Vector<ServerThread> serverThreads;
	private Vector<BotThread> botThreads;
	private DataContainer data;
	private Map<Integer, ArrayList<String>> chatHistory;
	@SuppressWarnings("unused")
	private Scanner scan;
	private Database db;

	private void initializeHistory() {
		chatHistory = db.getMessagesMap(data);
	}

	public Server(int port) {
		botThreads = new Vector<BotThread>();
		db = new Database("localhost", 3306, "demo", "demo", "CSCI201");
		ArrayList<User> foundUsers = db.getUsers();
		this.data = new DataContainer(foundUsers);

		for (User user : this.data.getUsers()) {
			System.out.println("UID: " + user.getUid() + "  Username: " + user.getUsername() + "  Password: "
					+ user.getPassword());
		}
		ServerSocket ss = null;
		serverThreads = new Vector<>();
		initializeConversations(data.getUsers());
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

	private void initializeConversations(ArrayList<User> users) {
		conversationMap = db.getConversations(data);
	}

	// Constructor for TempMain
	public Server(int port, DataContainer data) {
		botThreads = new Vector<BotThread>();
		chatHistory = new HashMap<Integer, ArrayList<String>>();
		this.data = data;

		for (User user : this.data.getUsers()) {
			System.out.println("UID: " + user.getUid() + "  Username: " + user.getUsername() + "  Password: "
					+ user.getPassword());
		}
		ServerSocket ss = null;
		serverThreads = new Vector<>();
		initializeConversations(data.getUsers());
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
		for (Entry<Integer, Conversation> entry : conversationMap.entrySet()) {
			entry.getValue().removeActiveUser(st.getUser());
		}
		serverThreads.remove(st);
	}

	public boolean addUser(User user, ServerThread st) {
		if (data.addUser(user)) {
			System.out.println(data);
			conversationMap.get(1).addUser(user);
			conversationMap.get(1).addActiveUser(user);
			db.registerUser(user.getUsername(), user.getPassword());
			db.addUserToConversation(user, 1);
			Log.log("User Created");

			return true;
		} else {
			return false;
		}
	}

	public void updateContacts() {
		for (ServerThread serverThread : serverThreads) {
			serverThread.updateContacts();
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

	public void removeBotThread(BotThread bt) {
		if (botThreads.contains(bt)) {
			botThreads.remove(bt);
		}
	}

	public void receiveCommand(CommandMessage message, ServerThread st) {
		String command = message.getCommand();
		Log.command(message);
		if (command.startsWith("/add bot")) {
			Integer number = Integer.parseInt(command.substring(9, 10));
			String statement = command.substring(11, command.length());

			botThreads.add(new BotThread("localhost", 6789, number, statement, this));
		} else if (command.startsWith("/remove bots")) {
			for (BotThread bt : botThreads) {
				bt.end();
			}
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
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		try {
			while (true) {
				String command = scan.nextLine();
				switch (command) {
				case "help":
					System.out.println("\n\n///HELP MENU///");
					System.out.println("Commands: ");
					System.out.println("\"add bot\" - begins the add bot process");
					System.out.println("\"help\" - brings up the help menu\n\n");

					// do nothing
					break;
				case "shutdown":
					System.out.println("The server is now shutting down...");
					// PUSH NEW DATA
					System.exit(0);
				default:
					System.out.println("SERVER :: INVALID COMMAND");
					break;
				}
			}

		} catch (NoSuchElementException n) {
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

	public void createConversation(CreateConversationMessage message) {

		Integer chatID = conversationMap.size() + 1;

		ArrayList<User> newUsers = new ArrayList<>();
		chatHistory.put(chatID, new ArrayList<>());

		for (String username : message.getUsers()) {

			User temp = data.findUserByUsername(username);

			if (temp != null && !temp.getUsername().equals("Guest")) {
				newUsers.add(temp);
			}

		}

		db.createConversation(newUsers, message.getName(), chatID);
		conversationMap.put(chatID, new Conversation(newUsers, chatID, message.getName()));
		for (ServerThread st : serverThreads) {
			conversationMap.get(chatID).addActiveUser(st.getUser());
			st.updateConversation();
			st.sendMessage(new MessagesMessage(chatHistory));
		}
	}
}
