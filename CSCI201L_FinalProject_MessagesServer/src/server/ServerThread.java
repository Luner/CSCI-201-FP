package server;

import database.Database;
import objects.ClientConversation;
import objects.User;
import objects.message.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class ServerThread extends Thread {

	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Server cs;
	private User user;
	@SuppressWarnings("unused")
	private String username;
	int uid;

	private boolean running = true;
	@SuppressWarnings("unused")
	private Database db;

	public User getUser() {
		return user;
	}

	public ServerThread(Socket s, Server cs, Database db) {
		// Initialize the Object streams for the socket
		try {
			this.cs = cs;
			this.db = db;
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			this.start();
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}

	private void login() {
		try {
			// loops until connected user is verified
			while (true) {
				Object message = ois.readObject();
				if (message instanceof VerificationMessage) {
					// if the message is a VerificationMessage, Log the message received
					Log.recieved((Message) message);

					// Check the information sent against every user that exists
					for (User user : cs.getData().getUsers()) {
						if (user.verify(((VerificationMessage) message).getUsername(),
								((VerificationMessage) message).getPassword())) {
							this.username = user.getUsername();
							this.uid = user.getUid();
							this.user = user;

							// Send VerificationResponseMessage
							VerificationResponseMessage response;

							// Send a verificationResponseMessage with true and the corresponding User ID
							response = new VerificationResponseMessage(true, uid);

							oos.writeObject(response);
							oos.flush();

							// Tell Server User logged on
							cs.logOn(user, this);

							Log.sent(response);

							updateConversation();
							updateContacts();

							// Log it?
							return;
						}
					}
					// If the user was not found, send a verificationResponseMessage with false and
					// a User ID or -1
					VerificationResponseMessage response;
					response = new VerificationResponseMessage(false, -1);
					try {
						oos.writeObject(response);
						oos.flush();

						// Log the message that was sent
						Log.sent(response);
					} catch (IOException ioe) {
						System.out.println("ioe: " + ioe.getMessage());
					}

				} else if (message instanceof CreateUserMessage) {
					String username = ((CreateUserMessage) message).getUsername();
					String password = ((CreateUserMessage) message).getPassword();
					System.out.println("username: " + username + "  password: " + password + "  nextId: "
							+ cs.getData().getNextID());
					User created = new User(username, password, cs.getData().getNextID());
					boolean userCreated = cs.addUser(created, this);
					VerificationResponseMessage response;
					if (!userCreated) {
						response = new VerificationResponseMessage(false, -1);
						try {
							oos.writeObject(response);
							oos.flush();

							// Log the message that was sent
							Log.sent(response);
						} catch (IOException ioe) {
							System.out.println("ioe: " + ioe.getMessage());
						}
					} else {

						response = new VerificationResponseMessage(true, created.getUid());
						try {
							oos.writeObject(response);
							oos.flush();

							// Log the message that was sent
							Log.sent(response);
						} catch (IOException ioe) {
							System.out.println("ioe: " + ioe.getMessage());
						}
						this.username = created.getUsername();
						this.uid = created.getUid();
						this.user = created;

						// Tell Server User logged on
						cs.logOn(user, this);

						updateConversation();
						cs.updateContacts();
						return;
					}

				} else if (message instanceof LogoutMessage) {
					running = false;
					break;
				} else {
					// If the Message received was not an instance of Verification Messages
					System.out.println("Exception: Expecting an instanceof VerificationMessage!");
				}
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("cnfe in run: " + cnfe.getMessage());
		} catch (IOException ioe) {
			System.out.println("ioe in run: " + ioe.getMessage());
		}
	}

	public void updateConversation() {
		try {
			ArrayList<ClientConversation> list = cs.getUserConversations(user);
			oos.writeObject(new ConversationsMessage(list));
			oos.flush();
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}

	public void sendMessage(Message message) {
		try {
			oos.writeObject(message);
			oos.flush();
		} catch (IOException ioe) {
			cs.removeServerThread(this);
			System.out.println("ioe: " + ioe.getMessage());
			running = false;

		}
	}

	public void sendChatStringMessage(ChatMessage message) {
		// Send out a StringMessage to the user
		try {
			ChatStringMessage sMessage = new ChatStringMessage(
					cs.getData().findUserByUid(message.getUid()).getUsername() + ": " + message.getMessage(),
					message.getCid());
			oos.writeObject(sMessage);
			oos.flush();
		} catch (IOException ioe) {
			cs.removeServerThread(this);
			System.out.println("ioe: " + ioe.getMessage());
			running = false;

		}
	}

	public void sendStringMessage(String message) {
		// Send out a StringMessage to the user
		try {
			StringMessage sMessage = new StringMessage(message);
			oos.writeObject(sMessage);
			oos.flush();
		} catch (IOException ioe) {
			cs.removeServerThread(this);
			System.out.println("ioe: " + ioe.getMessage());
			running = false;

		}
	}

	public void updateContacts() {
		ArrayList<String> contacts = new ArrayList<>();
		ArrayList<User> temp = cs.getData().getUsers();
		for (User u : temp) {
			contacts.add(u.getUsername());
		}
		ContactsMessage cm = new ContactsMessage(contacts);
		try {
			oos.writeObject(cm);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Handles consistently listening for chatMessages from client
	public void run() {
		login();
		ArrayList<String> contacts = new ArrayList<>();
		ArrayList<User> temp = cs.getData().getUsers();
		for (User u : temp) {
			contacts.add(u.getUsername());
		}

		try {

			while (running) {
				Message message = (Message) ois.readObject();
				// Log the received Message
				Log.recieved(message);

				if (message instanceof ChatMessage) {
					cs.sendMessageToAllClients(message);
				} else if (message instanceof CommandMessage) {
					cs.receiveCommand((CommandMessage) message, this);
				} else if (message instanceof CreateConversationMessage) {
					cs.createConversation((CreateConversationMessage) message);
				} else if (message instanceof LogoutMessage) {
					break;
				}
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("cnfe in run: " + cnfe.getMessage());
		} catch (IOException ioe) {
			System.out.println("ioe in run: " + ioe.getMessage());
		} catch (ConcurrentModificationException cme) {
			System.out.println("cme in run: " + cme.getMessage());
		}
		cs.removeServerThread(this);
	}
}
