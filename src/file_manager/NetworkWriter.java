package file_manager;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import variable.Environment;
import variable.FilePathManager;
import broker_collaborator.NetworkGenerator;

public class NetworkWriter {
	public static void writeNetworkToFile(){
		/*if(Environment.isRecordedTrace){
			return;
		}*/
		String filePath = FilePathManager.getNetworkFilePath();
		ArrayList<ArrayList<Double>> bwTrace = NetworkGenerator.getBwTrace();
		PrintWriter writer;
		try {
			writer = new PrintWriter(filePath, "UTF-8");
			double bw;
			
			for(int i=0; i<bwTrace.get(0).size(); i++){
				for(int j=0; j<Environment.threadNum; j++){
					bw = bwTrace.get(j).get(i);
					writer.print(bw + " ");
				}
				writer.println();
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
