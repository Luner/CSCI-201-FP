package objects.message;

import java.io.Serializable;
import java.util.ArrayList;

public class ContactsMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	private String[] contacts;

	public ContactsMessage(ArrayList<String> contacts) {
		this.contacts = new String[contacts.size()];
		for(int i = 0; i < contacts.size(); i++) {
			this.contacts[i] = contacts.get(i);
		}
		
	}

	public ArrayList<String> getContacts() {
		ArrayList<String> result = new ArrayList<String>();
		for(int i = 0; i < this.contacts.length; i++) {
			if(contacts[i].equals("Guest") || contacts[i].equals("Bot")) {
				//Do Nothing
			} else {
				result.add(contacts[i]);
			}
		}
		return result;
	}
}
