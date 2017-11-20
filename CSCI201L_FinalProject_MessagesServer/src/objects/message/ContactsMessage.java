package objects.message;

import java.io.Serializable;
import java.util.ArrayList;

public class ContactsMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	private ArrayList<String> contacts;

	public ContactsMessage(ArrayList<String> contacts) {
		this.contacts = contacts;
	}

	public ArrayList<String> getContacts() {
		if (contacts.contains("Guest")) {
			contacts.remove("Guest");
		}

		if (contacts.contains("Bot")) {
			contacts.remove("Bot");
		}
		return contacts;

	}
}
