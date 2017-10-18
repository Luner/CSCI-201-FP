package testing;

import listener.ChatClient;
import javafx.application.Application;
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

public class TempMain extends Application {

	Button login;
	Button sendMessage;
	TextField username;
	TextField password;
	TextField chatText;
	Text usernameLabel;
	Text passwordLabel;
	ChatClient client;
	Scene chat;
	Stage window;
 VBox chatLayout;
	
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
				if(client.login(username.getText(), password.getText())) {
					primaryStage.setScene(chat);
					client.startChatThread();
				} 				
			}
		});
		
		sendMessage = new Button();
		sendMessage.setText("Send Message");
		sendMessage.setOnAction(e -> client.send(chatText.getText()));
		

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

		client = new ChatClient("localhost", 6789);	
	}
}
	