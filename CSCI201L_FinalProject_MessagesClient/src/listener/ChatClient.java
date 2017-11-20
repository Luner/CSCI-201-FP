package listener;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import objects.ClientConversation;
import objects.message.CommandMessage;
import objects.message.ContactsMessage;
import objects.message.ConversationsMessage;
import objects.message.CreateConversationMessage;
import objects.message.CreateUserMessage;
import objects.message.LogoutMessage;
import objects.message.Message;
import objects.message.ProfileMessage;
import objects.message.MessagesMessage;
import objects.message.VerificationMessage;
import objects.message.VerificationResponseMessage;

import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import objects.message.ChatMessage;
import objects.message.ChatStringMessage;
import objects.message.SettingsMessage;

public class ChatClient extends Application {

	// GRAPHICAL USER INTERFACE ------------------

	////////// IP SELECT/////////////
	private AnchorPane ipSelect;
	private Scene ipSelectScene;
	private TextField ipEntry;
	private Button ipEnter;
	private Text ipSelectTitle;

	////////// LOGIN WINDOW//////////
	// LogInPage
	private Scene loginScene;

	// Pane
	private AnchorPane loginLayout;

	// Labels
	private Text usernameLabel;
	private Text passwordLabel;
	private Text title;

	// TextFields
	private TextField username;
	private PasswordField password;

	// Login Button
	private Button login;
	private Button createUser;
	private Button guestButton;

	private Line devider;

	////////// CONTACTS WINDOW//////////
	private ScrollPane contactsPane;
	private VBox contactsLayout;
	// ArrayList<HBox> contactsList;
	private ArrayList<String> contactsFromServer;
	ArrayList<Text> contactsTextList;
	private ArrayList<Button> contactsButtons;

	////////// Function Bar and Chats//////////
	private Scene chatScene;

	SplitPane chatLayout;
	private AnchorPane leftSide;
	private ScrollPane chatsPane;
	private VBox chatsButtonsLayout;
	private ArrayList<Button> chatsButtons;
	private ImageView settings;
	private ImageView contacts;
	private ImageView profile;
	private ImageView logOutButton;
	private ImageView add;

	private AnchorPane rightSide;
	private Text chatName;
	////////// CHAT WINDOW//////////

	private TextField typedMessage;
	private TextArea chatText;
	private Button sendMessage;
	@SuppressWarnings("unused")
	private Button sendFileButton;

	////////// Settings pane //////////////

	private Pane settingsPane;
	private VBox settingsVBox;

	// title area
	private Pane settingsTitlePane;

	// setting details
	private Pane settingsDetailPane;

	// account info
	private Label settingsInfoLabel;
	private HBox settingsInfoHBox;
	private VBox settingsInfoLabelVBox;
	private Label settingsInfoNewUsernameLabel;
	private Label settingsInfoNewPasswordLabel;
	private VBox settingsInfoFieldsVBox;
	private TextField settingsInfoNewUsernameField;
	private TextField settingsInfoNewPasswordField;

	// chat settings
	private Label settingsDetailSettingsLabel;
	private HBox settingsDetailSettingsHBox;
	private VBox settingsDetailSettingsLabelVBox;
	private Label settingsDetailSettingsColorLabel;
	private Label settingsDetailSettingsFontLabel;
	private VBox settingsDetailSettingsComboBoxVBox;
	private ComboBox<Color> settingsDetailSettingsColorComboBox;
	private ComboBox<Font> settingsDetailSettingsFontComboBox;

	// update button
	private Button settingsUpdateButton;

	// -------------------------------------------

	////////// PROFILE WINDOW//////////

	private ScrollPane profilePane;
	private VBox profileLayout;
	private HBox spacing1;
	private HBox spacing2;
	private HBox spacing3;
	private HBox spacing4;
	private HBox spacing5;
	private HBox spacing6;
	private HBox spacing7;

	private Pane facebookSpacing;

	private HBox firstNameLayout;
	private Text firstNameProfileLabel;
	private TextField firstNameProfileInput;

	private HBox lastNameLayout;
	private Text lastNameProfileLabel;
	private TextField lastNameProfileInput;

	private HBox emailLayout;
	private Text emailProfileLabel;
	private TextField emailProfileInput;

	private HBox phoneLayout;
	private Text phoneProfileLabel;
	private TextField phoneProfileInput;

	private HBox facebookLayout;
	private Text facebookProfileLabel;
	private ImageView facebookProfileIcon;

	private HBox profileUpdateLayout;
	private Button profileUpdateButton;

	// -------------------------------------------

	////////// ADD CHAT WINDOW//////////

