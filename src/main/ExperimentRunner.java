package main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import container.Parameters;
import parser.JsonReader;
import variable.Environment;
import variable.FilePathContainer;

public class ExperimentRunner {	
	private int experimentRounds = 1;
	public static int currentRound = 1;
	
	private JsonReader decoder;
	
	public ExperimentRunner(String inputPath, String outputPath, int experimentRounds){
		FilePathContainer.setInputPath(inputPath);
		FilePathContainer.setOutputDirectory(outputPath);
		//FilePathContainer.setOutputDirectory("experiment/case8-1024Mbps-fifo-dynamic");
		decoder = new JsonReader();
		this.experimentRounds = experimentRounds;
	}
	
	public void runExperiment(){
		try {
			Parameters param = decoder.readInputFile(FilePathContainer.inputPath);
			for(;currentRound <= experimentRounds; currentRound++){
				FilePathContainer.setExperimentRound(currentRound);
				runExperiment(param);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void runExperiment(Parameters param) throws FileNotFoundException{
		PrintStream stream = prepareLogPath();
		System.setOut(stream);
		
		VmigSim vmigsim = new VmigSim();
		vmigsim.startSimulation(param);
		Environment.migrationResult.printLog();
		Environment.migrationResult.printNetwork();
		
		stream.close();
	}
	
	private PrintStream prepareLogPath(){
		String logPath = FilePathContainer.getLogFilePath();
		FileOutputStream fileStream;
		try {
			fileStream = new FileOutputStream(logPath);
			PrintStream stream =  new PrintStream(fileStream);
			return stream;
		} catch (FileNotFoundException e) {
			System.out.println("Output file's path not found.");
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	
	public static void main(String args[]){		
		if(args.length == 3){
			String inputPath = args[0];
			String outputPath = args[1];
			int experimentRound = Integer.valueOf(args[2]);
			
			ExperimentRunner runner = new ExperimentRunner(inputPath, outputPath, experimentRound);
			runner.runExperiment();
		}
		else{
			System.out.println("VmigSim need 3 arguments for input path, output path, and experiment rounds.");
		}
		//System.out.println("Test!");
	}
}
