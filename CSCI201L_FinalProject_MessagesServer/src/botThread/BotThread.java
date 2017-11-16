package botThread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

import objects.message.ChatMessage;
import objects.message.Message;
import objects.message.VerificationMessage;
import objects.message.VerificationResponseMessage;

public class BotThread extends Thread {

	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private int uid;
	Socket s;
	Scanner scan;
	Random rand;
	Integer amount;

	public BotThread(String hostname, int port, Integer amount) {
		rand = new Random();
		s = null;
		scan = null;
		uid = -1;
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
			this.amount = amount;
			login(amount);

		} catch (IOException ioe) {
			System.out.println("BOT :: ioe: " + ioe.getMessage());
		}
	}

	// handles the sending of information to the Server
	public void sender() {

		int counter = 0;
		// An infinite loop that will constantly look for a line from the console
		// And send a ChatMessage to the Server
		while (true) {
			try {
				if (rand.nextInt(1000000000) == 1)
					counter++;
				if (counter == amount) {
					String line = "";
					if (amount == 1) {
						line = "Hello!";
						counter = 0;
					} else if (amount == 2) {
						line = "Whats up Everyone?";
						counter = 0;
					} else if (amount == 3) {
						line = "Anything cool happening?";
						counter = 0;
					} else if (amount == 4) {
						line = "How has your day been?";
						counter = 0;
					}

					// Creates a ChatMessage with the input
					Message message = new ChatMessage(uid, 0, line);

					// Sends the ChatMessage Object to the server
					oos.writeObject(message);
					oos.flush();
				}
			} catch (IOException ioe) {
				System.out.println("BOT :: ioe: " + ioe.getMessage());
			}
		}
	}

	private void login(int number) {

		try {
			String usernameInput = "Bot" + number;
			String passwordInput = "Bot" + number + "_password";

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
