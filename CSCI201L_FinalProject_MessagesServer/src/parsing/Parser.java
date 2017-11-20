package parsing;

import com.google.gson.Gson;
import objects.DataContainer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Parser {

	private DataContainer data;

	public Parser(String filename) throws IOException {
		BufferedReader bf = new BufferedReader(new FileReader(filename));
		Gson gson = new Gson();
		data = gson.fromJson(bf, DataContainer.class);
	}

	public DataContainer getData() {
		return data;
	}

}
