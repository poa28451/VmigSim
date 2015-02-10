package parser;

import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONObject;

public class JsonWriter {
	
	public void writeResultFile(String filepath){
		//FileWriter wr;
		//JSONObject obj;
		try {
			FileWriter wr = new FileWriter("test.json");
			JSONObject mainObj = new JSONObject();
			
			wr.write(mainObj.toString(4));
			wr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeSections(JSONObject mainObj){
		
	}
}
