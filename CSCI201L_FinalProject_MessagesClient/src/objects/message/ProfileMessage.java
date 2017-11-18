package objects.message;

public class ProfileMessage extends Message {

	private static final long serialVersionUID = 8791896595286305425L;
	
	private String fName;
	private String lName;
	private String phone;
	private String email;
	
	public ProfileMessage(String fName, String lName, String phone, String email) {
		this.fName = fName;
		this.lName = lName;
		this.phone = phone;
		this.email = email;
	}
	
	public String getFName() {
		return fName;
	}
	
	public String getLName() {
		return lName;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public String getEmail() {
		return email;
	}

}
