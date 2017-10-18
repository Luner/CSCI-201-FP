package objects.message;

public class VerificationResponseMessage extends Message{

	private static final long serialVersionUID = 1L;
	
	//If true, then the verification was successful
	private boolean verified;
	//The userId
	private int uid;
	
	//Constructor: initializes the variables
	public VerificationResponseMessage(boolean verified, int uid){
		this.verified = verified;
		this.uid = uid;
	}
	
	//getter for verified
	public boolean isVerified() {
		return verified;
	}
	
	//getter for Uid
	public int getUid() {
		return this.uid;
	}
	
	public String toString() { 
	    return "VerificationResponseMessage| verified?:" + this.verified + ", uid: " + this.uid;
	} 
}
