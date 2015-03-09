package container;

import java.util.ArrayList;

import broker_collaborator.NetworkGenerator;
import cloudsim_inherit.VmigSimVm;
import variable.Constant;
import variable.Environment;

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
	
	public double getMaxBandwidth(){
		return NetworkGenerator.getMaxBandwidth();
	}
	
	public double getMeanBandwidth() {
		
		return NetworkGenerator.getMeanBandwidth();
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