	private ScrollPane addConversationPane;
	private VBox addConversationLayout;
	private HBox addConversationspacing1;
	private HBox addConversationspacing2;
	private HBox addConversationspacing3;
	private HBox addConversationspacing4;
	private HBox addConversationspacing5;
	private HBox addConversationspacing6;
	private HBox addConversationspacing7;
	private HBox addConversationspacing8;
	private HBox addConversationspacing9;

	private HBox User1Layout;
	private Text User1Label;
	private TextField User1Input;

	private HBox User2Layout;
	private Text User2Label;
	private TextField User2Input;

	private HBox User3Layout;
	private Text User3Label;
	private TextField User3Input;

	private HBox User4Layout;
	private Text User4Label;
	private TextField User4Input;

	private HBox User5Layout;
	private Text User5Label;
	private TextField User5Input;

	private HBox addConversationBox;
	private Text addConversationText;

	private HBox addConversationButtonBox;
	private Button addConversationButton;

	// -------------------------------------------

	// SERVER CLIENT COMMUNICATION
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Socket s;
	private Integer selectedChat;
	private Map<Button, Integer> chatsMap;
	private Color fontColor;
	private Font chatFont;

	// client properties
	private int uid;
	@SuppressWarnings("unused")
	private String fName;
	@SuppressWarnings("unused")
	private String lName;
	@SuppressWarnings("unused")
	private String email;
	@SuppressWarnings("unused")
	private String number;
	@SuppressWarnings("unused")
	private String bio;
	@SuppressWarnings("unused")
	private String interests;
	private volatile boolean loggedIn;

	private String user_Username;
	private Map<Integer, ArrayList<String>> chatHistory;
	private Map<Integer, String> chatIDtoName;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// 10.14.112.127
		primaryStage.setTitle("Messaging Application");
		primaryStage.initStyle(StageStyle.UTILITY);
		primaryStage.setResizable(false);

		chatsMap = new HashMap<Button, Integer>();
		chatIDtoName = new HashMap<Integer, String>();
		initializeIPSelect();
		initializeLoginPage();
		initializeChatWindow();
		initializeProfileWindow();
		initializeContactsWindow();
		initializeAddConversationWindow();
		initializeSettingsPane();

		primaryStage.setOnCloseRequest(evt -> {
			// prevent window from closing
			evt.consume();

			// execute own shutdown procedure
			logout();
		});

