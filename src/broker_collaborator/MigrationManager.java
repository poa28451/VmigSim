package broker_collaborator;

import java.util.Random;

import org.cloudbus.cloudsim.core.CloudSim;

import cloudsim_inherit.VmigSimVm;
import variable.Constant;
import variable.Environment;
import message.MigrationMessage;
import message.PreCopyMessage;;

public class MigrationManager {
	private MigrationMessage migrationData;
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
		VmigSimVm vm = migrationData.getVm();
		int vmRam = vm.getRam();
		double vmRamKB = convertMbToKb(vmRam);
		MigrationMessage msg = new MigrationMessage(vm);
		
		msg.setDataSizeKB(vmRamKB);
		msg.setLastMigrationMsg(true);
		return msg;
	}
	
	protected MigrationMessage migrateByPreCopy(){
		VmigSimVm vm = migrationData.getVm();
		int dirtyPages = Integer.MIN_VALUE;
		PreCopyMessage msg = new PreCopyMessage(vm, CloudSim.clock());
		
		if(preCopyRound == 0){//first round, send every memory page
			int vmRam = vm.getRam();
			double vmRamKB = convertMbToKb(vmRam);
			msg.setDataSizeKB(vmRamKB);
			msg.setDirtyPageAmount(Integer.MIN_VALUE);
		}
		else{
			dirtyPages = findDirtyPage();
			checkIfNoProgress(dirtyPages);
			msg.setDirtyPageAmount(dirtyPages);
			
			if(isPreCopyEnd(dirtyPages)){//If ended already, send final data
				double dirtySizeKB = dirtyPages * Environment.pageSizeKB;
				double registerSizeKB = (double) Constant.REGISTER_SIZE_BYTE / Constant.KILO_BYTE;
				msg.setDataSizeKB(dirtySizeKB + registerSizeKB);
				//msg.setDataSizeKB(dirtySizeKB);
				msg.setLastMigrationMsg(true);
			}
			else{
				msg.setDataSizeKB(dirtyPages * Environment.pageSizeKB);
			}
			setPreviousDirtyPage(dirtyPages);
		}
		
		generateDirtyPage();
		setPreCopyRound(preCopyRound + 1);
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
		//Reached specified iterations
		if(preCopyRound == Environment.maxPreCopyRound){
			return true;
		}
		//Dirty page less than specified pages
		if(dirtyPageNum <= Environment.minDirtyPage){
			return true;
		}
			
		//Higher dirty page spawned than previous iteration's dirty page for specified iterations straight
		if(noProgressRound >= Environment.maxNoProgressRound){
			return true;
		}

		/*double dirtySizeKB = dirtyPageNum * Environment.pageSizeKB;
		double maxDowntimeSec = Environment.maxDowntimeMs / 1000;
		
		double bwAtTimeKB = NetworkGenerator.getBandwidthAtTimeKB(CloudSim.clock());
		double expectedDowntime = dirtySizeKB / bwAtTimeKB;
		if(expectedDowntime <= maxDowntimeSec){
			return true;
		}*/
		
		return false;
	}
	
	protected int findDirtyPage(){
		VmigSimVm vm = migrationData.getVm();
		return vm.getDirtyPageNum();
	}
	
	protected void generateDirtyPage(){
		int diryPage = 0;
		VmigSimVm vm = migrationData.getVm();
		vm.resetDirtyPageNum();
		Random typeRandom = new Random();
		Random dirtyRandom = new Random();
		
		for(int i=0; i<vm.getMemoryPageNum(); i++){
			double pageType = typeRandom.nextDouble() * 100.0;
			double dirty = dirtyRandom.nextDouble() * 100.0; //random value between 0-100
			double dirtyCriterion = checkDirtyRate(pageType); //check dirty rate by the page type
			

			if(dirty <= dirtyCriterion){
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

	public void setMigrationData(MigrationMessage migrationData) {
		this.migrationData = migrationData;
		setPreCopyRound(0);
		setNoProgressRound(0);
		setPreviousDirtyPage(Integer.MAX_VALUE);
	}

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
