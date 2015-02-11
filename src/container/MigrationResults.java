package container;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import broker_collaborator.NetworkGenerator;
import cloudsim_inherit.VmigSimVm;
import variable.Constant;
import variable.Environment;
import variable.FilePathContainer;

/**
 * This class has responsibilities for collecting the migration result of each VM and summarize
 * 	into usable information. In additional, MigrationResults take responsibility to print results,
 * 	and logs of VmigSim to the file.
 * @author tawee_000
 *
 */
public class MigrationResults {
	private ArrayList<VmigSimVm> allVm;
	private ArrayList<VmigSimVm> migratedVm;
	private ArrayList<VmigSimVm> violatedVm;
	private int totalPriority1, totalPriority2, totalPriority3;
	private int migratedPriority1, migratedPriority2, migratedPriority3;
	
	public MigrationResults(ArrayList<VmigSimVm> allVm, ArrayList<VmigSimVm> migratedVm){
		setAllVm(allVm);
		setMigratedVm(migratedVm);
		setViolatedVm(new ArrayList<VmigSimVm>());
		setTotalPriority1(0);
		setTotalPriority2(0);
		setTotalPriority3(0);
		setMigratedPriority1(0);
		setMigratedPriority2(0);
		setMigratedPriority3(0);
		
		countTotalPriority();
		countMigratedPriority();
		countTotalViolated();
	}
	
	/**
	 * Print the file of the migration result, This file will be used by the Web Application
	 * 	to create a graph and other works.w
	 */
	/*public void printResult(){
		
	}*/
	
	/**
	 * Print the log file of the migration result. Note that this is differ from CloudSim's log.
	 * 	This only contain the RESULT of VmigSim
	 */
	public void printLog(){
		printMigratedVmLog();
		printOverallLog();
		printEnvironmentLog();
	}
	
