package objects.message;

public class ProfileMessage extends Message {

	private static final long serialVersionUID = 8791896595286305425L;
	
	private Integer uid;
	private String fName;
	private String lName;
	private String email;
	private String number;
	private String bio;
	private String interests;
	
	public ProfileMessage(Integer uid, String fName, String lName, String email,
							String number, String bio, String interests) {
		this.uid = uid;
		this.fName = fName;
		this.lName = lName;
		this.email = email;
		this.number = number;
		this.bio = bio;
		this.interests = interests;
	}
	
	public Integer getUid() {
		return uid;
	}
	
	public String getFName() {
		return fName;
	}
	
	public String getLName() {
		return lName;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getNumber() {
		return number;
	}
	
	public String getBio() {
		return bio;
	}
	
	public String getInterests() {
		return interests;
	}
	
	public String toString() {
		return uid.toString()+" "+fName+" "+lName+" "
				+email+" "+number+" "+bio+" "+interests;
	}
	
}
