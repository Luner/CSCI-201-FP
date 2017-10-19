package objects.message;

//A message meant to be sent to the server for verification
public class CreateUserMessage extends Message{
	private static final long serialVersionUID = 1L;
	
	//The message contains a userneme and password to be compared to find the
	//respective user if it exists
	private String username;
	private String password;
	
	//Constructor: sets all of the variable
	public CreateUserMessage(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	//getter for username
	public String getUsername() {
		return username;
	}
	
	//Getter for password
	public String getPassword() {
		return password;
	}
	
	public String toString() { 
	    return "CreateUserMessage:: username:" + this.username + ", password: " + this.password;
	} 
}
