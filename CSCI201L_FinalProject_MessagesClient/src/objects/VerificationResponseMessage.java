package objects;

public class VerificationResponseMessage extends Message{

	private static final long serialVersionUID = 1L;
	
	private boolean verified;
	private int uid;
	
	public VerificationResponseMessage(boolean verified, int uid){
		this.verified = verified;
		this.uid = uid;
	}
	
	public boolean isVerified() {
		return verified;
	}
	
	public int getUid() {
		return this.uid;
	}
}
