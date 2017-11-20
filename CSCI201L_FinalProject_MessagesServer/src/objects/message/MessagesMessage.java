package objects.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

//A message meant to be sent to the server for verification
public class MessagesMessage extends Message {
	private static final long serialVersionUID = 1L;
	
	private ArrayList<ArrayList<String>> arrayMap;
	
	// Constructor: sets all of the variable
	public MessagesMessage(Map<Integer, ArrayList<String>> map) {
		arrayMap = new ArrayList<ArrayList<String>>();
		
		for (Entry<Integer, ArrayList<String>> entry : map.entrySet()) {
			arrayMap.add(new ArrayList<String>());
			for(String s : entry.getValue()) {
				arrayMap.get(entry.getKey() - 1).add(s);
			}
		}
	}
	
	public Map<Integer, ArrayList<String>> getMessage(){
		Map<Integer, ArrayList<String>> result = new HashMap<Integer,  ArrayList<String>>();
		for(int i = 0; i < arrayMap.size(); i++) {
			result.put(i + 1, arrayMap.get(i));
		}
		return result;
	}
}