		ipEnter.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				if (!setUpChatClient(ipEntry.getText(), 6789)) {
					System.out.println("Failed to connect to specified server.");
					System.exit(0);
				} else {
					loggedIn = true;
					primaryStage.setScene(loginScene);
				}
			}
		});

		// Set what happens on button press
		login.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (login(username.getText(), password.getText())) {
					loggedIn = true;
					user_Username = username.getText();
					addFunctions();
					primaryStage.setScene(chatScene);
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
			String name = User5Input.getText();

			User1Input.setText("");
			User2Input.setText("");
			User3Input.setText("");
			User4Input.setText("");

			createConversation(user1, user2, user3, user4, name);
			chatText.setFont(new Font("Helvetica", 12));
			chatText.setText("");

			chatName.setText("Global Chat");
			selectedChat = 1;
			setChatWindow();
		});

		guestButton.setOnAction(e -> {
			if (login("Guest", "None")) {
				loggedIn = true;
				primaryStage.setScene(chatScene);
				Thread th = new Thread(task);
				th.setDaemon(true);
				th.start();
			}
		});

		createUser.setOnAction(e -> {
			if (createUser(username.getText(), password.getText())) {
				System.out.println("User Created");
				loggedIn = true;
				user_Username = username.getText();
				addFunctions();
				primaryStage.setScene(chatScene);
				Thread th = new Thread(task);
				th.setDaemon(true);
				th.start();
			}
		});

		add.setOnMouseClicked(e -> {
			chatText.setFont(new Font("Helvetica", 18));
			chatName.setText("Creating Chat");
			setAddConversationWindow();
		});

		settings.setOnMouseClicked(e -> {
			chatName.setText("Settings");
			setSettingsPane();
		});

		logOutButton.setOnMouseClicked(e -> {
			logout();
		});

		profile.setOnMouseClicked(e -> {
			chatName.setText("Profile");
			setProfileWindow();
		});

		contacts.setOnMouseClicked(e -> {
			chatText.setFont(new Font("Helvetica", 18));
			chatName.setText("Contacts");
			setContactsWindow();
		});

		typedMessage.setOnKeyPressed((e) -> {
			if (e.getCode() == KeyCode.ENTER && !typedMessage.getText().equals("")) {
				send(typedMessage.getText());
				typedMessage.setText("");
			}
		});

		sendMessage.setOnAction(e -> {
			if (!typedMessage.getText().equals("")) {
				send(typedMessage.getText());
				typedMessage.setText("");
			}
		});

		settingsUpdateButton.setOnMouseClicked(e -> {
			fontColor = settingsDetailSettingsColorComboBox.getValue();
			chatFont = settingsDetailSettingsFontComboBox.getValue();
			chatText.setStyle("-fx-text-fill: " + fontColor.toString().replace("0x", "#") + ";");
			chatText.setFont(chatFont);
			sendMessage(new SettingsMessage(uid, fontColor, chatFont));
			System.out.println(
					"Color changed to: " + fontColor.toString() + ", Font changed to: " + chatFont.getFamily());
		});

		profileUpdateButton.setOnMouseClicked(e -> {
			fName = firstNameProfileInput.getText();
			lName = lastNameProfileInput.getText();
			email = emailProfileInput.getText();
			number = phoneProfileInput.getText();
		});

		primaryStage.setScene(ipSelectScene);
		primaryStage.show();

	}

	private void addFunctions() {
		leftSide.getChildren().add(settings);
		leftSide.getChildren().add(contacts);
		leftSide.getChildren().add(profile);
		leftSide.getChildren().add(logOutButton);
		leftSide.getChildren().add(add);
	}

	private void logout() {
		if (loggedIn == true) {

			try {
				oos.writeObject(new LogoutMessage());
				oos.flush();
			} catch (IOException e) {
				System.out.println("IOE in logout");
			}

			loggedIn = false;
		}

		System.exit(0);
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
			// System.out.println("ioe in set-up: " + ioe.getMessage());
		}
		return false;
	}

	public boolean createUser(String username, String password) {
		try {

			// Creates a VerificationMessage with the username and password inputs
			Message message = new CreateUserMessage(username, password);

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
				System.out.println("GOT A: " + message.getClass().getName());
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("cnfe: " + cnfe.getMessage());
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}

		System.out.println("verification missed");
		return false;
	}

	private void send(String text) {

		// And send a ChatMessage to the Server
		try {
			// Creates a ChatMessage with the input
			Message message;
			if (text.startsWith("/") || text.startsWith("\\")) {
				if (text.startsWith("/addUser")) {
					int max = 9 - selectedChat.toString().length();
					for (int i = 0; i < max; i++) {
						text += " ";
					}
					text += "J";
					text += selectedChat.toString().length();
				}
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

	private void createConversation(String user1, String user2, String user3, String user4, String name) {
		Message message = new CreateConversationMessage(user_Username, user1, user2, user3, user4, name);
		try {
			oos.writeObject(message);
			oos.flush();
		} catch (IOException e) {
			System.out.println("ioe in createConversation");
		}
	}

	private void cleanUp() {
		try {
			if (ois != null) {
				ois.close();
			}
			if (oos != null) {
				oos.close();
			}
			if (s != null) {
				s.close();
			}

		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
		System.out.println("Exitng");
		System.exit(0);
	}

	private Task<Void> task = new Task<Void>() {
		@Override
		protected Void call() throws Exception {
			try {
				// Loop consistently looking for an object to be sent from the server
				while (loggedIn) {
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
										chatText.setText(chatText.getText() + "\n" + chatMessageString);
									} else {
										chatText.setText(chatMessageString);
									}
									chatText.selectEnd();
									chatText.deselect();
								}
							}
						});

					} else if (message instanceof SettingsMessage) {
						SettingsMessage sm = (SettingsMessage) message;
						fontColor = sm.getColor();
						chatFont = sm.getFont();
						chatText.setStyle("-fx-text-fill: " + fontColor.toString().replace("0x", "#") + ";");
						chatText.setFont(chatFont);

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
										for (int j = 0; j < chatHistory.get(chatid).size(); j++) {
											if (j == 0) {
												chatText.setText(chatHistory.get(chatid).get(j).replace("!@#$%^&*()",
														user_Username));
											} else {

												chatText.setText(chatText.getText() + "\n" + chatHistory.get(chatid)
														.get(j).replace("!@#$%^&*()", user_Username));
											}
										}
										if (chatid == 0) {
											chatName.setText("Global Chat");
										} else {
											chatName.setText(chatIDtoName.get(chatid));
										}
										selectedChat = chatid;

										setChatWindow();
									});
								}
							}
						});
					} else if (message instanceof ProfileMessage) {
						ProfileMessage pm = (ProfileMessage) message;
						fName = pm.getFName();
						lName = pm.getLName();
						email = pm.getEmail();
						number = pm.getNumber();
						bio = pm.getBio();
						interests = pm.getInterests();
					} else if (message instanceof MessagesMessage) {
						chatHistory = ((MessagesMessage) message).getMessage();

					} else if (message instanceof ContactsMessage) {
						contactsFromServer = ((ContactsMessage) message).getContacts();
						updateContactsWind();
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								contactsLayout.getChildren().clear();
								for (Button button : contactsButtons) {
									contactsLayout.getChildren().add(button);
								}
								for (Integer i = 0; i < contactsButtons.size(); i++) {
									Button button = contactsButtons.get(i);
									button.setOnAction(e -> {
										createConversation(button.getText(), "", "", "",
												button.getText() + "/" + user_Username);
									});
								}
							}
						});
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

	private void initializeIPSelect() {
		// IP Pane
		ipSelect = new AnchorPane();
		ipSelect.setMaxHeight(Double.NEGATIVE_INFINITY);
		ipSelect.setMaxWidth(Double.NEGATIVE_INFINITY);
		ipSelect.setMinHeight(Double.NEGATIVE_INFINITY);
		ipSelect.setMinWidth(Double.NEGATIVE_INFINITY);
		ipSelect.setPrefHeight(400.0);
		ipSelect.setPrefWidth(600.0);

		// IP Title
		ipSelectTitle = new Text();
		ipSelectTitle.setLayoutX(190.0);
		ipSelectTitle.setLayoutY(84.0);
		ipSelectTitle.setStrokeType(StrokeType.OUTSIDE);
		ipSelectTitle.setStrokeWidth(0.0);
		ipSelectTitle.setText("Server Connection");
		ipSelectTitle.setFont(Font.font("Helvetica", 27));

		// IP Entry
		ipEntry = new TextField();
		ipEntry.setLayoutX(207.0);
		ipEntry.setLayoutY(185.0);
		ipEntry.setPromptText("Enter IP");

		// IP Submit
		ipEnter = new Button();
		ipEnter.setLayoutX(273.0);
		ipEnter.setLayoutY(261.0);
		ipEnter.setText("Enter");
		ipEnter.setMnemonicParsing(false);

		// Finalization
		ipSelect.getChildren().add(ipEnter);
		ipSelect.getChildren().add(ipEntry);
		ipSelect.getChildren().add(ipSelectTitle);

		// New Scene
		ipSelectScene = new Scene(ipSelect);
	}

	private void initializeLoginPage() {
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
		guestButton.setLayoutY(266.0);
		guestButton.setMnemonicParsing(false);
		guestButton.setText("Guest");

		// Add Children to Pane
		loginLayout.getChildren().add(usernameLabel);
		loginLayout.getChildren().add(passwordLabel);
		loginLayout.getChildren().add(devider);
		loginLayout.getChildren().add(username);
		loginLayout.getChildren().add(password);
		loginLayout.getChildren().add(login);
		loginLayout.getChildren().add(createUser);
		loginLayout.getChildren().add(guestButton);

		loginScene = new Scene(loginLayout);
	}

	private void initializeSettingsPane() {
		// initialize components here
		settingsPane = new Pane();
		settingsPane.setPrefHeight(400);
		settingsPane.setPrefWidth(435);
		settingsVBox = new VBox();
		settingsVBox.setPrefHeight(400);
		settingsVBox.setPrefWidth(435);

		settingsTitlePane = new Pane();
		settingsTitlePane.setPrefHeight(27);
		settingsTitlePane.setPrefWidth(435);
		VBox.setMargin(settingsTitlePane, new Insets(10));

		settingsDetailPane = new Pane();
		settingsDetailPane.setPrefHeight(349);
		settingsDetailPane.setPrefWidth(435);

		settingsInfoLabel = new Label("Account Information");
		settingsInfoLabel.setLayoutX(122);
		settingsInfoLabel.setLayoutY(7);
		settingsInfoLabel.setFont(Font.font("Helvetica", 18));
		settingsInfoLabel.setPrefHeight(26);
		settingsInfoLabel.setPrefWidth(174);
		settingsInfoLabel.setTextAlignment(TextAlignment.CENTER);

		settingsInfoHBox = new HBox();
		settingsInfoHBox.setLayoutX(38);
		settingsInfoHBox.setLayoutY(46);
		settingsInfoHBox.setPrefHeight(53);
		settingsInfoHBox.setPrefWidth(324);

		settingsInfoLabelVBox = new VBox();
		settingsInfoLabelVBox.setPrefHeight(200);
		settingsInfoLabelVBox.setPrefHeight(100);

		settingsInfoNewUsernameLabel = new Label("Username:");
		settingsInfoNewUsernameLabel.setFont(Font.font("Helvetica", 16));
		settingsInfoNewUsernameLabel.setPrefHeight(36);
		settingsInfoNewUsernameLabel.setPrefWidth(100);
		VBox.setMargin(settingsInfoNewUsernameLabel, new Insets(4));

		settingsInfoNewPasswordLabel = new Label("Password:");
		settingsInfoNewPasswordLabel.setFont(Font.font("Helvetica", 16));
		settingsInfoNewPasswordLabel.setPrefHeight(36);
		settingsInfoNewPasswordLabel.setPrefWidth(100);
		VBox.setMargin(settingsInfoNewPasswordLabel, new Insets(4));

		settingsInfoFieldsVBox = new VBox();
		settingsInfoFieldsVBox.setPrefHeight(35);
		settingsInfoFieldsVBox.setPrefWidth(225);

		settingsInfoNewUsernameField = new TextField();
		VBox.setMargin(settingsInfoNewUsernameField, new Insets(4));

		settingsInfoNewPasswordField = new TextField();
		VBox.setMargin(settingsInfoNewPasswordField, new Insets(4));

		settingsDetailSettingsLabel = new Label("Chat Settings");
		settingsDetailSettingsLabel.setFont(Font.font("Helvetica", 18));
		settingsDetailSettingsLabel.setLayoutX(154);
		settingsDetailSettingsLabel.setLayoutY(118);
		settingsDetailSettingsLabel.setPrefHeight(26);
		settingsDetailSettingsLabel.setPrefWidth(131);
		settingsDetailSettingsLabel.setTextAlignment(TextAlignment.CENTER);

		settingsDetailSettingsHBox = new HBox();
		settingsDetailSettingsHBox.setLayoutX(38);
		settingsDetailSettingsHBox.setLayoutY(156);
		settingsDetailSettingsHBox.setPrefHeight(66);
		settingsDetailSettingsHBox.setPrefWidth(324);

		settingsDetailSettingsLabelVBox = new VBox();
		settingsDetailSettingsLabelVBox.setPrefHeight(82);
		settingsDetailSettingsLabelVBox.setPrefWidth(105);

		settingsDetailSettingsColorLabel = new Label("Text Color:");
		settingsDetailSettingsColorLabel.setFont(Font.font("Helvetica", 16));
		settingsDetailSettingsColorLabel.setPrefHeight(45);
		settingsDetailSettingsColorLabel.setPrefWidth(107);

		settingsDetailSettingsFontLabel = new Label("Font Style:");
		settingsDetailSettingsFontLabel.setFont(Font.font("Helvetica", 16));
		settingsDetailSettingsFontLabel.setPrefHeight(45);
		settingsDetailSettingsFontLabel.setPrefWidth(107);

		settingsDetailSettingsComboBoxVBox = new VBox();
		settingsDetailSettingsComboBoxVBox.setPrefHeight(82);
		settingsDetailSettingsComboBoxVBox.setPrefWidth(221);

		settingsDetailSettingsColorComboBox = new ComboBox<Color>();
		settingsDetailSettingsColorComboBox.setPrefHeight(31);
		settingsDetailSettingsColorComboBox.setPrefWidth(221);
		VBox.setMargin(settingsDetailSettingsColorComboBox, new Insets(4));
		settingsDetailSettingsColorComboBox.getItems().addAll(Color.BLACK, Color.CRIMSON, Color.ROYALBLUE);

		settingsDetailSettingsColorComboBox.setButtonCell(new ListCell<Color>() {
			private final Rectangle rectangle;
			{
				setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
				rectangle = new Rectangle(180, 26);
			}

			@Override
			protected void updateItem(Color item, boolean empty) {
				super.updateItem(item, empty);

				if (item == null || empty) {
					setGraphic(null);
				} else {
					rectangle.setFill(item);
					setGraphic(rectangle);
				}
			}

		});

		settingsDetailSettingsColorComboBox.setCellFactory(new Callback<ListView<Color>, ListCell<Color>>() {
			@Override
			public ListCell<Color> call(ListView<Color> p) {
				return new ListCell<Color>() {
					private final Rectangle rectangle;
					{
						setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
						rectangle = new Rectangle(180, 26);

					}

					@Override
					protected void updateItem(Color item, boolean empty) {
						super.updateItem(item, empty);

						if (item == null || empty) {
							setGraphic(null);
						} else {
							rectangle.setFill(item);
							setGraphic(rectangle);
						}
					}
				};
			}
		});
		settingsDetailSettingsColorComboBox.setValue(fontColor);

		settingsDetailSettingsFontComboBox = new ComboBox<Font>();
		settingsDetailSettingsFontComboBox.setPrefHeight(31);
		settingsDetailSettingsFontComboBox.setPrefWidth(221);
		VBox.setMargin(settingsDetailSettingsFontComboBox, new Insets(4));
		settingsDetailSettingsFontComboBox.getItems().addAll(Font.font("Arial"), Font.font("Georgia"),
				Font.font("Tahoma"), Font.font("Lucida Bright"), Font.font("Verdana"));

		settingsDetailSettingsFontComboBox.setButtonCell(new ListCell<Font>() {
			private final Text text;
			{
				setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
				text = new Text();
			}

			@Override
			protected void updateItem(Font item, boolean empty) {
				super.updateItem(item, empty);

				if (item == null || empty) {
					setGraphic(null);
				} else {
					text.setText(item.getName());
					text.setFont(item);
					setGraphic(text);
				}
			}
		});

		settingsDetailSettingsFontComboBox.setCellFactory(new Callback<ListView<Font>, ListCell<Font>>() {
			@Override
			public ListCell<Font> call(ListView<Font> p) {
				return new ListCell<Font>() {
					private final Text text;
					{
						setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
						text = new Text();
					}

					@Override
					protected void updateItem(Font item, boolean empty) {
						super.updateItem(item, empty);

						if (item == null || empty) {
							setGraphic(null);
						} else {
							text.setText(item.getName());
							text.setFont(item);
							setGraphic(text);
						}
					}
				};
			}
		});
		settingsDetailSettingsFontComboBox.setValue(chatFont);

		settingsUpdateButton = new Button("Update");
		settingsUpdateButton.setLayoutX(152);
		settingsUpdateButton.setLayoutY(254);
		settingsUpdateButton.setMnemonicParsing(false);
		settingsUpdateButton.setPrefHeight(25);
		settingsUpdateButton.setPrefWidth(114);

		// set layout here
		settingsPane.getChildren().add(settingsVBox);
		settingsVBox.getChildren().addAll(settingsTitlePane, settingsDetailPane);
		settingsDetailPane.getChildren().addAll(settingsInfoLabel, settingsInfoHBox, settingsDetailSettingsLabel,
				settingsDetailSettingsHBox, settingsUpdateButton);
		settingsInfoHBox.getChildren().addAll(settingsInfoLabelVBox, settingsInfoFieldsVBox);
		settingsInfoLabelVBox.getChildren().addAll(settingsInfoNewUsernameLabel, settingsInfoNewPasswordLabel);
		settingsInfoFieldsVBox.getChildren().addAll(settingsInfoNewUsernameField, settingsInfoNewPasswordField);
		settingsDetailSettingsHBox.getChildren().addAll(settingsDetailSettingsLabelVBox,
				settingsDetailSettingsComboBoxVBox);
		settingsDetailSettingsLabelVBox.getChildren().addAll(settingsDetailSettingsColorLabel,
				settingsDetailSettingsFontLabel);
		settingsDetailSettingsComboBoxVBox.getChildren().addAll(settingsDetailSettingsColorComboBox,
				settingsDetailSettingsFontComboBox);

	}

	private void setSettingsPane() {
		rightSide.getChildren().clear();
		rightSide.getChildren().add(settingsPane);
		rightSide.getChildren().add(chatName);
	}

	private void initializeChatWindow() {

		SplitPane chatLayout = new SplitPane();
		chatLayout.setDividerPositions(0.335);
		chatLayout.setMaxHeight(Double.NEGATIVE_INFINITY);
		chatLayout.setMaxWidth(Double.NEGATIVE_INFINITY);
		chatLayout.setMinHeight(Double.NEGATIVE_INFINITY);
		chatLayout.setMinWidth(Double.NEGATIVE_INFINITY);
		chatLayout.setPrefHeight(400.0);
		chatLayout.setPrefWidth(635.0);

		leftSide = new AnchorPane();
		leftSide.setMinHeight(0.0);
		leftSide.setMinWidth(0.0);
		leftSide.setPrefHeight(398);
		leftSide.setPrefWidth(200);

		rightSide = new AnchorPane();
		rightSide.setMinHeight(0.0);
		rightSide.setMinWidth(0.0);
		rightSide.setPrefHeight(400);
		rightSide.setPrefWidth(435);

		chatLayout.getItems().add(leftSide);
		chatLayout.getItems().add(rightSide);

		typedMessage = new TextField();
		typedMessage.setLayoutX(2.0);
		typedMessage.setLayoutY(371.0);
		typedMessage.setPrefHeight(25.0);
		typedMessage.setPrefWidth(348.0);

		chatText = new TextArea();
		chatText.setLayoutX(2.0);
		chatText.setLayoutY(33.0);
		chatText.setPrefHeight(336.0);
		chatText.setPrefWidth(412.0);
		chatText.setEditable(false);
		chatText.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");

		chatName = new Text();
		chatName.setLayoutX(2.0);
		chatName.setLayoutY(26.0);
		chatName.setStrokeType(StrokeType.OUTSIDE);
		chatName.setStrokeWidth(0.0);
		chatName.setTextAlignment(TextAlignment.CENTER);
		chatName.setWrappingWidth(416.0);
		chatName.setFont(new Font("Helvetica", 20));
		chatName.setText("Global Chat");

		sendMessage = new Button();
		sendMessage.setLayoutX(352.0);
		sendMessage.setLayoutY(370.0);
		sendMessage.setMnemonicParsing(false);
		sendMessage.setPrefHeight(25.0);
		sendMessage.setPrefWidth(62.0);
		sendMessage.setText("Send");

		rightSide.getChildren().add(typedMessage);
		rightSide.getChildren().add(chatText);
		rightSide.getChildren().add(chatName);
		rightSide.getChildren().add(sendMessage);

		sendFileButton = new Button("Send File");

		chatsPane = new ScrollPane();
		chatsPane.setPrefHeight(367.0);
		chatsPane.setPrefWidth(209.0);

		chatsButtonsLayout = new VBox();
		chatsButtonsLayout.setPrefHeight(365.0);
		chatsButtonsLayout.setPrefWidth(190.0);

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
		profile.setLayoutX(92.0);
		profile.setLayoutY(368.0);
		profile.setPickOnBounds(true);
		profile.setPreserveRatio(true);

		file = new File("images/profile.png");
		profile.setImage(new Image(file.toURI().toString()));

		logOutButton = new ImageView();
		logOutButton.setFitHeight(30);
		logOutButton.setFitWidth(40);
		logOutButton.setLayoutX(132);
		logOutButton.setLayoutY(368);
		logOutButton.setPickOnBounds(true);
		logOutButton.setPreserveRatio(true);

		file = new File("images/logout.png");
		logOutButton.setImage(new Image(file.toURI().toString()));

		add = new ImageView();
		add.setFitHeight(30.0);
		add.setFitWidth(40.0);
		add.setLayoutX(169.0);
		add.setLayoutY(368.0);
		add.setPickOnBounds(true);
		add.setPreserveRatio(true);

		file = new File("images/add.png");
		add.setImage(new Image(file.toURI().toString()));

		chatsPane.setContent(chatsButtonsLayout);

		leftSide.getChildren().add(chatsPane);

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

	private void updateClientChats(ArrayList<ClientConversation> chats) {

		chatsButtons.clear();

		// Add New

		for (int i = 0; i < chats.size(); i++) {
			ClientConversation chat = chats.get(i);
			chatsButtons.add(new Button());
			chatsButtons.get(i).setMnemonicParsing(false);
			chatsButtons.get(i).setPrefHeight(50.0);
			chatsButtons.get(i).setPrefWidth(190);

			if (i == 0) {
				chatsButtons.get(i).setText("Global Chat");
				chatIDtoName.put(chats.get(i).getConversationID(), "Global Chat");
			} else {
				chatsButtons.get(i).setText(chat.getName());
				chatIDtoName.put(chats.get(i).getConversationID(), chat.getName());
			}

			chatsMap.put(chatsButtons.get(i), chat.getConversationID());
		}

		// change height of vbox
		chatsButtonsLayout.setPrefHeight(chats.size() * 50.0);
	}

	private void initializeProfileWindow() {
		profilePane = new ScrollPane();
		profilePane.setLayoutX(-1.0);
		profilePane.setLayoutY(-1.0);
		profilePane.setPrefHeight(398.0);
		profilePane.setPrefWidth(416.0);

		profileLayout = new VBox();
		profileLayout.setPrefHeight(378.0);
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

		spacing6 = new HBox();
		spacing6.setPrefHeight(20.0);
		spacing6.setPrefWidth(414.0);

		spacing7 = new HBox();
		spacing7.setPrefHeight(20.0);
		spacing7.setPrefWidth(414.0);

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
		profileLayout.getChildren().add(spacing7);
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

		profileUpdateLayout = new HBox();
		profileUpdateLayout.setPrefHeight(20.0);
		profileUpdateLayout.setPrefWidth(414.0);

		profileUpdateButton = new Button();
		profileUpdateButton.setText("Update Profile");

		profileLayout.getChildren().add(spacing6);
		profileLayout.getChildren().add(profileUpdateLayout);

		HBox.setMargin(profileUpdateButton, new Insets(0, 0, 0, 160));
		profileUpdateLayout.getChildren().add(profileUpdateButton);
	}

	private void setAddConversationWindow() {
		rightSide.getChildren().clear();
		rightSide.getChildren().add(addConversationPane);
		rightSide.getChildren().add(chatName);
	}

	private void initializeAddConversationWindow() {
		addConversationPane = new ScrollPane();
		addConversationPane.setLayoutX(-1.0);
		addConversationPane.setLayoutY(-1.0);
		addConversationPane.setPrefHeight(398.0);
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

		addConversationspacing7 = new HBox();
		addConversationspacing7.setPrefHeight(20.0);
		addConversationspacing7.setPrefWidth(414.0);

		addConversationspacing8 = new HBox();
		addConversationspacing8.setPrefHeight(20.0);
		addConversationspacing8.setPrefWidth(414.0);

		addConversationspacing9 = new HBox();
		addConversationspacing9.setPrefHeight(20.0);
		addConversationspacing9.setPrefWidth(414.0);

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

		User5Layout = new HBox();
		User5Layout.setPrefHeight(20.0);
		User5Layout.setPrefWidth(414.0);

		User5Label = new Text();
		User5Label.setStrokeType(StrokeType.OUTSIDE);
		User5Label.setStrokeWidth(0.0);
		User5Label.setText("Chat Name:");
		User5Label.setTextAlignment(TextAlignment.CENTER);
		User5Label.setWrappingWidth(214.0);
		User5Label.setFont(new Font("Helvetica", 18));

		User5Input = new TextField();

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
		addConversationLayout.getChildren().add(addConversationspacing8);

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

		addConversationLayout.getChildren().add(addConversationspacing7);
		addConversationLayout.getChildren().add(addConversationspacing9);

		addConversationLayout.getChildren().add(User5Layout);
		User5Layout.getChildren().add(User5Label);
		User5Layout.getChildren().add(User5Input);

		addConversationLayout.getChildren().add(addConversationspacing6);

		addConversationLayout.getChildren().add(addConversationButtonBox);
		addConversationButtonBox.getChildren().add(addConversationButton);

	}

	private void updateContactsWind() {
		contactsButtons.clear();
		// add new

		for (int i = 0; i < contactsFromServer.size(); i++) {
			contactsButtons.add(new Button());
			contactsButtons.get(i).setMnemonicParsing(false);
			contactsButtons.get(i).setPrefHeight(50.0);
			contactsButtons.get(i).setLayoutX(5);
			VBox.setMargin(contactsButtons.get(i), new Insets(0, 0, 0, 5));
			contactsButtons.get(i).setPrefWidth(400.0);
			contactsButtons.get(i).setText(contactsFromServer.get(i));
		}

		contactsLayout.setPrefHeight(contactsButtons.size() * 50);
	}

	private void initializeContactsWindow() {
		contactsPane = new ScrollPane();
		contactsPane.setLayoutX(2.0);
		contactsPane.setLayoutY(33.0);
		contactsPane.setPrefHeight(358.0);
		contactsPane.setPrefWidth(412.0);
		contactsButtons = new ArrayList<Button>();
		// contactsList = new ArrayList<HBox>();
		contactsFromServer = new ArrayList<String>();
		contactsLayout = new VBox();
		// contactsLayout.setPrefHeight(contactsButtons.size() * 50);
		contactsLayout.setPrefWidth(390.0);
		contactsPane.setContent(contactsLayout);
	}

	private void setContactsWindow() {
		rightSide.getChildren().clear();
		rightSide.getChildren().add(contactsPane);
		rightSide.getChildren().add(chatName);
	}

	private void sendMessage(Message m) {
		try {
			oos.writeObject(m);
			oos.flush();
		} catch (IOException ioe) {
			System.out.println("ioe in sendMessage: " + ioe.getMessage());
		}
	}

	public void clearAllFields() {
		username.clear();
		password.clear();
		ipEntry.clear();
		typedMessage.clear();
		chatText.clear();
		settingsInfoNewUsernameField.clear();
		settingsInfoNewPasswordField.clear();
		firstNameProfileInput.clear();
		lastNameProfileInput.clear();
		emailProfileInput.clear();
		phoneProfileInput.clear();
		User1Input.clear();
		User2Input.clear();
		User3Input.clear();
		User4Input.clear();
	}

}
