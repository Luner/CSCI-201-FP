package listener;

import java.io.File;
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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import objects.message.ChatMessage;
import objects.message.CommandMessage;
import objects.message.CreateUserMessage;
import objects.message.Message;
import objects.message.StringMessage;
import objects.message.VerificationMessage;
import objects.message.VerificationResponseMessage;


public class ChatClient extends Application {
	
//GRAPHICAL USER INTERFACE ------------------

//////////LOGIN WINDOW//////////
	//LogInPage
	Scene loginScene;
	
	//Pane
	AnchorPane loginLayout;
	
	//Labels
	Text usernameLabel;
	Text passwordLabel;
	Text title;
	Text guestUsernameLabel;
	
	//TextFields
	TextField username;
	TextField password;
	TextField guestUsername;
	
	//Login Button
	Button login;
	Button createUser;
	Button guest;
	
	Line devider;
	
//////////Function Bar and Chats//////////
	Scene chatScene;
	
	SplitPane chatLayout;
	AnchorPane leftSide;
	ScrollPane chatsPane;
	VBox chatsButtonsLayout;
	ArrayList<Button> chatsButtons;
	ImageView settings;
	ImageView contacts;
	ImageView profile;
	ImageView add;
	
	
	

	AnchorPane rightSide;
	Text chatName;
//////////CHAT WINDOW//////////
	
	TextField typedMessage;
	TextArea chatText;
	Button sendMessage;
	
	
	
	
//////////PROFILE WINDOW//////////

	ScrollPane profilePane;
	VBox profileLayout;
	HBox spacing1;
	HBox spacing2;
	HBox spacing3;
	HBox spacing4;
	HBox spacing5;
	
	Pane facebookSpacing;
	
	HBox firstNameLayout;
	Text firstNameProfileLabel;
	TextField firstNameProfileInput;
	
	HBox lastNameLayout;
	Text lastNameProfileLabel;
	TextField lastNameProfileInput;
	
	HBox emailLayout;
	Text emailProfileLabel;
	TextField emailProfileInput;
	
	HBox phoneLayout;
	Text phoneProfileLabel;
	TextField phoneProfileInput;
	
	HBox facebookLayout;
	Text facebookProfileLabel;
	ImageView facebookProfileIcon;
	
	
	
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
		//10.14.112.127
		
		if(!setUpChatClient("localhost", 6789)) {
			System.out.println("Failed to initialize object Streams");
		}
		primaryStage.setTitle("Messaging Application");
		initializeLoginPage();
		initializeChatWindow();
		initializeProfileWindow();
		
