package cloudsim_inherit;

import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.Vm;

import variable.Constant;
import variable.Environment;

public class VmTest extends Vm{
	private int priority;
	private int qos;
	//private ArrayList<Integer> memoryPage;
	private int memoryPageNum, dirtyPageNum;
	private double startClock;
	private double stopClock;
	private double migrationTime;
	private double downTime;
	private boolean isViolated;
	private boolean isMigrated;
	
	public VmTest(int id, int userId, double mips, int numberOfPes, int ram,
			long bw, long size, int qos, int priority, String vmm, CloudletScheduler cloudletScheduler) {
		
		super(id, userId, mips, numberOfPes, ram, bw, size, vmm, cloudletScheduler);		
		setQos(qos);
		setPriority(priority);
		//setMemoryPage(new ArrayList<Integer>());
		setMemoryPageNum(ram);
		setStartClock(0);
		setStopClock(0);
		setMigrationTime(0);
		setDownTime(0);
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

	/*public ArrayList<Integer> getMemoryPage() {
		return memoryPage;
	}*/

	/*public void setMemoryPage(ArrayList<Integer> memoryPage) {
		//Make the RAM into KB scale
		int ramInKb = getRam()*Constant.KILO_BYTE;
		
		//Create a list of memory pages VM have, each page size is 4KB
		for(int i=0; i<ramInKb/Constant.PAGESIZE_KB; i++){
			memoryPage.add(Constant.CLEAN_PAGE);
		}
		this.memoryPage = memoryPage;
	}*/

	/*public void resetMemoryPage(){
		for(int i=0; i<memoryPage.size(); i++){
			memoryPage.set(i, Constant.CLEAN_PAGE);
		}
	}*/

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

	public double getDownTime() {
		return downTime;
	}

	public void setDownTime(double downTime) {
		this.downTime = downTime;
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
}
