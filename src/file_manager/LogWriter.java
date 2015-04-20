package file_manager;

import variable.Constant;
import cloudsim_inherit.VmigSimVm;
import container.MigrationResults;

public class LogWriter {
	private static MigrationResults migResult;
	
	/**
	 * Print the log file of the migration result. Note that this is differ from CloudSim's log.
	 * 	This only contain the RESULT of VmigSim
	 */
	public static void writeLogToFile(MigrationResults result){
		migResult = result;
		writeMigratedVmLog();
		writeOverallLog();
		writeEnvironmentLog();
	}

	private static void writeMigratedVmLog() {
		System.out.println();
		System.out.println("Migrated VM details ::");
		for(VmigSimVm vm : migResult.getMigratedVm()){
			int id = vm.getId();
			double migrationTime = vm.getMigrationTime();
			double downTime = vm.getDowntime();
			int ram = vm.getRam();
			boolean violate = vm.isViolated();
			boolean result = vm.isMigrated();
			int priority = vm.getPriority();
			int qos = vm.getQos();
			
			System.out.println("\tVM: " + id + " finished the migration with the time " + migrationTime + " seconds");
			System.out.println("\t\tRAM = " + ram + " MB");
			System.out.println("\t\tPriority = " + priority);
			System.out.println("\t\tDowntime = " + downTime + " seconds");
			System.out.println("\t\tQoS = " +  qos + " seconds / violate = " + violate);
			System.out.println("\t\tMigration result = " + result);
		}
		System.out.println();
	}

	private static void writeOverallLog() {
		System.out.println("Overall details ::");
		System.out.println("\tTotal migrated VM = " + migResult.getTotalMigratedVm() + " / " + migResult.getTotalVm());
		System.out.println("\tTotal migrated VM in each priority: ");
		System.out.println("\t\tPriority 1 = " + migResult.getMigratedPriority1() + " / " + migResult.getTotalPriority1());
		System.out.println("\t\tPriority 2 = " + migResult.getMigratedPriority2() + " / " + migResult.getTotalPriority2());
		System.out.println("\t\tPriority 3 = " + migResult.getMigratedPriority3() + " / " + migResult.getTotalPriority3());
		
		System.out.println("\tTotal violated VM = " + migResult.getTotalViolatedVm() + " / " + migResult.getTotalVm());
		System.out.println("\tTotal violated VM in each priority: ");
		System.out.println("\t\tPriority 1 = " + migResult.getViolatedPriority1() + " / " + migResult.getTotalPriority1());
		System.out.println("\t\tPriority 2 = " + migResult.getViolatedPriority2() + " / " + migResult.getTotalPriority2());
		System.out.println("\t\tPriority 3 = " + migResult.getViolatedPriority3() + " / " + migResult.getTotalPriority3());
		
		double excessP1 = migResult.getAvgExcessPriority1();
		double excessP2 = migResult.getAvgExcessPriority2();
		double excessP3 = migResult.getAvgExcessPriority3();
		double allExcess = migResult.getAllAvgExcessDowntime();
		System.out.println("\tAvg excess downtime percentage = " + allExcess + "%");
		System.out.println("\tAvg excess downtime percentage in each priority: ");
		System.out.println("\t\tPriority 1 = " + excessP1 + "%");
		System.out.println("\t\tPriority 2 = " + excessP2 + "%");
		System.out.println("\t\tPriority 3 = " + excessP3 + "%");
		
		double avgMigTime, avgDownTime;		
		avgMigTime = migResult.getAverageMigrationTime();
		avgDownTime = migResult.getAverageDownTime();
		
		/*System.out.println("\tTotal migration time = " + migResult.getTotalMigrationTime() + 
				" (Avg. = " + avgMigTime + ") secs");*/
		System.out.println("\tTotal migration time = " + migResult.getRealMigTime() + 
				" (Avg. = " + avgMigTime + ") secs");
		System.out.println("\tTotal down time = " + migResult.getTotalDowntime() +
				" (Avg. = " + avgDownTime + ") secs");
		System.out.println();
	}
	
	private static void writeEnvironmentLog() {
		System.out.println("Environment details ::");
		System.out.println("\tThread number = " + migResult.getThreadNum());
		writeNetworkDetail();
		//System.out.println("\tPage size = " + migResult.getPageSize() + " KB");
		System.out.println("\tTime limit = " + migResult.getTimeLimit() + " secs");
		System.out.println("\tSchedule type = " + migResult.getScheduleName());
		System.out.println("\tMigration type = " + migResult.getMigrationName());
		if(migResult.getMigrationType() == Constant.PRECOPY){
			writePreCopyDetail();
		}
		System.out.println("\tControl type = " + migResult.getControlName());
		System.out.println();
	}

	private static void writeNetworkDetail() {
		System.out.println("\tRecorded bandwidth trace = " + migResult.isRecordedTrace());
		if(migResult.isRecordedTrace()){
			System.out.println("\t\tTrace file name = " + migResult.getTraceFile());
		}
		else{
			System.out.println("\t\tNetwork type = " + migResult.getNetworkName());
			System.out.println("\t\tMax bandwidth = " + migResult.getMaxBandwidth() + " Mbps");
			System.out.println("\t\tMean bandwidth = " + migResult.getMeanBandwidth() + " Mbps");
			System.out.println("\t\tNetwork's standard deviation = " + migResult.getNetworkSD() + "%");
		}
		
		System.out.println("\t\tNetwork interval = " + migResult.getNetworkInterval());
	}

	private static void writePreCopyDetail() {
		System.out.println("\t\tWWS page percentage = " + migResult.getWwsRatio() + "%");
		System.out.println("\t\tWWS dirty rate = " + migResult.getWwsDirtyRate() + "%");
		System.out.println("\t\tNormal dirty rate = " + migResult.getNormalDirtyRate() + "%");
		System.out.println("\t\tMax pre-copy iteration = " + migResult.getMaxPreCopyRound());
		System.out.println("\t\tMin dirty page = " + migResult.getMinDirtyPage());
		System.out.println("\t\tMax no-progress round = " + migResult.getMaxNoProgressRound());
	}
}