		//Set what happens on button press
		login.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				if(login(username.getText(), password.getText())) {
					primaryStage.setScene(chatScene);
					Thread th = new Thread(task);
					th.setDaemon(true);
					th.start();
				} 				
			}
		});
		
		createUser.setOnAction(e -> {
			createUser(username.getText(), password.getText());
		});
		
		add.setOnMouseClicked(e -> {
			chatText.setFont(new Font("Helvetica", 18));
			chatText.setText("Enter the users you want to add");
			chatName.setText("Creating Chat");
		});
		
		settings.setOnMouseClicked(e -> {
			chatText.setFont(new Font("Helvetica", 18));
			chatText.setText("What color would you like");
			chatName.setText("Settings");
		});
		
		profile.setOnMouseClicked(e -> {
			chatName.setText("Profile");
			setProfileWindow();
		});
		
		contacts.setOnMouseClicked(e -> {
			chatText.setFont(new Font("Helvetica", 18));
			chatText.setText("Bot1 \nBot2");
			chatName.setText("Contacts");
		});
		
		chatsButtons.get(0).setOnAction(e -> {

			chatText.setFont(new Font("Helvetica", 12));
			chatText.setText("");
			chatName.setText("Main Chat");
			setChatWindow();
		});
		
		sendMessage.setOnAction(e -> {
			send(typedMessage.getText());
			typedMessage.setText("");
		});
		
		primaryStage.setScene(loginScene);
		primaryStage.show();
	}
	
	private boolean setUpChatClient(String hostname, int port) {
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
			return true;
			
			//Calls the sender, which handles the sending of data from the client to the server
			//sender();
			
		} catch (IOException ioe) {
			System.out.println("ioe in set-up: " + ioe.getMessage());
		}	
		return false;
	}
	
	public void createUser(String username, String password) {
		try {
			
			//Creates a VerificationMessage with the username and password inputs
			Message message = new CreateUserMessage(username, password);
			
			//Sends the VerificationMessage Object to the server
			oos.writeObject(message);
			oos.flush();
			
		} catch (IOException ioe) {
			System.out.println("ioe in login : " + ioe.getMessage());
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
			Message message;
			if(text.startsWith("/") || text.startsWith("\\")) {
				message = new CommandMessage(uid, text);
			} else {
				message = new ChatMessage(uid, text);
			}
			
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
        		    	        	if(chatText.getText().length() > 0) {
        		    	        		chatText.setText(chatText.getText() + "\n" + ((StringMessage) message).getMessage());
        		    	        		chatText.setScrollTop(Double.MAX_VALUE);
        		    	        	} else {
        		    	        		chatText.setText(((StringMessage) message).getMessage());
        		    	        	}
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
		loginLayout = new AnchorPane();
		loginLayout.setMaxHeight(Double.NEGATIVE_INFINITY);
		loginLayout.setMaxWidth(Double.NEGATIVE_INFINITY);
		loginLayout.setMinHeight(Double.NEGATIVE_INFINITY);
		loginLayout.setMinWidth(Double.NEGATIVE_INFINITY);
		loginLayout.setPrefHeight(400.0);
		loginLayout.setPrefWidth(600.0);

		//Title text
		title = new Text();
		title.setLayoutX(137.0);
		title.setLayoutY(50.0);
		title.setStrokeType(StrokeType.OUTSIDE);
		title.setStrokeWidth(0.0);
		title.setText("Social Messaging");
		title.setFont(Font.font("Helvetica", 36));
		
		//UsernameLabel
		usernameLabel = new Text();
		usernameLabel.setLayoutX(168.0);
		usernameLabel.setLayoutY(108.0);
		usernameLabel.setStrokeType(StrokeType.OUTSIDE);
		usernameLabel.setStrokeWidth(0.0);
		usernameLabel.setText("Username:");
		usernameLabel.setFont(Font.font("Helvetica", 18));
		
		//UsernameLabel
		passwordLabel = new Text();
		passwordLabel.setLayoutX(172.0);
		passwordLabel.setLayoutY(147.0);
		passwordLabel.setStrokeType(StrokeType.OUTSIDE);
		passwordLabel.setStrokeWidth(0.0);
		passwordLabel.setText("Password:");
		passwordLabel.setFont(Font.font("Helvetica", 18));
		
		//guestUsername text
		guestUsernameLabel = new Text();
		guestUsernameLabel.setLayoutX(168.0);
		guestUsernameLabel.setLayoutY(290.0);
		guestUsernameLabel.setStrokeType(StrokeType.OUTSIDE);
		guestUsernameLabel.setStrokeWidth(0.0);
		guestUsernameLabel.setText("Username:");
		guestUsernameLabel.setFont(Font.font("Helvetica", 18));
		
		//Line
		devider = new Line();
		devider.setEndX(-200);
		devider.setStartX(200);
		devider.setLayoutX(309.0);
		devider.setLayoutY(236.0);
		
		
		//UsernameTextField
		username = new TextField();
		username.setLayoutX(265.0);
		username.setLayoutY(88.0);
		
		//PasswordTextField
		password = new TextField();
		password.setLayoutX(265.0);
		password.setLayoutY(129.0);
		
		//PasswordTextField
		guestUsername = new TextField();
		guestUsername.setLayoutX(265.0);
		guestUsername.setLayoutY(272.0);
		
		//LoginButton
		login = new Button();
		login.setLayoutX(230.0);
		login.setLayoutY(174.0);
		login.setMnemonicParsing(false);
		login.setText("Login");
		
		//create User Button
		createUser = new Button();
		createUser.setLayoutX(313.0);
		createUser.setLayoutY(174.0);
		createUser.setMnemonicParsing(false);
		createUser.setText("Create User");
		
		//create Guest Button
		guest = new Button();
		guest.setLayoutX(281.0);
		guest.setLayoutY(316.0);
		guest.setMnemonicParsing(false);
		guest.setText("Guest");
		
		
		//Add Children to Pane
		loginLayout.getChildren().add(usernameLabel);
		loginLayout.getChildren().add(passwordLabel);
		loginLayout.getChildren().add(guestUsernameLabel);
		loginLayout.getChildren().add(devider);
		loginLayout.getChildren().add(username);
		loginLayout.getChildren().add(password);
		loginLayout.getChildren().add(guestUsername);
		loginLayout.getChildren().add(login);
		loginLayout.getChildren().add(createUser);
		loginLayout.getChildren().add(guest);
		

		loginScene = new Scene(loginLayout);
	}
    
    public void initializeChatWindow() {
   
    	
    	SplitPane chatLayout = new SplitPane();
    	chatLayout.setDividerPositions(0.3);
    	chatLayout.setMaxHeight(Double.NEGATIVE_INFINITY);
    	chatLayout.setMaxWidth(Double.NEGATIVE_INFINITY);
    	chatLayout.setMinHeight(Double.NEGATIVE_INFINITY);
    	chatLayout.setMinWidth(Double.NEGATIVE_INFINITY);
    	chatLayout.setPrefHeight(400.0);
    	chatLayout.setPrefWidth(600.0);

    	leftSide = new AnchorPane();
    	leftSide.setMinHeight(0.0);
    	leftSide.setMinWidth(0.0);
    	leftSide.setPrefHeight(160.0);
    	leftSide.setPrefWidth(100.0);
    	
    	rightSide = new AnchorPane();
    	rightSide.setMinHeight(0.0);
    	rightSide.setMinWidth(0.0);
    	leftSide.setPrefHeight(160.0);
    	rightSide.setPrefWidth(100.0);
    	
    	chatLayout.getItems().add(leftSide);
    	chatLayout.getItems().add(rightSide);
    	
    	typedMessage = new TextField();
    	typedMessage.setLayoutX(2.0);
    	typedMessage.setLayoutY(371.0);
    	typedMessage.setPrefHeight(25.0);
    	typedMessage.setPrefWidth(345.0);
    	
    	
    	chatText = new TextArea();
    	chatText.setLayoutX(2.0);
    	chatText.setLayoutY(33.0);
    	chatText.setPrefHeight(336.0);
    	chatText.setPrefWidth(416.0);
    	chatText.setEditable(false);
    	chatText.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
    
    	chatName = new Text();
    	chatName.setLayoutX(2.0);
    	chatName.setLayoutY(21.0);
    	chatName.setStrokeType(StrokeType.OUTSIDE);
    	chatName.setStrokeWidth(0.0);
    	chatName.setTextAlignment(TextAlignment.CENTER);
    	chatName.setWrappingWidth(416.0);
    	chatName.setFont(new Font("Helvetica", 20));
		chatName.setText("Main Chat");
    	
    	sendMessage = new Button();
    	sendMessage.setLayoutX(350.0);
    	sendMessage.setLayoutY(371.0);
    	sendMessage.setMnemonicParsing(false);
    	sendMessage.setPrefHeight(25.0);
    	sendMessage.setPrefWidth(62.0);
    	sendMessage.setText("Send");

    	rightSide.getChildren().add(typedMessage);
    	rightSide.getChildren().add(chatText);
    	rightSide.getChildren().add(chatName);
    	rightSide.getChildren().add(sendMessage);
    
    	chatsPane = new ScrollPane();
    	chatsPane.setPrefHeight(367.0);
    	chatsPane.setPrefWidth(176.0);
    	
    	chatsButtonsLayout = new VBox();
    	chatsButtonsLayout.setPrefHeight(365.0);
    	chatsButtonsLayout.setPrefWidth(174.0);
    	
    	chatsButtons = new ArrayList<Button>();
    	
    	chatsButtons.add(new Button());
    	chatsButtons.get(0).setMnemonicParsing(false);
    	chatsButtons.get(0).setPrefHeight(50.0);
    	chatsButtons.get(0).setPrefWidth(174.0);
    	chatsButtons.get(0).setText("Open Chat");  	
    	
    	
        
    	settings = new ImageView();
    	settings.setFitHeight(30.0);
    	settings.setFitWidth(40.0);
    	settings.setLayoutX(6.0);
    	settings.setLayoutY(368.0);
    	settings.setPickOnBounds(true);
    	settings.setPreserveRatio(true);
    	
    	File file = new File("images/Settings.png");
    	settings.setImage(new Image(file.toURI().toString()));
    	
    	
    	contacts = new ImageView();
    	contacts.setFitHeight(30.0);
    	contacts.setFitWidth(40.0);
    	contacts.setLayoutX(50.0);
    	contacts.setLayoutY(368.0);
    	contacts.setPickOnBounds(true);
    	contacts.setPreserveRatio(true);
    	
    	file = new File("images/contacts.png");
    	contacts.setImage(new Image(file.toURI().toString()));
    	
    	
    	profile = new ImageView();
    	profile.setFitHeight(30.0);
    	profile.setFitWidth(40.0);
    	profile.setLayoutX(94.0);
    	profile.setLayoutY(368.0);
    	profile.setPickOnBounds(true);
    	profile.setPreserveRatio(true);
    	
    	file = new File("images/profile.png");
    	profile.setImage(new Image(file.toURI().toString()));
    	
    	
    	add = new ImageView();
    	add.setFitHeight(30.0);
    	add.setFitWidth(40.0);
    	add.setLayoutX(139.0);
    	add.setLayoutY(368.0);
    	add.setPickOnBounds(true);
    	add.setPreserveRatio(true);
    	
    	file = new File("images/add.png");
    	add.setImage(new Image(file.toURI().toString()));
    	
    	
    	chatsPane.setContent(chatsButtonsLayout);
    	for(Button button : chatsButtons) {
    		chatsButtonsLayout.getChildren().add(button);
    	}
    	
    	
    	leftSide.getChildren().add(chatsPane);
    	leftSide.getChildren().add(settings);
    	leftSide.getChildren().add(contacts);
    	leftSide.getChildren().add(profile);
    	leftSide.getChildren().add(add);
     	chatScene = new Scene(chatLayout);
    }
    
    private void setChatWindow() {
    	rightSide.getChildren().clear();
    	rightSide.getChildren().add(typedMessage);
    	rightSide.getChildren().add(chatText);
    	rightSide.getChildren().add(chatName);
    	rightSide.getChildren().add(sendMessage);
    
    }
    
    private void setProfileWindow() {
    	rightSide.getChildren().clear();
    	rightSide.getChildren().add(profilePane);
    	rightSide.getChildren().add(chatName);
    }
    
    public void initializeProfileWindow() {
    	profilePane = new ScrollPane();
    	profilePane.setLayoutX(-1.0);
    	profilePane.setLayoutY(-1.0);
    	profilePane.setPrefHeight(358.0);
    	profilePane.setPrefWidth(416.0);
    	
    	
    	profileLayout = new VBox();
    	profileLayout.setPrefHeight(348.0);
    	profileLayout.setPrefWidth(414.0);
    	

    	spacing1 = new HBox();
    	spacing1.setPrefHeight(40.0);
    	spacing1.setPrefWidth(414.0);

    	spacing2 = new HBox();
    	spacing2.setPrefHeight(20.0);
    	spacing2.setPrefWidth(414.0);

    	spacing3 = new HBox();
    	spacing3.setPrefHeight(20.0);
    	spacing3.setPrefWidth(414.0);

    	spacing4 = new HBox();
    	spacing4.setPrefHeight(20.0);
    	spacing4.setPrefWidth(414.0);

    	spacing5 = new HBox();
    	spacing5.setPrefHeight(20.0);
    	spacing5.setPrefWidth(414.0);    	
    	
    	firstNameLayout = new HBox();
    	firstNameLayout.setPrefHeight(20.0);
    	firstNameLayout.setPrefWidth(414.0);
    	
    	firstNameProfileLabel = new Text();
    	firstNameProfileLabel.setStrokeType(StrokeType.OUTSIDE);
    	firstNameProfileLabel.setStrokeWidth(0.0);
    	firstNameProfileLabel.setText("First Name:");
    	firstNameProfileLabel.setTextAlignment(TextAlignment.CENTER);
    	firstNameProfileLabel.setWrappingWidth(214.0);
    	firstNameProfileLabel.setFont(new Font("Helvetica", 18));
    	
    	firstNameProfileInput = new TextField();
    	
    	
    	
    	lastNameLayout = new HBox();
    	lastNameLayout.setPrefHeight(20.0);
    	lastNameLayout.setPrefWidth(414.0);
    	
    	lastNameProfileLabel = new Text();
    	lastNameProfileLabel.setStrokeType(StrokeType.OUTSIDE);
    	lastNameProfileLabel.setStrokeWidth(0.0);
    	lastNameProfileLabel.setText("Last Name:");
    	lastNameProfileLabel.setTextAlignment(TextAlignment.CENTER);
    	lastNameProfileLabel.setWrappingWidth(214.0);
    	lastNameProfileLabel.setFont(new Font("Helvetica", 18));
    	
    	lastNameProfileInput = new TextField();
    	
    	
    	
    	emailLayout = new HBox();
    	emailLayout.setPrefHeight(20.0);
    	emailLayout.setPrefWidth(414.0);
    	
    	emailProfileLabel = new Text();
    	emailProfileLabel.setStrokeType(StrokeType.OUTSIDE);
    	emailProfileLabel.setStrokeWidth(0.0);
    	emailProfileLabel.setText("Email:");
    	emailProfileLabel.setTextAlignment(TextAlignment.CENTER);
    	emailProfileLabel.setWrappingWidth(214.0);
    	emailProfileLabel.setFont(new Font("Helvetica", 18));
    	
    	emailProfileInput = new TextField();
    	
    	
    	
    	phoneLayout = new HBox();
    	phoneLayout.setPrefHeight(20.0);
    	phoneLayout.setPrefWidth(414.0);
    	
    	phoneProfileLabel = new Text();
    	phoneProfileLabel.setStrokeType(StrokeType.OUTSIDE);
    	phoneProfileLabel.setStrokeWidth(0.0);
    	phoneProfileLabel.setText("Phone Number:");
    	phoneProfileLabel.setTextAlignment(TextAlignment.CENTER);
    	phoneProfileLabel.setWrappingWidth(214.0);
    	phoneProfileLabel.setFont(new Font("Helvetica", 18));
    	
    	phoneProfileInput = new TextField();
    	
    	
    	
    	facebookLayout = new HBox();
    	facebookLayout.setPrefHeight(20.0);
    	facebookLayout.setPrefWidth(414.0);
    	
    	facebookProfileLabel = new Text();
    	facebookProfileLabel.setStrokeType(StrokeType.OUTSIDE);
    	facebookProfileLabel.setStrokeWidth(0.0);
    	facebookProfileLabel.setText("Connect to Facebook:");
    	facebookProfileLabel.setTextAlignment(TextAlignment.CENTER);
    	facebookProfileLabel.setWrappingWidth(214.0);
    	facebookProfileLabel.setFont(new Font("Helvetica", 18));
    	
    	facebookSpacing = new Pane();
    	facebookSpacing.setPrefHeight(25.0);
    	facebookSpacing.setPrefWidth(64.0);
    	facebookProfileIcon = new ImageView();
    	facebookProfileIcon.setFitHeight(25.0);
    	facebookProfileIcon.setFitWidth(200.0);
    	facebookProfileIcon.setPickOnBounds(true);
    	facebookProfileIcon.setPreserveRatio(true);
    	
    	File file = new File("images/facebook.png");
    	facebookProfileIcon.setImage(new Image(file.toURI().toString()));
    	
    	
    	profilePane.setContent(profileLayout);
    	
    	profileLayout.getChildren().add(spacing1);
    	
    	profileLayout.getChildren().add(firstNameLayout);
    	firstNameLayout.getChildren().add(firstNameProfileLabel);
    	firstNameLayout.getChildren().add(firstNameProfileInput);

    	profileLayout.getChildren().add(spacing2);
    	
    	profileLayout.getChildren().add(lastNameLayout);
    	lastNameLayout.getChildren().add(lastNameProfileLabel);
    	lastNameLayout.getChildren().add(lastNameProfileInput);

    	profileLayout.getChildren().add(spacing3);
    	
    	profileLayout.getChildren().add(emailLayout);
    	emailLayout.getChildren().add(emailProfileLabel);
    	emailLayout.getChildren().add(emailProfileInput);

    	profileLayout.getChildren().add(spacing4);
    	
    	profileLayout.getChildren().add(phoneLayout);
    	phoneLayout.getChildren().add(phoneProfileLabel);
    	phoneLayout.getChildren().add(phoneProfileInput);

    	profileLayout.getChildren().add(spacing5);
    	
    	profileLayout.getChildren().add(facebookLayout);
    	facebookLayout.getChildren().add(facebookProfileLabel);
    	facebookLayout.getChildren().add(facebookSpacing);
    	facebookLayout.getChildren().add(facebookProfileIcon);
    }
    
}
	
