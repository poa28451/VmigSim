package main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import container.Parameters;
import parser.JsonReader;
import variable.Environment;
import variable.FilePathContainer;

public class ExperimentRunner {
	/*
	private final String outputPath = "result\\";
	private final String outputName = "output";
	
	private int scheduleType = Constant.FIFO;
	private int migrationType = Constant.OFFLINE;
	private int networkType = Constant.STATIC;
	private int scenario = Constant.SCENE_C;
	private int vmRam = 1024;
	private int bandwidth = 500;*/
	/*private int[] scheduleType;
	private int[] migrationType;
	private int[] networkType;
	private int[] scenarioType;
	private int[] vmRamType;// = {512, 1024};
	private double[] bandwidthType;// = {64, 256, 1024};
	
	private int expNum = 7;
*/	
	/*private String inputPath;
	private String outputPath;*/
	
	private int experimentRounds = 1;
	public static int currentRound = 1;
	
	private JsonReader decoder;
	
	public ExperimentRunner(String inputPath, String outputPath, int experimentRounds){
		FilePathContainer.setInputPath(inputPath);
		FilePathContainer.setOutputDirectory(outputPath);
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
		/*Environment.setNetworkInterval(17);
		Environment.setMigrationTimeLimit(21600);
		System.out.println(NetworkGenerator.calculateIntervalFraction(21599.999999999975));*/
		
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
		

		/*FilePathContainer.setOutputDirectory(args[1]);
		for(int i=0; i<5; i++){
			FilePathContainer.setExperimentRound(i+1);
			System.out.println(FilePathContainer.getLogFilePath());
		}*/
		
		/*Random ran = new Random();
		for(int i=0; i<100; i++){
			double chance = ran.nextGaussian();
			if(chance > 0) chance *= -1;
			double bw = Math.max(1, Math.min(100, (int) 100 + chance * 54.8222));
			System.out.println(bw);
		}*/
		
		/*Random ran = new Random();
		for(int i=0; i<100; i++){
			double range = 100 - 0;
	        double mid = 0 + range / 2.0;
	        double unitGaussian = ran.nextGaussian();
	        double biasFactor = Math.exp(1.0);
	        double retval = mid+(range*(biasFactor/(biasFactor+Math.exp(-unitGaussian/(1.0-0.548222)))-0.5));
	        System.out.println(retval);
		}*/
		
	}
	
	/*public PrintStream createOutputFilename(double bw,
			int schedType, int migType, int conType, int netType) throws FileNotFoundException{
		String schedule = Constant.scheduleKeyName.get(schedType);
		String migration = Constant.migrationKeyName.get(migType);
		String control = Constant.controlKeyName.get(conType);
		String network = Constant.networkKeyName.get(netType);
		
		String filename = expNum + "" + currentRound + "-" + outputName + bw +
				"-" + schedule + "-" + migration + "-" + control + "-" + network + ".txt";
		return new PrintStream(new FileOutputStream(outputPath + filename));
	}*/
	
	/*public Parameters setParameters(){
		double timeLimit = ExperimentParameters.COMMON_TIME_LIMIT;
		double bandwidth = bandwidthType[0];
		int vmRam = vmRamType[0];
		int scenario = scenarioType[ExperimentParameters.SCENE_C];
		int schedule = scheduleType[Constant.PRIORITY_BASED];
		int migration = migrationType[Constant.PRECOPY];
		int network = networkType[Constant.DYNAMIC];
		
		ArrayList<VmSpec> vmSpecList = createVmSpecList(scenario, vmRam);
	
		int pageSizeKB = ExperimentParameters.PAGESIZE_KB;
		double wwsPageRatio = ExperimentParameters.WWS_RATE;
		double wwsDirtyRate = ExperimentParameters.WWS_DIRTY_RATE;
		double normalDirtyRate = ExperimentParameters.NORMAL_DIRTY_RATE;
		int maxPreCopyRound = ExperimentParameters.MAX_PRECOPY_ROUND;
		int minDirtyPage = ExperimentParameters.MIN_DIRTY_PAGE;
		int maxNoProgressRound = ExperimentParameters.MAX_NO_PROGRESS;
		double networkInterval = ExperimentParameters.NETWORK_INTERVAL;
		double networkSD = ExperimentParameters.NETWORK_SD;
		
		Parameters param = new Parameters();
		//param.setVmRam(vmRam);
		//param.setScenarioType(scenario);
		param.setBandwidth(bandwidth);
		param.setTimeLimit(timeLimit);
		param.setScheduleType(schedule);
		param.setMigrationType(migration);
		param.setNetworkType(network);
		param.setPageSizeKB(pageSizeKB);
		param.setVmSpecList(vmSpecList);
		
		param.setWwsPageRatio(wwsPageRatio);
		param.setWwsDirtyRate(wwsDirtyRate);
		param.setNormalDirtyRate(normalDirtyRate);
		param.setMaxPreCopyRound(maxPreCopyRound);
		param.setMinDirtyPage(minDirtyPage);
		param.setMaxNoProgressRound(maxNoProgressRound);
		param.setNetworkInterval(networkInterval);
		param.setNetworkSD(networkSD);
		
		return param;
	}*/
	
