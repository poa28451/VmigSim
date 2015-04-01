package cloudsim_inherit;

import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.Vm;

import variable.Constant;
import variable.Environment;

public class VmigSimVm extends Vm{
	private int priority;
	private int qos;
	private int memoryPageNum, dirtyPageNum;
	private double startClock;
	private double stopClock;
	private double migrationTime;
	private double downtime;
	private double totalTransferredKB;
	private boolean isViolated;
	private boolean isMigrated;
	
	public VmigSimVm(int id, int userId, double mips, int numberOfPes, int ram,
			long bw, long size, int qos, int priority, String vmm, CloudletScheduler cloudletScheduler) {
		
		super(id, userId, mips, numberOfPes, ram, bw, size, vmm, cloudletScheduler);		
		setQos(qos);
		setPriority(priority);
		setMemoryPageNum(ram);
		setStartClock(0);
		setStopClock(0);
		setMigrationTime(0);
		setDowntime(0);
		setTotalTransferredKB(0);
		setViolated(false);
		setMigrated(false);
	}
	
	public int getQos() {
		return qos;
	}
	
	public void setQos(int qos) {
		this.qos = qos;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getMemoryPageNum() {
		return memoryPageNum;
	}

	public void setMemoryPageNum(int ram) {
		int ramInKb = ram*Constant.KILO_BYTE;
		this.memoryPageNum = ramInKb/Environment.pageSizeKB;
	}

	public int getDirtyPageNum() {
		return dirtyPageNum;
	}

	public void setDirtyPageNum(int dirtyPageNum) {
		this.dirtyPageNum = dirtyPageNum;
	}
	
	public void resetDirtyPageNum(){
		setDirtyPageNum(0);
	}
	
	public double getStartClock() {
		return startClock;
	}

	public void setStartClock(double startClock) {
		this.startClock = startClock;
	}

	public double getStopClock() {
		return stopClock;
	}

	public void setStopClock(double stopClock) {
		this.stopClock = stopClock;
	}

	public double getMigrationTime() {
		return migrationTime;
	}

	public void setMigrationTime(double migrationTime) {
		this.migrationTime = migrationTime;
	}
	
	public void updateMigrationTime(double additionTime){
		this.migrationTime += additionTime;
	}

	public double getDowntime() {
		return downtime;
	}

	public void setDowntime(double downTime) {
		this.downtime = downTime;
	}

	public double getTotalTransferredKB() {
		return totalTransferredKB;
	}

	public void setTotalTransferredKB(double totalTransferred) {
		this.totalTransferredKB = totalTransferred;
	}

	public boolean isViolated() {
		return isViolated;
	}

	public void setViolated(boolean isViolated) {
		this.isViolated = isViolated;
	}

	public boolean isMigrated() {
		return isMigrated;
	}

	public void setMigrated(boolean isMigrated) {
		this.isMigrated = isMigrated;
	}
	
	public double getViolationPercenteage(){
		double percent = 0;
		if(isViolated){
			double violate = downtime - qos;
			percent = violate * 100 / qos;
		}
		return percent;
	}
}
