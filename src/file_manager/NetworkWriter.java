package file_manager;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import variable.FilePathContainer;
import broker_collaborator.NetworkGenerator;

public class NetworkWriter {
	public static void writeNetworkToFile(){
		String filePath = FilePathContainer.getNetworkFilePath();
		ArrayList<Double> bwTrace = NetworkGenerator.getBwTrace();
		PrintWriter writer;
		try {
			writer = new PrintWriter(filePath, "UTF-8");
			
			for(int i=0; i<bwTrace.size(); i++){
				double bw = bwTrace.get(i);
				writer.println(bw);
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
