package listener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import objects.message.ChatMessage;
import objects.message.Message;
import objects.message.StringMessage;
import objects.message.VerificationMessage;
import objects.message.VerificationResponseMessage;


public class ChatClient extends Application {
	
//GRAPHICAL USER INTERFACE ------------------

//////////LOGIN WINDOW//////////
	//LogInPage
	Scene loginScene;
	VBox loginLayout;
	
	//Menu
	MenuBar menu;
	Menu userMenu;
	MenuItem logoutMenuItem;
	
	//Pane
	AnchorPane loginPane;
	
	//Labels
	Label usernameLabel;
	Label passwordLabel;
	
	//TextFields
	TextField username;
	TextField password;
	
	//Login Button
	Button login;
	
	//Decoration
	ArrayList<Circle> circles;
	
	
	
	
	
//////////CHAT WINDOW//////////
	
	//22 messages on screen
	int onScreen = 0;
	Button sendMessage;
	TextField chatText;
	Scene chat;
	VBox chatLayout;
	
//-------------------------------------------
	
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
		
		setUpChatClient("localhost", 6789);
		initializeLoginPage();
		
		//Set what happens on button press
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
		
		primaryStage.setTitle("Messaging Application");
		
		//SetUp chat
		chatText = new TextField();
		sendMessage = new Button();
		sendMessage.setText("Send Message");
		sendMessage.setOnAction(e -> {
			send(chatText.getText());
			chatText.setText("");
		});
		

		chatLayout = new VBox();
		chatLayout.getChildren().add(chatText);	
		chatLayout.getChildren().add(sendMessage);
		
		
		chat = new Scene(chatLayout, 600, 400);
		primaryStage.setScene(loginScene);
		primaryStage.show();
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
			System.out.println("ioe in set-up: " + ioe.getMessage());
		}	
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
			System.out.println("ioe in login : " + ioe.getMessage());
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
        		    	        	if(onScreen == 22) {
        		    	        		chatLayout.getChildren().remove(2);
        		    	        	} else {
        		    	        		onScreen++;
        		    	        	}
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

        		//Close the Socket and the Scanner
        		cleanUp();
            	return null;
            }
    };
    
    
    public void initializeLoginPage() {
		//layout
		loginLayout = new VBox();
		loginLayout.setPrefHeight(400.0);
		loginLayout.setPrefWidth(640.0);
		
		//MenuBar
		menu = new MenuBar();
		VBox.setVgrow(menu, Priority.NEVER);
		
		//Menu
		userMenu = new Menu();
		userMenu.setMnemonicParsing(false);
		userMenu.setText("User");
		
		//MenuItems 
		logoutMenuItem = new MenuItem();
		logoutMenuItem.setText("Logout");
		
		//add UserMenu to MenuBar
		menu.getMenus().add(userMenu);
		//Add logoutMenuItem to userMenu
		userMenu.getItems().add(logoutMenuItem);
		
		//Pane
		loginPane = new AnchorPane();
		loginPane.setMaxHeight(-1.0);
		loginPane.setMaxWidth(-1.0);
		loginPane.setPrefHeight(-1.0);
		loginPane.setPrefWidth(-1.0);
		loginPane.setStyle("-fx-border-color: black; -fx-background-color: grey;");
		VBox.setVgrow(loginPane, Priority.ALWAYS);
		
		//Font
		
		
		//UsernameLabel
		usernameLabel = new Label();
		usernameLabel.setLayoutX(147.0);
		usernameLabel.setLayoutY(79.0);
		usernameLabel.setPrefHeight(53);
		usernameLabel.setPrefWidth(140);
		usernameLabel.setText("Username:");
		usernameLabel.setFont(Font.font(30));
		
		//UsernameLabel
		passwordLabel = new Label();
		passwordLabel.setLayoutX(154.0);
		passwordLabel.setLayoutY(132.0);
		passwordLabel.setPrefHeight(45);
		passwordLabel.setPrefWidth(133);
		passwordLabel.setText("Password:");
		passwordLabel.setFont(Font.font(30));
		
		//UsernameTextField
		username = new TextField();
		username.setLayoutX(293.0);
		username.setLayoutY(94.0);
		username.setPrefHeight(26);
		username.setPrefWidth(200);
		
		//PasswordTextField
		password = new TextField();
		password.setLayoutX(293.0);
		password.setLayoutY(142.0);
		password.setPrefHeight(26);
		password.setPrefWidth(200);
		
		//LoginButton
		login = new Button();
		login.setLayoutX(278.0);
		login.setLayoutY(202.0);
		login.setMnemonicParsing(false);
		login.setPrefWidth(62.0);
		login.setStyle("-fx-background-color: #59f429;");
		login.setText("LogIn");
		
		
		//Circles
		circles = new ArrayList<Circle>();
		
		for(int i = 0; i < 12; i++) {
			Circle c = new Circle();
			c.setFill(Color.web("#59f429"));
			c.setStroke(Color.BLACK);
			c.setStrokeType(StrokeType.INSIDE);
			circles.add(c);
		}
		
		Circle temp = circles.get(0);
		temp.setLayoutX(79.0);
		temp.setLayoutY(303.0);
		temp.setRadius(41);
		
		temp = circles.get(1);
		temp.setLayoutX(190.0);
		temp.setLayoutY(263.0);
		temp.setRadius(27);
	
		temp = circles.get(2);
		temp.setLayoutX(96.0);
		temp.setLayoutY(211.0);
		temp.setRadius(20);
		
		temp = circles.get(3);
		temp.setLayoutX(81.0);
		temp.setLayoutY(76.0);
		temp.setRadius(36);
		
		temp = circles.get(4);
		temp.setLayoutX(309.0);
		temp.setLayoutY(314.0);
		temp.setRadius(36);
		
		temp = circles.get(5);
		temp.setLayoutX(415.0);
		temp.setLayoutY(240.0);
		temp.setRadius(22);
		
		temp = circles.get(6);
		temp.setLayoutX(539.0);
		temp.setLayoutY(290.0);
		temp.setRadius(60);
		
		temp = circles.get(7);
		temp.setLayoutX(586.0);
		temp.setLayoutY(132.0);
		temp.setRadius(31);
		
		temp = circles.get(8);
		temp.setLayoutX(519.0);
		temp.setLayoutY(57.0);
		temp.setRadius(22);
		
		temp = circles.get(9);
		temp.setLayoutX(420.0);
		temp.setLayoutY(47.0);
		temp.setRadius(27);
		
		temp = circles.get(10);
		temp.setLayoutX(293.0);
		temp.setLayoutY(59.0);
		temp.setRadius(16);
		
		temp = circles.get(11);
		temp.setLayoutX(207.0);
		temp.setLayoutY(47.0);
		temp.setRadius(22);
		
		
		
		//Add Children to Pane
		loginPane.getChildren().add(usernameLabel);
		loginPane.getChildren().add(passwordLabel);
		loginPane.getChildren().add(username);
		loginPane.getChildren().add(password);
		loginPane.getChildren().add(login);
		for(Circle circle : circles){
			loginPane.getChildren().add(circle);
		}
		
		//Add Children to Layout
		loginLayout.getChildren().add(menu);
		loginLayout.getChildren().add(loginPane);
		

		loginScene = new Scene(loginLayout);
	}
    
}
	
