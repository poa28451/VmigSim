package file_manager;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import variable.FilePathManager;

public class FuzzyWriter {
	private static String threadTrace = "";
	
	public static void appendThreadTrace(String trace){
		threadTrace += trace + "\n";
	}
	
	public static void writeFuzzyLogToFile(){
		String filePath = FilePathManager.getFuzzyFilePath();
		PrintWriter writer;
		try {
			writer = new PrintWriter(filePath, "UTF-8");
			writer.println(threadTrace);
			writer.close();
			resetTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	private static void resetTrace(){
		threadTrace = "";
	}
}
