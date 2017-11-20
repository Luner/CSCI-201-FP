package listener;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import objects.ClientConversation;
import objects.message.ChatMessage;
import objects.message.ChatStringMessage;
import objects.message.CommandMessage;
import objects.message.ConversationsMessage;
import objects.message.CreateConversationMessage;
import objects.message.CreateUserMessage;
import objects.message.Message;
import objects.message.ProfileMessage;
import objects.message.MessagesMessage;
import objects.message.VerificationMessage;
import objects.message.VerificationResponseMessage;

public class ChatClient extends Application {

	// GRAPHICAL USER INTERFACE ------------------

	////////// IP SELECT/////////////
	AnchorPane ipSelect;
	Scene ipSelectScene;
	TextField ipEntry;
	Button ipEnter;
	Text ipSelectTitle;
	
	////////// LOGIN WINDOW//////////
	// LogInPage
	Scene loginScene;

	// Pane
	AnchorPane loginLayout;

	// Labels
	Text usernameLabel;
	Text passwordLabel;
	Text title;
	Text guestUsernameLabel;

	// TextFields
	TextField username;
	PasswordField password;
	TextField guestUsername;

	// Login Button
	Button login;
	Button createUser;
	Button guestButton;

	Line devider;

	//////////CONTACTS WINDOW//////////
	ScrollPane contactsPane;
	VBox contactsLayout;
	ArrayList<HBox> contactsList;
	ArrayList<String> contactsFromServer;
	ArrayList<Text> contactsTextList;
	
	////////// Function Bar and Chats//////////
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
	////////// CHAT WINDOW//////////

	TextField typedMessage;
	TextArea chatText;
	Button sendMessage;

	////////// PROFILE WINDOW//////////

	Pane profilePane;
	VBox profileLayout;
	
	ImageView profileImage;
	Label profileUsernameLabel;
	Separator profileSeparator1;
	
	HBox profileFNameHBox;
	Label profileFNameLabel;
	TextField profileFNameField;
	Separator profileSeparator2;
	
	HBox profileLNameHBox;
	Label profileLNameLabel;
	TextField profileLNameField;
	Separator profileSeparator3;
	
	HBox profileEmailHBox;
	Label profileEmailLabel;
	TextField profileEmailField;
	Separator profileSeparator4;
	
	HBox profilePhoneHBox;
	Label profilePhoneLabel;
	TextField profilePhoneField;
	Separator profileSeparator5;
	
	HBox profileBioHBox;
	Label profileBioLabel;
	TextArea profileBioArea;
	Separator profileSeparator6;
	
	HBox profileInterestsHBox;
	Label profileInterestsLabel;
	TextArea profileInterestsArea;
	Separator profileSeparator7;
	
	HBox profileSocialsHBox;
	Label profileSocialsLabel;
	ImageView profileFBIcon;
	Button profileUpdateButton;
	
	// -------------------------------------------

	////////// ADD CHAT WINDOW//////////

	ScrollPane addConversationPane;
	VBox addConversationLayout;
	HBox addConversationspacing1;
	HBox addConversationspacing2;
	HBox addConversationspacing3;
	HBox addConversationspacing4;
	HBox addConversationspacing5;
	HBox addConversationspacing6;

	HBox User1Layout;
	Text User1Label;
	TextField User1Input;

	HBox User2Layout;
	Text User2Label;
	TextField User2Input;

	HBox User3Layout;
	Text User3Label;
	TextField User3Input;

	HBox User4Layout;
	Text User4Label;
	TextField User4Input;

	HBox addConversationBox;
	Text addConversationText;

	HBox addConversationButtonBox;
	Button addConversationButton;

	// -------------------------------------------

	// SERVER CLIENT COMMUNICATION
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Socket s;
	private Integer selectedChat;
	private Map<Button, Integer> chatsMap;
	
	// client properties
	private int uid;
	private String userName;
	private String fName;
	private String lName;
	private String email;
	private String number;
	private String bio;
	private String interests;
	private boolean guest;

	private String user_Username;
	private Map<Integer, ArrayList<String>> chatHistory;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// 10.14.112.127

