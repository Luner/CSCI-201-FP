package testing;

import java.io.FileNotFoundException;
import java.io.IOException;

import parsing.Parser;
import server.Server;
import objects.DataContainer;
import objects.User;

import com.google.gson.JsonSyntaxException;

public class TempMain {

	private Parser parser;
	private DataContainer data;

	public TempMain() {

		// loop until given a valid file
		Boolean gotFile = getFilename();

		while (!gotFile) {
			gotFile = getFilename();
		}
		data = parser.getData();
//		 Print all users
		for (User user : data.getUsers()) {
			System.out.println("UID: " + user.getUid() + "  Username: " + user.getUsername() + "  Password: "
					+ user.getPassword());
		}
		new Server(data, 6789);
	}

	// method that queries for an input file and parses it
	private Boolean getFilename() {
		try {
			System.out.println("What is the name of the input file?");
			// parser = new Parser(br.readLine());

			parser = new Parser("JSON/ExampleInput.json");

			// check we something in the data
			if (parser.getData() == null) {
				System.out.println("That file is not a well-formed JSON file.");
				return false;
			}

			return true;
		} catch (FileNotFoundException e) {
			System.out.println("That file could not be found.");
			return false;
		} catch (IOException | JsonSyntaxException e) {
			System.out.println("That file is not a well-formed JSON file.");
			return false;
		}
	}

	public static void main(String[] args) {
		// calls the constructor that will start the parsing
		new TempMain();
	}
}
