package variable;

public class FilePathContainer {
	public static String inputPath;
	public static String outputPath;
	public static String networkPath = "network\\";
	
	public static void setInputPath(String inputPath) {
		FilePathContainer.inputPath = inputPath;
	}
	public static void setOutputPath(String outputPath) {
		FilePathContainer.outputPath = outputPath;
	}
	public static void setNetworkPath(String networkPath) {
		FilePathContainer.networkPath = networkPath;
	}
}
