package variable;

import java.io.File;

public class FilePathContainer {
	public static String inputPath;
	public static String outputPath;
	public static String networkPath = "network\\";
	
	public static void setInputPath(String inputPath) {
		FilePathContainer.inputPath = inputPath;
	}
	public static void setOutputPath(String outputPath) {
		FilePathContainer.outputPath = outputPath;
		checkIfDirectoryExist(outputPath);
	}
	public static void setNetworkPath(String networkPath) {
		FilePathContainer.networkPath = networkPath;
		checkIfDirectoryExist(networkPath);
	}
	
	private static void checkIfDirectoryExist(String filePath){
		int pos1 = filePath.lastIndexOf('/');
		int pos2 = filePath.lastIndexOf('\\');
		
		//If both was -1, it means filePath has no directory included
		if(pos1 != -1 && pos2 != -1){
			int usedPos = pos1 > pos2 ? pos1 : pos2;
			String dirPath = filePath.substring(0, usedPos);
			File dir = new File(dirPath);
			if(!dir.mkdirs()){
				System.out.println("Unable to create path" + dirPath);
			}
		}
	}
}
