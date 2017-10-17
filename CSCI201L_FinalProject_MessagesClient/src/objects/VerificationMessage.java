package objects;

public class VerificationMessage extends Message{
	private static final long serialVersionUID = 1L;
	
	private String username;
	private String password;
	

	public VerificationMessage(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
}
