package tryclass;

import java.util.Random;

import org.cloudbus.cloudsim.core.CloudSim;

import variable.Constant;
import variable.Environment;
import message.MigrationMessage;
import message.PreCopyMessage;;

public class MigrationManager {
	private MigrationMessage migrationData;
	//private boolean isDoneMigrating;
	private int preCopyRound, noProgressRound;
	private int previousDirtyPage;
	
	public MigrationManager(){
		setPreCopyRound(0);
		setNoProgressRound(0);
	}
	
	public MigrationMessage manageMigration(){
		MigrationMessage migratedData = null;
		switch (Environment.migrationType) {
			case Constant.OFFLINE:
				migratedData = migrateByOffline();
				break;
		
			case Constant.PRECOPY:
				migratedData = migrateByPreCopy();
				break;
				
			default:
				migratedData = migrateByOffline();
				break;
		}
		
		return migratedData;
	}
	
	protected MigrationMessage migrateByOffline(){
		VmTest vm = migrationData.getVm();
		int vmRam = vm.getRam();
		double vmRamKb = convertMbToKb(vmRam);
		/*double migrationTime = calculateMigrationTime(vmRamKb);	*/	
		MigrationMessage msg = new MigrationMessage(vm);
		
		/*vm.setMigrationTime(migrationTime);
		vm.setDownTime(migrationTime);
		msg.setMigrationTime(migrationTime);*/
		msg.setDataSizeKB(vmRamKb);
		msg.setLastMigrationMsg(true);
		//setDoneMigrating(true);//One-time migration
		return msg;
	}
	
	protected MigrationMessage migrateByPreCopy(){
		//double migrationTime = 0;
		VmTest vm = migrationData.getVm();
		//ArrayList<Integer> dirtyPages = null;
		int dirtyPages = Integer.MIN_VALUE;
		PreCopyMessage msg = new PreCopyMessage(vm, CloudSim.clock());
		
		if(preCopyRound == 0){//first round, send every memory page
			int vmRam = vm.getRam();
			double vmRamKb = convertMbToKb(vmRam);
			/*migrationTime = calculateMigrationTime(vmRamKb);*/
			msg.setDataSizeKB(vmRamKb);
		}
		else{
			dirtyPages = findDirtyPage();
			checkIfNoProgress(dirtyPages);
			
			if(!isPreCopyEnd(dirtyPages)){//If not end yet, send dirty pages normally
				msg.setDataSizeKB(dirtyPages * Environment.pageSizeKB);
			}
			else{//If ended already, send final data
				//May add time of transferring registers
				msg.setDataSizeKB(dirtyPages * Environment.pageSizeKB);
				msg.setLastMigrationMsg(true);
				
				//Down time = time using in stop-and-copy phase
				//vm.setDownTime(migrationTime);
				//migrationData.setDoneMigrating(true);//Set for stopping the sending loop in the broker
			}
			
			setPreviousDirtyPage(dirtyPages);
		}
		
		generateDirtyPage();
		setPreCopyRound(preCopyRound + 1);
		//vm.updateMigrationTime(migrationTime);
		//msg.setMigrationTime(migrationTime);
		//msg.setDirtyPage(dirtyPages);
		return msg;
	}
	
	protected void checkIfNoProgress(int currentDirtyPage){
		if(currentDirtyPage > previousDirtyPage){
			setNoProgressRound(noProgressRound + 1);
		}
		else{
			setNoProgressRound(0);
		}
	}
	
	protected boolean isPreCopyEnd(int dirtyPageNum){
		//Reached 30 iterations
		if(preCopyRound == Environment.maxPreCopyRound){
			return true;
		}
		//Dirty page less than 50 pages
		if(dirtyPageNum <= Environment.minDirtyPage){
			return true;
		}
		//Higher dirty page spawned than previous iteration's dirty page for 2 iterations straight
		if(noProgressRound >= Environment.maxNoProgressRound){
			return true;
		}
		return false;
	}
	
	/*protected double calculateMigrationTime(double sizeKb){
		double wanBw = (double) Environment.bandwidth / 8; //Make Mbps to MBps
		double wanBwKb = convertMbToKb(wanBw);
		double delay = sizeKb / wanBwKb; // scale in seconds
		return delay;
	}*/
	
	/*protected int findDirtyPage(){
		VmTest vm = migrationData.getVm();
		ArrayList<Integer> memoryPage = vm.getMemoryPage();
		//ArrayList<Integer> dirtyIndices = new ArrayList<Integer>();
		//int memPageNum = vm.getMemoryPageNum();
		int dirtyPageNum = 0;
		
		for(int i=0; i<memoryPage.size(); i++){
			Integer page = memoryPage.get(i);
			if(page.intValue() == Constant.DIRTY_PAGE){
				//dirtyIndices.add(i);
				dirtyPageNum++;
			}
		}
		return dirtyPageNum;
	}*/
	
	protected int findDirtyPage(){
		VmTest vm = migrationData.getVm();
		return vm.getDirtyPageNum();
	}
	
	protected void generateDirtyPage(){
		int diryPage = 0;
		VmTest vm = migrationData.getVm();
		//vm.resetMemoryPage();
		vm.resetDirtyPageNum();
		Random typeRandom = new Random();
		Random dirtyRandom = new Random();
		
		for(int i=0; i<vm.getMemoryPageNum(); i++){
			double pageType = typeRandom.nextDouble() * 100.0;
			double dirty = dirtyRandom.nextDouble() * 100.0; //random value between 0-100
			double dirtyCriterion = checkDirtyRate(pageType); //check dirty rate by the page type
			

			if(dirty <= dirtyCriterion){
				//vm.getMemoryPage().set(i, Constant.DIRTY_PAGE);
				diryPage++;
			}
		}
		vm.setDirtyPageNum(diryPage);
	}
	
	private double checkDirtyRate(double type){
		if(type <= Environment.wwsPageRatio){
			//This page is a Writable Working Set.
			//It has a very high dirty rate.
			return Environment.wwsDirtyRate;
		}
		else{
			//This page is a normal page
			//It has a low dirty rate
			return Environment.normalDirtyRate;
		}
	}
	
	protected double convertMbToKb(double num){
		return num * Constant.KILO_BYTE;
	}

	protected void setMigrationData(MigrationMessage migrationData) {
		this.migrationData = migrationData;
		setPreCopyRound(0);
		setNoProgressRound(0);
		setPreviousDirtyPage(Integer.MAX_VALUE);
		//setDoneMigrating(false);
	}

	/*public boolean isDoneMigrating() {
		return isDoneMigrating;
	}*/

	/*public void setDoneMigrating(boolean isDoneMigrating) {
		//this.isDoneMigrating = isDoneMigrating;
		migrationData.setDoneMigrating(isDoneMigrating);
	}*/

	protected void setPreCopyRound(int preCopyRound) {
		this.preCopyRound = preCopyRound;
	}

	protected void setNoProgressRound(int noProgressRound) {
		this.noProgressRound = noProgressRound;
	}

	protected void setPreviousDirtyPage(int previousDirtyPage) {
		this.previousDirtyPage = previousDirtyPage;
	}
	
	
}
