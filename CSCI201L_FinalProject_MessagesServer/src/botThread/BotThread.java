package botThread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import objects.message.ChatMessage;
import objects.message.Message;
import objects.message.VerificationMessage;
import objects.message.VerificationResponseMessage;
import server.Server;

public class BotThread extends Thread {

	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private int uid;
	private boolean running;
	private Server cs;
	private Socket s;
	private Scanner scan;
	private Random rand;
	private Integer frequency;
	private String words;
	private ArrayList<String> ytlinks;

	public BotThread(String hostname, int port, int frequency, String words, Server cs) {
		this.cs = cs;
		running = true;
		rand = new Random();
		s = null;
		scan = null;
		uid = -10;
		this.words = words;
		ytlinks = new ArrayList<>();
		this.frequency = frequency;
		ytlinks.add("https://www.youtube.com/watch?v=gy1B3agGNxw");
		ytlinks.add("https://www.youtube.com/watch?v=XCiDuy4mrWU");
		ytlinks.add("https://www.youtube.com/watch?v=k3jlviX88iw");
		ytlinks.add("https://www.youtube.com/watch?v=y6120QOlsfU");
		ytlinks.add("https://www.youtube.com/watch?v=feA64wXhbjo");
		ytlinks.add("https://www.youtube.com/watch?v=2adAoJJB8Z0&feature=youtu.be");
		ytlinks.add("https://www.youtube.com/watch?v=GVN17U3Vg34");
		ytlinks.add("https://www.youtube.com/watch?v=vwaiyjh1dGk");
		ytlinks.add("https://www.youtube.com/watch?v=pD_imYhNoQ4");
		ytlinks.add("https://www.youtube.com/watch?v=KS7hkwbKmBM");
		ytlinks.add("https://www.youtube.com/watch?v=L_jWHffIx5E");
		try {
			// Attempts to connect to the Socket
			s = new Socket(hostname, port);
			// Creates a new scanner to receive information from the console
			scan = new Scanner(System.in);

			/*
			 * If successful, will create ObjectStreams to allow for the sending of objects
			 * to and from the server
			 */
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			login();

		} catch (IOException ioe) {
			System.out.println("BOT :: ioe: " + ioe.getMessage());
		}
	}

	public void end() {
		this.running = false;
	}

	// handles the sending of information to the Server
	private void sender() {

		// An infinite loop that will constantly look for a line from the console
		// And send a ChatMessage to the Server

		switch (words) {
		case "time":
			while (running) {
				try {
					Message message = new ChatMessage(uid, 1, "Welcome to global chat!");
					oos.writeObject(message);
					oos.flush();
					Thread.sleep(30000);
				} catch (InterruptedException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		case "youtube":
			try {
				int i = rand.nextInt();
				Message message = new ChatMessage(uid, 1, ytlinks.get(i % 11));
				oos.writeObject(message);
				oos.flush();
				Thread.sleep(30000);
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
			break;
		default:
			while (running) {
				try {
					if (rand.nextInt(1000000000) < frequency) {
						// Creates a ChatMessage with the input
						Message message = new ChatMessage(uid, 1, words);

						// Sends the ChatMessage Object to the server
						oos.writeObject(message);
						oos.flush();
					}
				} catch (IOException ioe) {
					System.out.println("BOT :: ioe: " + ioe.getMessage());
				}
			}
		}
		cs.removeBotThread(this);
	}

	private void login() {

		try {
			String usernameInput = "Bot";
			String passwordInput = "Bot_password";

			// Creates a VerificationMessage with the username and password inputs
			Message message = new VerificationMessage(usernameInput, passwordInput);

			// Sends the VerificationMessage Object to the server
			oos.writeObject(message);
			oos.flush();

			// prepares to listen for the response from the server
			if (verificationResponse()) {
				// Runs Bot Sender
				this.start();
			}

		} catch (IOException ioe) {
			System.out.println("BOT :: ioe: " + ioe.getMessage());
		}

	}

	private boolean verificationResponse() {
		try {

			// Receives the object
			Object message = ois.readObject();

			// checks if the object is an instance of VerificationResponseMessage
			// If it is and user exists, set uid and return true
			if (message instanceof VerificationResponseMessage) {
				if (((VerificationResponseMessage) message).isVerified()) {
					uid = ((VerificationResponseMessage) message).getUid();
					return true;
				}
				// Let the user know the Verification Failed
				System.out.println("Could Not Verify Bot\n");
				return false;
			} else {
				// Recieved a message that was not a VerificationResponseMessage
				System.out.println(
						"BOT :: Exception in ChatClient verificationResponse(): Expecting VerificationResponseMessage");
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("BOT :: cnfe: " + cnfe.getMessage());
		} catch (IOException ioe) {
			System.out.println("BOT :: ioe: " + ioe.getMessage());
		}

		System.out.println("Could Not Verify Bot\n");
		return false;
	}

	// Handles the receiving of information
	public void run() {
		sender();
		cleanUp();
	}

	private void cleanUp() {
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
