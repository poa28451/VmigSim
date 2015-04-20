package variable;

import java.util.ArrayList;

import container.MigrationResults;
import container.VmSpec;

public class Environment {
	public static double migrationTimeLimit;
	public static int scheduleType;
	public static int migrationType;
	public static int controlType;
	public static int networkType;
	//public static int pageSizeKB;
	public static ArrayList<VmSpec> vmSpecList;
	
	public static double wwsPageRatio, wwsDirtyRate;
	public static double normalDirtyRate;
	public static int maxPreCopyRound;
	public static int minDirtyPage;
	public static int maxNoProgressRound;
	
	public static MigrationResults migrationResult;
	//public static double maxDowntimeMs;
	public static int threadNum;
	public static boolean isRecordedTrace;
	public static String traceFile;
	
	public static void setMigrationTimeLimit(double migrationTimeLimit){
		Environment.migrationTimeLimit = migrationTimeLimit;
	}
	
	public static void setScheduleType(int scheduleType){
		Environment.scheduleType = scheduleType;
	}
	
	public static void setMigrationType(int migrationType){
		Environment.migrationType = migrationType;
	}

	public static void setControlType(int controlType) {
		Environment.controlType = controlType;
	}

	public static void setNetworkType(int networkType) {
		Environment.networkType = networkType;
	}

	public static void setWwsPageRatio(double wwsPageRatio) {
		Environment.wwsPageRatio = wwsPageRatio;
	}

	public static void setWwsDirtyRate(double wwsDirtyRate) {
		Environment.wwsDirtyRate = wwsDirtyRate;
	}

	public static void setNormalDirtyRate(double normalDirtyRate) {
		Environment.normalDirtyRate = normalDirtyRate;
	}

	/*public static void setPageSizeKB(int pageSizeKB) {
		Environment.pageSizeKB = pageSizeKB;
	}*/

	public static void setMaxPreCopyRound(int maxPreCopyRound) {
		Environment.maxPreCopyRound = maxPreCopyRound;
	}

	public static void setMinDirtyPage(int minDirtyPage) {
		Environment.minDirtyPage = minDirtyPage;
	}

	public static void setMaxNoProgressRound(int maxNoProgressRound) {
		Environment.maxNoProgressRound = maxNoProgressRound;
	}
	
	public static ArrayList<VmSpec> getVmSpecList() {
		return vmSpecList;
	}
	
	public static void setVmSpecList(ArrayList<VmSpec> vmSpecList) {
		Environment.vmSpecList = vmSpecList;
	}
	
	public static void setMigrationResult(MigrationResults migrationResult) {
		Environment.migrationResult = migrationResult;
	}

	public static void setThreadNum(int threadNum) {
		Environment.threadNum = threadNum;
	}
	
	public static void setIsRecordedTrace(boolean isRecordedTrace){
		Environment.isRecordedTrace = isRecordedTrace;
	}
	
	public static void setTraceFile(String traceFile){
		Environment.traceFile = traceFile;
	}

	public static int getHighestVmRam(){
		int maxRam = 0;
		for(VmSpec spec : vmSpecList){
			if(spec.getRam() > maxRam){
				maxRam = spec.getRam();
			}
		}
		return maxRam;
	}
	
	public static int getTotalVmAmount(){
		int amount = 0;
		for(VmSpec spec : vmSpecList){
			amount += spec.getVmAmount();
		}
		return amount;
	}
}