	/*public void runExperiment(int bw, int vmRam, double timeLimit, int schedType,
			int migType, int netType) throws FileNotFoundException{
		VMigSim run = new VMigSim();
		PrintStream out;
		for(Integer scene : Constant.sceneName.keySet()){
			//example of filename: "outputA512-fifo-offline-static.txt"
			out = createOutputFilename(scene, bw, vmRam, schedType, migType, netType);
			System.setOut(out);
			run.startExperiment(bw, vmRam, timeLimit, scene, schedType, migType, netType);
			out.close();
		}
	}*/
	
	/*public void initialMigrationScenario(){
		scheduleType = new int[]{Constant.FIFO, Constant.PRIORITY_BASED};
		migrationType = new int[]{Constant.OFFLINE, Constant.PRECOPY};
		networkType = new int[]{Constant.STATIC, Constant.DYNAMIC};
		scenarioType = new int[]{ExperimentParameters.SCENE_A, ExperimentParameters.SCENE_B, ExperimentParameters.SCENE_C};
		vmRamType = new int[]{512, 1024};
		bandwidthType = new double[]{64, 500, 1024};
	}*/
	
	/*private ArrayList<VmSpec> createVmSpecList(int scenario, int ram){
		ArrayList<VmSpec> vmSpecList = new ArrayList<VmSpec>();
		VmSpec spec1, spec2, spec3;
		switch (scenario) {
			case ExperimentParameters.SCENE_A:
				spec1 = new VmSpec(ExperimentParameters.PRIORITY_1_AMOUNT, ram, Constant.PRIORITY_1, ExperimentParameters.PRIORITY_1_QOS);
				vmSpecList.add(spec1);
				break;
			case ExperimentParameters.SCENE_B:
				spec1 = new VmSpec(ExperimentParameters.PRIORITY_1_AMOUNT, ram, Constant.PRIORITY_1, ExperimentParameters.PRIORITY_1_QOS);
				spec2 = new VmSpec(ExperimentParameters.PRIORITY_2_AMOUNT, ram, Constant.PRIORITY_2, ExperimentParameters.PRIORITY_2_QOS);
				vmSpecList.add(spec1);
				vmSpecList.add(spec2);
				break;
			case ExperimentParameters.SCENE_C:
				spec1 = new VmSpec(ExperimentParameters.PRIORITY_1_AMOUNT, ram, Constant.PRIORITY_1, ExperimentParameters.PRIORITY_1_QOS);
				spec2 = new VmSpec(ExperimentParameters.PRIORITY_2_AMOUNT, ram, Constant.PRIORITY_2, ExperimentParameters.PRIORITY_2_QOS);
				spec3 = new VmSpec(ExperimentParameters.PRIORITY_3_AMOUNT, ram, Constant.PRIORITY_3, ExperimentParameters.PRIORITY_3_QOS);
				vmSpecList.add(spec1);
				vmSpecList.add(spec2);
				vmSpecList.add(spec3);
				break;
			default:
				break;
		}
		return vmSpecList;	
	}*/
	
