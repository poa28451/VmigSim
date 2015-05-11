package broker_collaborator;

import cloudsim_inherit.VmigSimVm;
import variable.Environment;
import message.MigrationMessage;

public class MigrationCalculator {
	private int threadId;
	
	public MigrationCalculator(){
		threadId = 0;
	}
	
	public MigrationCalculator(int threadId){
		this.threadId = threadId;
	}
	
	public double calculateMigrationTime(MigrationMessage msg, double nextMigrationDelay, double clock){
		double migrationTime = 0;
		double dataKB = msg.getDataSizeKB();
		double currentMigrationClock = nextMigrationDelay + clock;
		double totalSentKB = 0;
		double bwAtTimeKB = 0;
		
		while(totalSentKB < dataKB){
			//Check if the time exceeded the limit already
			if(currentMigrationClock >= Environment.migrationTimeLimit){
				return Double.MIN_VALUE;
			}			
			bwAtTimeKB = NetworkGenerator.getBandwidthAtTimeKB(threadId, currentMigrationClock);
			
			//Find the left time between that b/w interval
			//Ex. if current time is 0.9 , then it has 0.1 second (1.1-0.9 = 0.2)
			//	before the changing of b/w interval (to b/w of the second 1.1 - 2) 
			//10 -> 11
			double intervalFraction = NetworkGenerator.calculateIntervalFraction(currentMigrationClock);
			if(intervalFraction == Double.MIN_VALUE){
				//If this case occurred, that means there's a double decimal issue.
				//Just end the migration of this data
				return Double.MIN_VALUE;
			}
			
			double roundSend, usedTime;
			if(isDoneWithinInterval(dataKB, totalSentKB, bwAtTimeKB, intervalFraction)){
				double leftoverSize = dataKB - totalSentKB;
				roundSend = leftoverSize;
				usedTime = leftoverSize / bwAtTimeKB;
			}
			else{
				roundSend = bwAtTimeKB * intervalFraction;
				usedTime = intervalFraction;
			}
			totalSentKB += roundSend;
			migrationTime += usedTime;
			currentMigrationClock += usedTime;
		}		
		updateMigrationTime(msg, migrationTime);
		return migrationTime;
	}
	
	
	private static boolean isDoneWithinInterval(double totalSize, double sentSize, double bw, double intervalFraction){
		double ableToSend = bw * intervalFraction;
		if(sentSize + ableToSend >= totalSize){
			//Able to send all of leftover data in this interval
			return true;
		}
		return false;
	}
	
	protected void updateMigrationTime(MigrationMessage msg, double transferTime){
		VmigSimVm vm = msg.getVm();
		
		msg.setMigrationTime(transferTime);
		vm.updateMigrationTime(transferTime);
		
		if(msg.isLastMigrationMsg()){
			//Downtime = last transferring time
			vm.setDowntime(transferTime);
		}
	}

	public int getThreadId() {
		return threadId;
	}
}