	public void printNetwork(){
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
	
	private void printMigratedVmLog(){
		System.out.println();
		System.out.println("Migrated VM details ::");
		for(VmigSimVm vm : getMigratedVm()){
			int id = vm.getId();
			double migrationTime = vm.getMigrationTime();
			double downTime = vm.getDownTime();
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
	
	private void printOverallLog(){
		System.out.println("Overall details ::");
		System.out.println("\tTotal migrated VM = " + getTotalMigratedVm() + " / " + getTotalVm());
		System.out.println("\tTotal migrated priority: ");
		System.out.println("\t\tPriority 1 = " + getMigratedPriority1() + " / " + getTotalPriority1());
		System.out.println("\t\tPriority 2 = " + getMigratedPriority2() + " / " + getTotalPriority2());
		System.out.println("\t\tPriority 3 = " + getMigratedPriority3() + " / " + getTotalPriority3());
		System.out.println("\tTotal violated VM = " + getTotalViolatedVm());
		System.out.println("\tTotal migration time = " + getTotalMigrationTime() + 
				" (Avg. = " + getTotalMigrationTime()/getTotalMigratedVm() + ") secs");
		System.out.println("\tTotal down time = " + getTotalDownTime() +
				" (Avg. = " + getTotalDownTime()/getTotalMigratedVm() + ") secs");
		System.out.println();
	}
	
	private void printEnvironmentLog(){
		System.out.println("Environment details ::");
		System.out.println("\tBandwidth = " + getBandwidth() + " Mbps");
		System.out.println("\tNetwork type = " + getNetworkName());
		if(getNetworkType() == Constant.DYNAMIC){
			printDynamicBandwidthDetail();
		}
		System.out.println("\tNetwork Interval = " + getNetworkInterval());
		System.out.println("\tPage size = " + getPageSize() + " KB");
		System.out.println("\tTime limit = " + getTimeLimit() + " secs");
		System.out.println("\tSchedule type = " + getScheduleName());
		System.out.println("\tMigration type = " + getMigrationName());
		if(getMigrationType() == Constant.PRECOPY){
			printPreCopyDetail();
		}
		System.out.println("\tControl type = " + getControlName());
		System.out.println();
	}
	
	private void printDynamicBandwidthDetail(){
		System.out.println("\t\tNetwork's standard deviation = " + getNetworkSD() + "%");
	}
	private void printPreCopyDetail(){
		System.out.println("\t\tNormal dirty rate = " + getNormalDirtyRate() + "%");
		System.out.println("\t\tWWS dirty rate = " + getWwsDirtyRate() + "%");
		System.out.println("\t\tWWS page ratio = " + getWwsRatio() + "%");
		System.out.println("\t\tMax pre-copy iteration = " + getMaxPreCopyRound());
		System.out.println("\t\tMin dirty page = " + getMinDirtyPage());
		System.out.println("\t\tMax no-progress round = " + getMaxNoProgressRound());
	}
	
	private void countTotalPriority(){
		for(VmigSimVm vm : getAllVm()){
			int priority = vm.getPriority();
			switch (priority) {
				case Constant.PRIORITY_1:
					setTotalPriority1(getTotalPriority1() + 1);
					break;
				
				case Constant.PRIORITY_2:
					setTotalPriority2(getTotalPriority2() + 1);
					break;
					
				case Constant.PRIORITY_3:
					setTotalPriority3(getTotalPriority3() + 1);
					break;
					
				default:
					break;
			}
		}
	}
	
	private void countMigratedPriority(){
		for(VmigSimVm vm : getMigratedVm()){
			int priority = vm.getPriority();
			switch (priority) {
				case Constant.PRIORITY_1:
					setMigratedPriority1(getMigratedPriority1() + 1);
					break;
				
				case Constant.PRIORITY_2:
					setMigratedPriority2(getMigratedPriority2() + 1);
					break;
					
				case Constant.PRIORITY_3:
					setMigratedPriority3(getMigratedPriority3() + 1);
					break;
					
				default:
					break;
			}
		}
	}
	
	private void countTotalViolated(){
		for(VmigSimVm vm : getMigratedVm()){
			if(vm.isViolated()){
				getViolatedVm().add(vm);
			}
		}
	}
	
	public double getTotalMigrationTime(){
		double migrationTime = 0;
		for(VmigSimVm vm : getMigratedVm()){
			migrationTime += vm.getMigrationTime();
		}
		return migrationTime;
	}
	
	public double getTotalDownTime(){
		double downTime = 0;
		for(VmigSimVm vm : getMigratedVm()){
			downTime += vm.getDownTime();
		}
		return downTime;
	}
	
	public int getTotalVm(){
		return getAllVm().size();
	}
	
	public int getTotalMigratedVm(){
		return getMigratedVm().size();
	}
	
	public int getTotalViolatedVm(){
		return getViolatedVm().size();
	}
	
	public double getBandwidth(){
		return NetworkGenerator.getOriginalBandwidth();
	}
	
	public double getTimeLimit(){
		return Environment.migrationTimeLimit;
	}
	
	public int getPageSize(){
		return Environment.pageSizeKB;
	}
	
	public int getScheduleType(){
		return Environment.scheduleType;
	}
	
	public String getScheduleName(){
		return Constant.scheduleKeyName.get(Environment.scheduleType);
	}
	
	public int getMigrationType(){
		return Environment.migrationType;
	}
	
	public String getMigrationName(){
		return Constant.migrationKeyName.get(Environment.migrationType);
	}
	
	public int getControlType(){
		return Environment.controlType;
	}
	
	public String getControlName(){
		return Constant.controlKeyName.get(Environment.controlType);
	}
	
	public int getNetworkType(){
		return Environment.networkType;
	}
	
	public String getNetworkName(){
		return Constant.networkKeyName.get(Environment.networkType);
	}
	
	public double getNormalDirtyRate(){
		return Environment.normalDirtyRate;
	}
	
	public double getWwsDirtyRate(){
		return Environment.wwsDirtyRate;
	}
	
	public double getWwsRatio(){
		return Environment.wwsPageRatio;
	}
	
	public int getMaxPreCopyRound(){
		return Environment.maxPreCopyRound;
	}
	
	public int getMinDirtyPage(){
		return Environment.minDirtyPage;
	}
	
	public int getMaxNoProgressRound(){
		return Environment.maxNoProgressRound;
	}
	
	public double getNetworkInterval(){
		return NetworkGenerator.getNetworkInterval();
	}
	
	public double getNetworkSD(){
		return NetworkGenerator.getStddevPercent();
	}
	
	public ArrayList<VmigSimVm> getAllVm() {
		return allVm;
	}
	
	public void setAllVm(ArrayList<VmigSimVm> allVm) {
		this.allVm = allVm;
	}
	
	public ArrayList<VmigSimVm> getMigratedVm() {
		return migratedVm;
	}
	
	public void setMigratedVm(ArrayList<VmigSimVm> migratedVm) {
		this.migratedVm = migratedVm;
	}

	public ArrayList<VmigSimVm> getViolatedVm() {
		return violatedVm;
	}

	public void setViolatedVm(ArrayList<VmigSimVm> violatedVm) {
		this.violatedVm = violatedVm;
	}

	public int getTotalPriority1() {
		return totalPriority1;
	}

	public void setTotalPriority1(int totalPriority1) {
		this.totalPriority1 = totalPriority1;
	}

	public int getTotalPriority2() {
		return totalPriority2;
	}

	public void setTotalPriority2(int totalPriority2) {
		this.totalPriority2 = totalPriority2;
	}

	public int getTotalPriority3() {
		return totalPriority3;
	}

	public void setTotalPriority3(int totalPriority3) {
		this.totalPriority3 = totalPriority3;
	}

	public int getMigratedPriority1() {
		return migratedPriority1;
	}

	public void setMigratedPriority1(int migratedPriority1) {
		this.migratedPriority1 = migratedPriority1;
	}

	public int getMigratedPriority2() {
		return migratedPriority2;
	}

	public void setMigratedPriority2(int migratedPriority2) {
		this.migratedPriority2 = migratedPriority2;
	}

	public int getMigratedPriority3() {
		return migratedPriority3;
	}

	public void setMigratedPriority3(int migratedPriority3) {
		this.migratedPriority3 = migratedPriority3;
	}
}
