package objects.message;

import java.util.ArrayList;

public class ContactsMessage {
	private ArrayList<String> contacts;
	
	public ContactsMessage(ArrayList<String> contacts) {
		this.contacts = contacts;
	}
	
	public ArrayList<String> getContacts() {
		return contacts;
	}
}
