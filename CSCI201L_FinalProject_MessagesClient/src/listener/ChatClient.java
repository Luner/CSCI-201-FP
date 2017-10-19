package listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.Socket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import objects.message.ChatMessage;
import objects.message.Message;
import objects.message.StringMessage;
import objects.message.VerificationMessage;
import objects.message.VerificationResponseMessage;


public class ChatClient extends Application {
	
	//GRAPHICAL USER INTERFACE
	Button login;
	Button sendMessage;
	TextField username;
	TextField password;
	TextField chatText;
	Text usernameLabel;
	Text passwordLabel;
	Scene chat;
	Stage window;
	VBox chatLayout;
	
	//SERVER CLIENT COMMUNICATION
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private int uid;
	private Socket s;
	
	public static void main(String [] args) {
		launch(args);
	}
	

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Messaging Application");

	
		usernameLabel = new Text("username: ");
		passwordLabel = new Text("password: ");	
		
		username = new TextField();
		password = new TextField();
		chatText = new TextField();
		
		login = new Button();
		login.setText("Login");
		login.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				if(login(username.getText(), password.getText())) {
					primaryStage.setScene(chat);
					Thread th = new Thread(task);
					th.setDaemon(true);
					th.start();
				} 				
			}
		});
		
		sendMessage = new Button();
		sendMessage.setText("Send Message");
		sendMessage.setOnAction(e -> {
			send(chatText.getText());
			chatText.setText("");
		});
		

		chatLayout = new VBox();
		HBox layout = new HBox();
		
		layout.setPadding(new Insets(15, 12, 15, 12));
		layout.setSpacing(10);
	    
		layout.setStyle("-fx-background-color: #336699;");
		layout.getChildren().add(usernameLabel);
		layout.getChildren().add(username);
		layout.getChildren().add(passwordLabel);
		layout.getChildren().add(password);
		layout.getChildren().add(login);
		chatLayout.getChildren().add(chatText);	
		chatLayout.getChildren().add(sendMessage);
		Scene scene = new Scene(layout, 600, 400);
		chat = new Scene(chatLayout, 600, 400);
		primaryStage.setScene(scene);
		primaryStage.show();

		setUpChatClient("localhost", 6789);	
	}
	
	private void setUpChatClient(String hostname, int port) {
		s = null;
		uid = -1;
		try {
			//Attempts to connect to the Socket
			s = new Socket(hostname, port);	
			//Creates a new scanner to receive information from the console
				
			/*If successful, will create ObjectStreams to allow for the sending of 
			  objects to and from the server*/
			oos = new ObjectOutputStream(s.getOutputStream()); 
			ois = new ObjectInputStream(s.getInputStream());

			
			//Calls the sender, which handles the sending of data from the client to the server
			//sender();
			
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}	
		
		//Close the Socket and the Scanner
		cleanUp();
	}
	
	
	public boolean login(String username, String password) {
		try {
			
			//Creates a VerificationMessage with the username and password inputs
			Message message = new VerificationMessage(username, password);
			
			//Sends the VerificationMessage Object to the server
			oos.writeObject(message);
			oos.flush();
			
			boolean response = verificationResponse();
			return response;
			
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
		return false;
	}
	
	private boolean verificationResponse() {
		try {

			//Receives the object
			Object message = ois.readObject();
				
			//checks if the object is an instance of VerificationResponseMessage
			//If it is and user exists, set uid and return true
			System.out.println("checking response: " + message);

			if(message instanceof VerificationResponseMessage) {
				if(((VerificationResponseMessage) message).isVerified()) {
					uid = ((VerificationResponseMessage) message).getUid();
					return true;
				}
				//Let the user know the Verification Failed
				System.out.println("\nVerification failed\n");
				return false;
			} else {
				//Recieved a message that was not a VerificationResponseMessage
				System.out.println("Exception in ChatClient verificationResponse(): Expecting VerificationResponseMessage");
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("cnfe: " + cnfe.getMessage());
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
		
		System.out.println("verification missed");
		return false;
	}

	
	public void send(String text) {
		
		//And send a ChatMessage to the Server
		try {	
			//Creates a ChatMessage with the input
			Message message = new ChatMessage(uid, text);
			
			//Sends the ChatMessage Object to the server
			oos.writeObject(message);
			oos.flush();
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}	
	} 
	
	private void cleanUp() {
		try {
			if (s != null) {
				s.close();
			}
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
	
	
	Task<Void> task = new Task<Void>() {
		@Override protected Void call() throws Exception {
            	try {
        			//Loop consistently looking for an object to be sent from the server
        			while(true) {
        				//Receives the object
        				Object message = ois.readObject();
        				
        				//checks if the object is an instance of StringMessage and prints out
        				if(message instanceof StringMessage) {
        					Platform.runLater(new Runnable() {
        		    	        @Override
        		    	        public void run() {
        		    	        	chatLayout.getChildren().add(new Text(((StringMessage) message).getMessage()));
        		    	        }
        		    	      });
        					
        				} else {
        					System.out.println("Exception in ChatClient run(): Expecting StringMessage");
        				}
        				
        			}
        		} catch (ClassNotFoundException cnfe) {
        			System.out.println("cnfe: " + cnfe.getMessage());
        		} catch (IOException ioe) {
        			System.out.println("ioe: " + ioe.getMessage());
        		}
            	return null;
            }
    };
    
}
	
