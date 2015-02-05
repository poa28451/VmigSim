package variable;

import java.util.ArrayList;

import container.MigrationResults;
import container.VmSpec;

public class Environment {
	public static double migrationTimeLimit;
	//public static int vmRam;//*******
	public static int scheduleType;
	public static int migrationType;
	public static int controlType;
	public static int networkType;
	public static int pageSizeKB;
	public static ArrayList<VmSpec> vmSpecList;
	
	public static double wwsPageRatio, wwsDirtyRate;
	public static double normalDirtyRate;
	public static int maxPreCopyRound;
	public static int minDirtyPage;
	public static int maxNoProgressRound;
	public static double networkInterval;
	public static double networkSD;
	
	public static MigrationResults migrationResult;
	
	/*public static void setBandwidth(int min, int max){
		Random r = new Random();
		//bandwidth = min + r.nextInt(max - min + 1);
		bandwidth = min + (r.nextDouble() * (max - min));
		
		System.out.println("WAN bandwidth generated at: " + bandwidth+ "Mbps ");
	}*/	
	
	/*public static void setMigrationTimeLimit(double min, double max){
		Random r = new Random();
		migrationLimit = min + (r.nextDouble() * (max - min));
		
		System.out.println("Migration time limit generated at: " + migrationLimit + " seconds");
	}*/
	
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
	
	/*public static void setMinMaxQos(int min, int max){
		minQos = min;
		maxQos = max;
	}*/
	
	/*public static int generateQos(){
		Random r = new Random();
		//double result = min + (r.nextDouble() * (max - min));
		int result = minQos + (r.nextInt(maxQos - minQos + 1));
		return result;
	}*/

	/*public static void setVmRam(int vmRam) {
		Environment.vmRam = vmRam;
	}*/

	/*public static void setScenarioType(int scenarioType) {
		Environment.scenarioType = scenarioType;
	}*/

	public static void setWwsPageRatio(double wwsPageRatio) {
		Environment.wwsPageRatio = wwsPageRatio;
	}

	public static void setWwsDirtyRate(double wwsDirtyRate) {
		Environment.wwsDirtyRate = wwsDirtyRate;
	}

	public static void setNormalDirtyRate(double normalDirtyRate) {
		Environment.normalDirtyRate = normalDirtyRate;
	}

	public static void setPageSizeKB(int pageSizeKB) {
		Environment.pageSizeKB = pageSizeKB;
	}

	public static void setMaxPreCopyRound(int maxPreCopyRound) {
		Environment.maxPreCopyRound = maxPreCopyRound;
	}

	public static void setMinDirtyPage(int minDirtyPage) {
		Environment.minDirtyPage = minDirtyPage;
	}

	public static void setMaxNoProgressRound(int maxNoProgressRound) {
		Environment.maxNoProgressRound = maxNoProgressRound;
	}

	public static void setNetworkInterval(double networkInterval) {
		Environment.networkInterval = networkInterval;
	}

	public static void setNetworkSD(double networkSD) {
		Environment.networkSD = networkSD;
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

	public static int getHighestVmRam(){
		int maxRam = 0;
		for(VmSpec spec : vmSpecList){
			if(spec.getRam() > maxRam){
				maxRam = spec.getRam();
			}
		}
		return maxRam;
	}
	
	/*public static int getTotalVmRam(){
		int totalRam = 0;
		for(VmSpec spec : vmSpecList){
			totalRam += (spec.getRam() * spec.getAmountVm());
		}
		return totalRam;
	}*/
	
	public static int getTotalVmAmount(){
		int amount = 0;
		for(VmSpec spec : vmSpecList){
			amount += spec.getVmAmount();
		}
		return amount;
	}
}
