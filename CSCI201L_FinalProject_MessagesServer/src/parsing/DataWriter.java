package parsing;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import objects.DataContainer;

public class DataWriter {
	
	public DataWriter(){
	}
	
	public void saveData(DataContainer data, String filename) {
		try (Writer writer = new FileWriter(filename)) {
		    Gson gson = new GsonBuilder().create();
		    gson.toJson(data, writer);
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}
	
}