		primaryStage.setTitle("Messaging Application");
		chatsMap = new HashMap<Button, Integer>();
		initializeIPSelect();
		initializeLoginPage();
		initializeChatWindow();
		initializeProfileWindow();
		initializeAddConversationWindow();
		ipEnter.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				if (!setUpChatClient(ipEntry.getText(), 6789)) {
					System.out.println("Failed to connect to specified server.");
					System.exit(0);
				}
				else {
					primaryStage.setScene(loginScene);
				}
			}
		});

		// Set what happens on button press
		login.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (login(username.getText(), password.getText())) {
					user_Username = username.getText();
					addFunctions();
					primaryStage.setScene(chatScene);
					guest = false;
					Thread th = new Thread(task);
					th.setDaemon(true);
					th.start();
				}
			}
		});

		addConversationButton.setOnAction(e -> {
			String user1 = User1Input.getText();
			String user2 = User2Input.getText();
			String user3 = User3Input.getText();
			String user4 = User4Input.getText();

			User1Input.setText("");
			User2Input.setText("");
			User3Input.setText("");
			User4Input.setText("");
		
			createConversation(user1, user2, user3, user4);
			chatText.setFont(new Font("Helvetica", 12));
			chatText.setText("");

			chatName.setText("Global Chat");
			selectedChat = 1;
			setChatWindow();
		});
		
		guestButton.setOnAction(e -> {
			if (login("Guest", "None")) {
				primaryStage.setScene(chatScene);
				guest = true;
				Thread th = new Thread(task);
				th.setDaemon(true);
				th.start();
			}
		});

		createUser.setOnAction(e -> {
			createUser(username.getText(), password.getText());
		});

		add.setOnMouseClicked(e -> {
			chatText.setFont(new Font("Helvetica", 18));
			chatName.setText("Creating Chat");
			setAddConversationWindow();
		});

		settings.setOnMouseClicked(e -> {
			chatText.setFont(new Font("Helvetica", 18));
			chatText.setText("What color would you like");
			chatName.setText("Settings");
		});

		profile.setOnMouseClicked(e -> {
			rightSide.getChildren().clear();
			rightSide.getChildren().add(profilePane);
		});

		contacts.setOnMouseClicked(e -> {
			chatText.setFont(new Font("Helvetica", 18));
//			chatText.setText("Bot1 \nBot2");
			chatName.setText("Contacts");
			setContactsWindow();
		});

		sendMessage.setOnAction(e -> {
			send(typedMessage.getText());
			typedMessage.setText("");
		});
		
		profileUpdateButton.setOnMouseClicked(e -> {
			fName = profileFNameField.getText();
			lName = profileLNameField.getText();
			email = profileEmailField.getText();
			number = profilePhoneField.getText();
			bio = profileBioArea.getText();
			interests = profileInterestsArea.getText();
			
			if(!guest) {	
				try {
					ProfileMessage pm = new ProfileMessage(uid,fName,lName,email,number,bio,interests); 
					oos.writeObject(pm);
					oos.flush();
					System.out.println("ProfileMessage sent.\n"+pm.toString());
				}catch(IOException ioe) {
					System.out.println("ioe during profileUpdate: "+ioe.getMessage());
				}
			}
			
		});


		primaryStage.setScene(ipSelectScene);
		primaryStage.show();
		
	}

	private boolean setUpChatClient(String hostname, int port) {
		s = null;
		uid = -1;
		selectedChat = 1; // default
		try {
			// Attempts to connect to the Socket
			s = new Socket(hostname, port);
			// Creates a new scanner to receive information from the console

			/*
			 * If successful, will create ObjectStreams to allow for the sending of objects
			 * to and from the server
			 */
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			return true;

			// Calls the sender, which handles the sending of data from the client to the
			// server
			// sender();

		} catch (IOException ioe) {
			//System.out.println("ioe in set-up: " + ioe.getMessage());
		}
		return false;
	}

	public void createUser(String username, String password) {
		try {

			// Creates a VerificationMessage with the username and password inputs
			Message message = new CreateUserMessage(username, password);

			// Sends the VerificationMessage Object to the server
			oos.writeObject(message);
			oos.flush();

		} catch (IOException ioe) {
			System.out.println("ioe in login : " + ioe.getMessage());
		}
	}

	public boolean login(String username, String password) {
		try {

			// Creates a VerificationMessage with the username and password inputs
			Message message = new VerificationMessage(username, password);
			
			// Sends the VerificationMessage Object to the server
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

			// Receives the object
			Object message = ois.readObject();

			// checks if the object is an instance of VerificationResponseMessage
			// If it is and user exists, set uid and return true
			System.out.println("checking response: " + message);

			if (message instanceof VerificationResponseMessage) {
				if (((VerificationResponseMessage) message).isVerified()) {
					uid = ((VerificationResponseMessage) message).getUid();
					return true;
				}
				// Let the user know the Verification Failed
				System.out.println("\nVerification failed\n");
				return false;
			} else {
				// Recieved a message that was not a VerificationResponseMessage
				System.out.println(
						"Exception in ChatClient verificationResponse(): Expecting VerificationResponseMessage");
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

		// And send a ChatMessage to the Server
		try {
			// Creates a ChatMessage with the input
			Message message;
			if (text.startsWith("/") || text.startsWith("\\")) {
				message = new CommandMessage(uid, text);
			} else {
				message = new ChatMessage(uid, selectedChat, text);
			}

			// Sends the ChatMessage Object to the server
			oos.writeObject(message);
			oos.flush();
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}

	private void createConversation(String user1, String user2, String user3, String user4) {
		Message message = new CreateConversationMessage(user_Username, user1, user2, user3, user4);
		try {
			oos.writeObject(message);
			oos.flush();
		} catch (IOException e) {
				System.out.println("ioe in createConversation");
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
		@Override
		protected Void call() throws Exception {
			try {
				// Loop consistently looking for an object to be sent from the server
				while (true) {
					// Receives the object
					Object message = ois.readObject();

					// checks if the object is an instance of StringMessage and prints out
					if (message instanceof ChatStringMessage) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								if (((ChatStringMessage) message).getChatID().equals(selectedChat)) {
									String chatMessageString = ((ChatStringMessage) message).getMessage();
									chatMessageString = chatMessageString.replace("!@#$%^&*()", user_Username);
									if (chatText.getText().length() > 0) {
										chatText.setText(
												chatText.getText() + "\n" + chatMessageString);
										chatText.setScrollTop(Double.MAX_VALUE);
									} else {
										chatText.setText(chatMessageString);
									}
								}
							}
						});

					} else if (message instanceof ConversationsMessage) {
						ArrayList<ClientConversation> chats = ((ConversationsMessage) message).getChats();
						updateClientChats(chats);

						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								chatsButtonsLayout.getChildren().clear();
								for (Button button : chatsButtons) {
									chatsButtonsLayout.getChildren().add(button);
								}
								for (Integer i = 0; i < chatsButtons.size(); i++) {
									Button button = chatsButtons.get(i);
									button.setOnAction(e -> {
										chatText.setFont(new Font("Helvetica", 12));
										chatText.setText("");
										int chatid = chatsMap.get(button);
										for(int j = 0; j < chatHistory.get(chatid).size(); j++)
										{
											if(j == 0) {
												chatText.setText(chatHistory.get(chatid).get(j));
											} else {
												
												chatText.setText(chatText.getText() + "\n" + chatHistory.get(chatid).get(j));
											}
										}
										if (chatid == 0) {
											chatName.setText("Global Chat");
										} else {
											chatName.setText("Chat: " + chatid);
										}
										selectedChat = chatid;

										setChatWindow();
									});
								}
							}
						});
					} else if(message instanceof ProfileMessage){
						ProfileMessage pm = (ProfileMessage)message;
						fName = pm.getFName();
						lName = pm.getLName();
						email = pm.getEmail();
						number = pm.getNumber();
						bio = pm.getBio();
						interests = pm.getInterests();
						populateProfileWindow();
					} else if(message instanceof MessagesMessage){
						System.out.println("YES!!!!");
						chatHistory = ((MessagesMessage) message).getMessage();
						
						for (Entry<Integer, ArrayList<String>> entry : chatHistory.entrySet()) {
							for(String s : entry.getValue()) {
								System.out.println("Conversation: " + entry.getKey() + " Message: " + s);
							}
						}
					} else {
						System.out.println("Exception in ChatClient run(): Expecting certain messages");
					}

				}
			} catch (ClassNotFoundException cnfe) {
				System.out.println("cnfe: " + cnfe.getMessage());
			} catch (IOException ioe) {
				System.out.println("ioe: " + ioe.getMessage());
			}

			// Close the Socket and the Scanner
			cleanUp();
			return null;

		}
	};
	public void initializeIPSelect() {
		//IP Pane
		ipSelect = new AnchorPane();
		ipSelect.setMaxHeight(Double.NEGATIVE_INFINITY);
		ipSelect.setMaxWidth(Double.NEGATIVE_INFINITY);
		ipSelect.setMinHeight(Double.NEGATIVE_INFINITY);
		ipSelect.setMinWidth(Double.NEGATIVE_INFINITY);
		ipSelect.setPrefHeight(400.0);
		ipSelect.setPrefWidth(600.0);
		
		//IP Title
		ipSelectTitle = new Text();
		ipSelectTitle.setLayoutX(190.0);
		ipSelectTitle.setLayoutY(84.0);
		ipSelectTitle.setStrokeType(StrokeType.OUTSIDE);
		ipSelectTitle.setStrokeWidth(0.0);
		ipSelectTitle.setText("Server Connection");
		ipSelectTitle.setFont(Font.font("Helvetica", 27));
		
		//IP Entry
		ipEntry = new TextField();
		ipEntry.setLayoutX(207.0);
		ipEntry.setLayoutY(185.0);
		ipEntry.setPromptText("Enter IP");
		
		//IP Submit
		ipEnter = new Button();
		ipEnter.setLayoutX(273.0);
		ipEnter.setLayoutY(261.0);
		ipEnter.setText("Enter");
		ipEnter.setMnemonicParsing(false);
		
		//Finalization
		ipSelect.getChildren().add(ipEnter);
		ipSelect.getChildren().add(ipEntry);
		ipSelect.getChildren().add(ipSelectTitle);
		
		//New Scene
		ipSelectScene = new Scene(ipSelect);
	}
	
	public void initializeLoginPage() {
		// layout
		loginLayout = new AnchorPane();
		loginLayout.setMaxHeight(Double.NEGATIVE_INFINITY);
		loginLayout.setMaxWidth(Double.NEGATIVE_INFINITY);
		loginLayout.setMinHeight(Double.NEGATIVE_INFINITY);
		loginLayout.setMinWidth(Double.NEGATIVE_INFINITY);
		loginLayout.setPrefHeight(400.0);
		loginLayout.setPrefWidth(600.0);

		// Title text
		title = new Text();
		title.setLayoutX(137.0);
		title.setLayoutY(50.0);
		title.setStrokeType(StrokeType.OUTSIDE);
		title.setStrokeWidth(0.0);
		title.setText("Social Messaging");
		title.setFont(Font.font("Helvetica", 36));

		// UsernameLabel
		usernameLabel = new Text();
		usernameLabel.setLayoutX(168.0);
		usernameLabel.setLayoutY(108.0);
		usernameLabel.setStrokeType(StrokeType.OUTSIDE);
		usernameLabel.setStrokeWidth(0.0);
		usernameLabel.setText("Username:");
		usernameLabel.setFont(Font.font("Helvetica", 18));

		// UsernameLabel
		passwordLabel = new Text();
		passwordLabel.setLayoutX(172.0);
		passwordLabel.setLayoutY(147.0);
		passwordLabel.setStrokeType(StrokeType.OUTSIDE);
		passwordLabel.setStrokeWidth(0.0);
		passwordLabel.setText("Password:");
		passwordLabel.setFont(Font.font("Helvetica", 18));

		// guestUsername text
		guestUsernameLabel = new Text();
		guestUsernameLabel.setLayoutX(168.0);
		guestUsernameLabel.setLayoutY(290.0);
		guestUsernameLabel.setStrokeType(StrokeType.OUTSIDE);
		guestUsernameLabel.setStrokeWidth(0.0);
		guestUsernameLabel.setText("Username:");
		guestUsernameLabel.setFont(Font.font("Helvetica", 18));

		// Line
		devider = new Line();
		devider.setEndX(-200);
		devider.setStartX(200);
		devider.setLayoutX(309.0);
		devider.setLayoutY(236.0);

		// UsernameTextField
		username = new TextField();
		username.setLayoutX(265.0);
		username.setLayoutY(88.0);

		// PasswordTextField
		password = new PasswordField();
		password.setLayoutX(265.0);
		password.setLayoutY(129.0);

		// PasswordTextField
		guestUsername = new TextField();
		guestUsername.setLayoutX(265.0);
		guestUsername.setLayoutY(272.0);

		// LoginButton
		login = new Button();
		login.setLayoutX(230.0);
		login.setLayoutY(174.0);
		login.setMnemonicParsing(false);
		login.setText("Login");

		// create User Button
		createUser = new Button();
		createUser.setLayoutX(313.0);
		createUser.setLayoutY(174.0);
		createUser.setMnemonicParsing(false);
		createUser.setText("Create User");

		// create Guest Button
		guestButton = new Button();
		guestButton.setLayoutX(281.0);
		guestButton.setLayoutY(316.0);
		guestButton.setMnemonicParsing(false);
		guestButton.setText("Guest");

		// Add Children to Pane
		loginLayout.getChildren().add(usernameLabel);
		loginLayout.getChildren().add(passwordLabel);
		loginLayout.getChildren().add(guestUsernameLabel);
		loginLayout.getChildren().add(devider);
		loginLayout.getChildren().add(username);
		loginLayout.getChildren().add(password);
		loginLayout.getChildren().add(guestUsername);
		loginLayout.getChildren().add(login);
		loginLayout.getChildren().add(createUser);
		loginLayout.getChildren().add(guestButton);

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
		chatName.setText("Global Chat");

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
		chatsButtonsLayout.setPrefWidth(154.0);

		chatsButtons = new ArrayList<Button>();

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

		leftSide.getChildren().add(chatsPane);
		chatScene = new Scene(chatLayout);
	}

	private void addFunctions() {
		leftSide.getChildren().add(settings);
		leftSide.getChildren().add(contacts);
		leftSide.getChildren().add(profile);
		leftSide.getChildren().add(add);
	}

	private void setChatWindow() {
		rightSide.getChildren().clear();
		rightSide.getChildren().add(typedMessage);
		rightSide.getChildren().add(chatText);
		rightSide.getChildren().add(chatName);
		rightSide.getChildren().add(sendMessage);

	}

	private void updateClientChats(ArrayList<ClientConversation> chats) {

		chatsButtons.clear();

		// Add New

		for (int i = 0; i < chats.size(); i++) {
			ClientConversation chat = chats.get(i);
			chatsButtons.add(new Button());
			chatsButtons.get(i).setMnemonicParsing(false);
			chatsButtons.get(i).setPrefHeight(50.0);
			chatsButtons.get(i).setPrefWidth(174.0);

			if (i == 0) {
				chatsButtons.get(i).setText("Global Chat");
			} else {
				chatsButtons.get(i).setText("Chat: " + chat.getConversationID());
			}
			chatsMap.put(chatsButtons.get(i), chat.getConversationID());
		}

		// change height of vbox
		chatsButtonsLayout.setPrefHeight(chats.size() * 52.0);
	}

	public void initializeProfileWindow() {
		profilePane = new Pane();
		profilePane.setPrefHeight(400);
		profilePane.setPrefWidth(400);

		profileLayout = new VBox();
		profileLayout.setPrefHeight(400);
		profileLayout.setPrefWidth(400);
		profileLayout.setAlignment(Pos.TOP_CENTER);
		
		profileImage = new ImageView();
		profileImage.setFitHeight(80);
		profileImage.setFitWidth(80);
		profileImage.setPickOnBounds(true);
		profileImage.setPreserveRatio(true);
		VBox.setMargin(profileImage,new Insets(10,20,0,20));
		profileUsernameLabel = new Label("Username");
		
		profileSeparator1 = new Separator();
		profileSeparator1.setPrefWidth(200);
		
		profileFNameHBox = new HBox();
		profileFNameHBox.setPrefHeight(24);
		profileFNameHBox.setPrefWidth(394);
		profileFNameLabel = new Label("First Name:");
		profileFNameLabel.setPrefHeight(17);
		profileFNameLabel.setPrefWidth(116);
		profileFNameField = new TextField();
		profileFNameField.setPrefHeight(20);
		profileFNameField.setPrefWidth(242);
		profileFNameField.setEditable(true);
		VBox.setMargin(profileFNameHBox, new Insets(2,20,2,20));
		profileSeparator2 = new Separator();
		profileSeparator2.setPrefWidth(200);
		
		profileLNameHBox = new HBox();
		profileLNameHBox.setPrefHeight(24);
		profileLNameHBox.setPrefWidth(394);
		profileLNameLabel = new Label("Last Name:");
		profileLNameLabel.setPrefHeight(17);
		profileLNameLabel.setPrefWidth(116);
		profileLNameField = new TextField();
		profileLNameField.setPrefHeight(20);
		profileLNameField.setPrefWidth(242);
		profileLNameField.setEditable(true);
		VBox.setMargin(profileLNameHBox, new Insets(2,20,2,20));
		profileSeparator3 = new Separator();
		profileSeparator3.setPrefWidth(200);
		
		profileEmailHBox = new HBox();
		profileEmailHBox.setPrefHeight(24);
		profileEmailHBox.setPrefWidth(394);
		profileEmailLabel = new Label("Email:");
		profileEmailLabel.setPrefHeight(17);
		profileEmailLabel.setPrefWidth(116);
		profileEmailField = new TextField();
		profileEmailField.setPrefHeight(20);
		profileEmailField.setPrefWidth(242);
		profileEmailField.setEditable(true);
		VBox.setMargin(profileEmailHBox, new Insets(2,20,2,20));
		profileSeparator4 = new Separator();
		profileSeparator4.setPrefWidth(200);
		
		profilePhoneHBox = new HBox();
		profilePhoneHBox.setPrefHeight(24);
		profilePhoneHBox.setPrefWidth(394);
		profilePhoneLabel = new Label("Phone:");
		profilePhoneLabel.setPrefHeight(17);
		profilePhoneLabel.setPrefWidth(116);
		profilePhoneField = new TextField();
		profilePhoneField.setPrefHeight(20);
		profilePhoneField.setPrefWidth(242);
		profilePhoneField.setEditable(true);
		VBox.setMargin(profilePhoneHBox, new Insets(2,20,2,20));
		profileSeparator5 = new Separator();
		profileSeparator5.setPrefWidth(200);
		
		profileBioHBox = new HBox();
		profileBioHBox.setPrefHeight(48);
		profileBioHBox.setPrefWidth(394);
		profileBioLabel = new Label("Biography:");
		profileBioLabel.setPrefHeight(17);
		profileBioLabel.setPrefWidth(116);
		profileBioArea = new TextArea();
		profileBioArea.setPrefHeight(48);
		profileBioArea.setPrefWidth(242);
		profileBioArea.setEditable(true);
		VBox.setMargin(profileBioHBox, new Insets(2,20,2,20));
		profileSeparator6 = new Separator();
		profileSeparator6.setPrefWidth(200);
		
		profileInterestsHBox = new HBox();
		profileInterestsHBox.setPrefHeight(48);
		profileInterestsHBox.setPrefWidth(394);
		profileInterestsLabel = new Label("Interests:");
		profileInterestsLabel.setPrefHeight(17);
		profileInterestsLabel.setPrefWidth(116);
		profileInterestsArea = new TextArea();
		profileInterestsArea.setPrefHeight(48);
		profileInterestsArea.setPrefWidth(242);
		profileInterestsArea.setEditable(true);
		VBox.setMargin(profileInterestsHBox, new Insets(2,20,2,20));
		profileSeparator7 = new Separator();
		profileSeparator7.setPrefWidth(200);
		
		profileSocialsHBox = new HBox();
		profileSocialsHBox.setPrefHeight(48);
		profileSocialsHBox.setPrefWidth(394);
		profileSocialsLabel = new Label("Social Media:");
		profileSocialsLabel.setPrefHeight(17);
		profileSocialsLabel.setPrefWidth(116);
		profileFBIcon = new ImageView();
		profileFBIcon.setFitHeight(40);
		profileFBIcon.setFitWidth(40);
		profileFBIcon.setPickOnBounds(true);
		profileFBIcon.setPreserveRatio(true);
		File file = new File("images/facebook.png");
		profileFBIcon.setImage(new Image(file.toURI().toString()));
		profileUpdateButton = new Button("Update Profile");
		HBox.setMargin(profileUpdateButton,new Insets(8,0,0,105));
		VBox.setMargin(profileSocialsHBox, new Insets(2,20,2,20));
		
		profilePane.getChildren().add(profileLayout);
		profileLayout.getChildren().addAll(profileImage,
											profileUsernameLabel,
											profileSeparator1,
											profileFNameHBox,
											profileSeparator2,
											profileLNameHBox,
											profileSeparator3,
											profileEmailHBox,
											profileSeparator4,
											profilePhoneHBox,
											profileSeparator5,
											profileBioHBox,
											profileSeparator6,
											profileInterestsHBox,
											profileSeparator7,
											profileSocialsHBox);
		
		profileFNameHBox.getChildren().addAll(profileFNameLabel,profileFNameField);
		profileLNameHBox.getChildren().addAll(profileLNameLabel,profileLNameField);
		profileEmailHBox.getChildren().addAll(profileEmailLabel,profileEmailField);
		profilePhoneHBox.getChildren().addAll(profilePhoneLabel,profilePhoneField);
		profileBioHBox.getChildren().addAll(profileBioLabel,profileBioArea);
		profileInterestsHBox.getChildren().addAll(profileInterestsLabel,profileInterestsArea);
		profileSocialsHBox.getChildren().addAll(profileSocialsLabel,profileFBIcon,profileUpdateButton);
		
	}

	private void setAddConversationWindow() {
		rightSide.getChildren().clear();
		rightSide.getChildren().add(addConversationPane);
		rightSide.getChildren().add(chatName);
	}

	public void initializeAddConversationWindow() {
		addConversationPane = new ScrollPane();
		addConversationPane.setLayoutX(-1.0);
		addConversationPane.setLayoutY(-1.0);
		addConversationPane.setPrefHeight(358.0);
		addConversationPane.setPrefWidth(416.0);

		addConversationLayout = new VBox();
		addConversationLayout.setPrefHeight(348.0);
		addConversationLayout.setPrefWidth(414.0);

		addConversationspacing1 = new HBox();
		addConversationspacing1.setPrefHeight(40.0);
		addConversationspacing1.setPrefWidth(414.0);

		addConversationspacing2 = new HBox();
		addConversationspacing2.setPrefHeight(20.0);
		addConversationspacing2.setPrefWidth(414.0);

		addConversationspacing3 = new HBox();
		addConversationspacing3.setPrefHeight(20.0);
		addConversationspacing3.setPrefWidth(414.0);

		addConversationspacing4 = new HBox();
		addConversationspacing4.setPrefHeight(20.0);
		addConversationspacing4.setPrefWidth(414.0);

		addConversationspacing5 = new HBox();
		addConversationspacing5.setPrefHeight(20.0);
		addConversationspacing5.setPrefWidth(414.0);

		addConversationspacing6 = new HBox();
		addConversationspacing6.setPrefHeight(20.0);
		addConversationspacing6.setPrefWidth(414.0);

		User1Layout = new HBox();
		User1Layout.setPrefHeight(20.0);
		User1Layout.setPrefWidth(414.0);

		User1Label = new Text();
		User1Label.setStrokeType(StrokeType.OUTSIDE);
		User1Label.setStrokeWidth(0.0);
		User1Label.setText("User1:");
		User1Label.setTextAlignment(TextAlignment.CENTER);
		User1Label.setWrappingWidth(214.0);
		User1Label.setFont(new Font("Helvetica", 18));

		User1Input = new TextField();

		User2Layout = new HBox();
		User2Layout.setPrefHeight(20.0);
		User2Layout.setPrefWidth(414.0);

		User2Label = new Text();
		User2Label.setStrokeType(StrokeType.OUTSIDE);
		User2Label.setStrokeWidth(0.0);
		User2Label.setText("User2:");
		User2Label.setTextAlignment(TextAlignment.CENTER);
		User2Label.setWrappingWidth(214.0);
		User2Label.setFont(new Font("Helvetica", 18));

		User2Input = new TextField();

		User3Layout = new HBox();
		User3Layout.setPrefHeight(20.0);
		User3Layout.setPrefWidth(414.0);

		User3Label = new Text();
		User3Label.setStrokeType(StrokeType.OUTSIDE);
		User3Label.setStrokeWidth(0.0);
		User3Label.setText("User3:");
		User3Label.setTextAlignment(TextAlignment.CENTER);
		User3Label.setWrappingWidth(214.0);
		User3Label.setFont(new Font("Helvetica", 18));

		User3Input = new TextField();

		User4Layout = new HBox();
		User4Layout.setPrefHeight(20.0);
		User4Layout.setPrefWidth(414.0);

		User4Label = new Text();
		User4Label.setStrokeType(StrokeType.OUTSIDE);
		User4Label.setStrokeWidth(0.0);
		User4Label.setText("User4:");
		User4Label.setTextAlignment(TextAlignment.CENTER);
		User4Label.setWrappingWidth(214.0);
		User4Label.setFont(new Font("Helvetica", 18));

		User4Input = new TextField();

		addConversationBox = new HBox();
		addConversationBox.setPrefHeight(20.0);
		addConversationBox.setPrefWidth(414.0);

		addConversationText = new Text();
		addConversationText.setStrokeType(StrokeType.OUTSIDE);
		addConversationText.setStrokeWidth(0.0);
		addConversationText.setText("Create a chat with up to four other users");
		addConversationText.setTextAlignment(TextAlignment.CENTER);
		addConversationText.setWrappingWidth(412.0);
		addConversationText.setFont(new Font("Helvetica", 12));

		addConversationButtonBox = new HBox();
		addConversationButtonBox.setPrefHeight(20.0);
		addConversationButtonBox.setPrefWidth(414.0);

		addConversationButton = new Button();
		addConversationButton.setMnemonicParsing(false);
		addConversationButton.setText("Create");
		HBox.setMargin(addConversationButton, new Insets(0, 0, 0, 175));

		addConversationPane.setContent(addConversationLayout);

		addConversationLayout.getChildren().add(addConversationspacing1);

		addConversationLayout.getChildren().add(User1Layout);
		User1Layout.getChildren().add(User1Label);
		User1Layout.getChildren().add(User1Input);

		addConversationLayout.getChildren().add(addConversationspacing2);

		addConversationLayout.getChildren().add(User2Layout);
		User2Layout.getChildren().add(User2Label);
		User2Layout.getChildren().add(User2Input);

		addConversationLayout.getChildren().add(addConversationspacing3);

		addConversationLayout.getChildren().add(User3Layout);
		User3Layout.getChildren().add(User3Label);
		User3Layout.getChildren().add(User3Input);

		addConversationLayout.getChildren().add(addConversationspacing4);

		addConversationLayout.getChildren().add(User4Layout);
		User4Layout.getChildren().add(User4Label);
		User4Layout.getChildren().add(User4Input);

		addConversationLayout.getChildren().add(addConversationspacing5);

		addConversationLayout.getChildren().add(addConversationBox);
		addConversationBox.getChildren().add(addConversationText);

		addConversationLayout.getChildren().add(addConversationspacing6);

		addConversationLayout.getChildren().add(addConversationButtonBox);
		addConversationButtonBox.getChildren().add(addConversationButton);

	}
	
	private void populateProfileWindow() {
		profileFNameField.setText(fName);
		profileLNameField.setText(lName);
		profileEmailField.setText(email);
		profilePhoneField.setText(number);
		profileBioArea.setText(bio);
		profileInterestsArea.setText(interests);		
	}

	public void updateContactsWind() {
		for(int i = 0; i < contactsFromServer.size(); i++) {
			contactsList.add(new HBox());
			contactsTextList.add(new Text());
		}
		
		for(int i = 0; i < contactsTextList.size(); i++) {
			Text contactText = contactsTextList.get(i);
			contactText.setLayoutY(25.0);
			contactText.setStrokeType(StrokeType.OUTSIDE);
			contactText.setStrokeWidth(0.0);
			contactText.setText(contactsFromServer.get(i));
			contactText.setTextAlignment(TextAlignment.CENTER);
			contactText.setWrappingWidth(416);
		}
		
		for(int i = 0; i < contactsList.size(); i++) {
			HBox contactBox = contactsList.get(i);
			contactBox.setPrefHeight(48.0);
			contactBox.setPrefWidth(414.0);
			contactBox.getChildren().add(contactsTextList.get(i));
		}
		
		profileLayout.setPrefHeight(contactsList.size() * 50);
	}
	public void initializeContactsWindow() {
		contactsPane = new ScrollPane();
		contactsPane.setLayoutX(-1.0);
		contactsPane.setLayoutY(-1.0);
		contactsPane.setPrefHeight(358.0);
		contactsPane.setPrefWidth(416.0);

		contactsLayout = new VBox();
		contactsLayout.setPrefHeight(contactsList.size() * 50);
		contactsLayout.setPrefWidth(414.0);
	}
	
	public void setContactsWindow() {
		
	}

}
