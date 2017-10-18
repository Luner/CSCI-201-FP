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
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class TempMain extends Application {
	
	Button button;
	TextField username;
	TextField password;
	Text usernameLabel;
	Text passwordLabel;
	ChatClient client;
	
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
		
		button = new Button();
		button.setText("Login");
		button.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				if(client.login(username.getText(), password.getText())) {
					System.out.println("Logged In!");
				} else {
					System.out.println("login FAILED!!!");
				}
				
			}
		});
		/*
		 * 	button.setOnAction(e -> {
				System.out.println("logged in");
		});
		 */
		HBox layout = new HBox();
		layout.setPadding(new Insets(15, 12, 15, 12));
		layout.setSpacing(10);
	    
		layout.setStyle("-fx-background-color: #336699;");
		layout.getChildren().add(usernameLabel);
		layout.getChildren().add(username);
		layout.getChildren().add(passwordLabel);
		layout.getChildren().add(password);
		layout.getChildren().add(button);
		
		Scene scene = new Scene(layout, 600, 400);
		primaryStage.setScene(scene);
		primaryStage.show();
		

		System.out.println("oh shit");
		client = new ChatClient("localhost", 6789);
		
		
		
	}
}
	