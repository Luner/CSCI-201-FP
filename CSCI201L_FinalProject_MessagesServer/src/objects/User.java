package objects;

public class User {
	private String username;
	private String password;
	private int uid; //user id
	
	public User(String username, String password, int uid) {
		this.username = username;
		this.password = password;
		this.uid = uid;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public int getUid() {
		return this.uid;
	}
	
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
	        return true;
	    }
	    if (!(o instanceof User)) {
	    		return false;
	    }
	      
	    User user = (User) o;
	    
	    if(this.username.equals(user.getUsername()) && this.password.equals(user.getPassword())
	    		&& this.uid == user.getUid()) {
	    		return true;
	    }
	      
	    return false;
	}
	
	public boolean verify(String username, String password) {
	    
	    if(this.username.equals(username) && this.password.equals(password)) {
	    		return true;
	    }
	      
	    return false;
	}
}