	/*public void run2mbpsExperiment() throws FileNotFoundException{
		VMigSim run = new VMigSim();
		double timeLimit = Constant.commonTimeLimit;
		PrintStream out;
		out = new PrintStream(new FileOutputStream("outputA-2.txt"));
		System.setOut(out);
		run.startExperiment(2, vmRam, timeLimit, Constant.SCENE_A, scheduleType, migrationType, networkType);
		out.close();
		
		out = new PrintStream(new FileOutputStream("outputB-2.txt"));
		System.setOut(out);
		run.startExperiment(2, vmRam, timeLimit, Constant.SCENE_B, scheduleType, migrationType, networkType);
		out.close();
		
		out = new PrintStream(new FileOutputStream("outputC-2.txt"));
		System.setOut(out);
		run.startExperiment(2, vmRam, timeLimit, Constant.SCENE_C, scheduleType, migrationType, networkType);
		out.close();
	}
	
	public void run500mbpsExperiment() throws FileNotFoundException{
		VMigSim run = new VMigSim();
		double timeLimit = Constant.commonTimeLimit;
		PrintStream out;
		out = new PrintStream(new FileOutputStream("outputA-500.txt"));
		System.setOut(out);
		run.startExperiment(500, vmRam, timeLimit, Constant.SCENE_A, scheduleType, migrationType, networkType);
		out.close();
		
		out = new PrintStream(new FileOutputStream("outputB-500.txt"));
		System.setOut(out);
		run.startExperiment(500, vmRam, timeLimit, Constant.SCENE_B, scheduleType, migrationType, networkType);
		out.close();
		
		out = new PrintStream(new FileOutputStream("outputC-500.txt"));
		System.setOut(out);
		run.startExperiment(500, vmRam, timeLimit, Constant.SCENE_C, scheduleType, migrationType, networkType);
		out.close();
	}
	
	public void run6gbpsExperiment() throws FileNotFoundException{
		VMigSim run = new VMigSim();
		double timeLimit = Constant.commonTimeLimit;
		PrintStream out;
		out = new PrintStream(new FileOutputStream("outputA-6000.txt"));
		System.setOut(out);
		run.startExperiment(6000, vmRam, timeLimit, Constant.SCENE_A, scheduleType, migrationType, networkType);
		out.close();
		
		out = new PrintStream(new FileOutputStream("outputB-6000.txt"));
		System.setOut(out);
		run.startExperiment(6000, vmRam, timeLimit, Constant.SCENE_B, scheduleType, migrationType, networkType);
		out.close();
		
		out = new PrintStream(new FileOutputStream("outputC-6000.txt"));
		System.setOut(out);
		run.startExperiment(6000, vmRam, timeLimit, Constant.SCENE_C, scheduleType, migrationType, networkType);
		out.close();
	}*/
	
	/*private HashMap<Integer, String> createSceneName(){
		HashMap<Integer, String> sceneName = new HashMap<Integer, String>();
		sceneName.put(Constant.SCENE_A, "A");
		sceneName.put(Constant.SCENE_B, "B");
		sceneName.put(Constant.SCENE_C, "C");
		return sceneName;
	}
	
	private HashMap<Integer, String> createMigrationName(){
		HashMap<Integer, String> migrationName = new HashMap<Integer, String>();
		migrationName.put(Constant.OFFLINE, "offline");
		migrationName.put(Constant.PRECOPY, "precopy");
		return migrationName;
	}
	
	private HashMap<Integer, String> createScheduleName(){
		HashMap<Integer, String> scheduleName = new HashMap<Integer, String>();
		scheduleName.put(Constant.FIFO, "fifo");
		scheduleName.put(Constant.DYNAMIC, "priority");
		return scheduleName;
	}
	
	private HashMap<Integer, String> createNetworkName(){
		HashMap<Integer, String> networkName = new HashMap<Integer, String>();
		networkName.put(Constant.STATIC, "static");
		networkName.put(Constant.DYNAMIC, "dynamic");
		return networkName;
	}*/
	/*private static int calculateIntervalNum(double time){
		double interval = Environment.networkInterval;
		double startFraction = Constant.START_INTERVAL / interval;
		double temp = time / interval;
		double tempFraction = temp - Math.floor(temp);
		double num = 0;
		
		double t = new BigDecimal( Constant.START_INTERVAL / interval).setScale(10, BigDecimal.ROUND_HALF_UP).doubleValue();
		double tt = new BigDecimal( time / interval).setScale(10, BigDecimal.ROUND_HALF_UP).doubleValue();
		double ttt = new BigDecimal(tt - Math.floor(tt)).setScale(10, BigDecimal.ROUND_HALF_UP).doubleValue();
		//if((int)(tt - t) != (int)Math.floor(temp)){
		if(ttt < t){
			num = Math.floor(temp) - 1;
			double x = temp - t;
			double y = Math.floor(temp);
			System.out.println("x = " + x);
			System.out.println("y = " + y);
			System.out.println("temp = " + temp);
			System.out.println("start = " + startFraction);
		}
		else{
			num = Math.floor(temp);
		}
		
		return (int) num;
	}*/
}
