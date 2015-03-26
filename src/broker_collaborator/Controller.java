package broker_collaborator;

import org.cloudbus.cloudsim.core.CloudSim;

import cloudsim_inherit.VmigSimVm;
import variable.Constant;
import variable.Environment;
import message.MigrationMessage;

public class Controller {
	
	public double calculateMigrationTime(MigrationMessage msg, double nextMigrationDelay){
		double migrationTime = 0;
		double dataKb = msg.getDataSizeKB();
		double currentMigrationClock = nextMigrationDelay + CloudSim.clock();
		
		switch (Environment.controlType) {
			case Constant.OPEN_LOOP:
				migrationTime = calculateByOpenLoop(dataKb, currentMigrationClock);
				break;
			
			case Constant.CLOSE_LOOP:
				//Close-loop system is still under studying
				calculateByCloseLoop();
				
			default:
				migrationTime = calculateByOpenLoop(dataKb, currentMigrationClock);
				break;
		}
		updateMigrationTime(msg, migrationTime);
		return migrationTime;
	}
	
	public double calculateByOpenLoop(double dataKB, double startClock) {
		double totalSentKB = 0;
		double totalUsedTime = 0;
		double bwAtTimeKB = 0;
		double currentClock = startClock;
		
		while(totalSentKB < dataKB){
			//Check if the time exceeded the limit already
			if(currentClock >= Environment.migrationTimeLimit){
				return Double.MIN_VALUE;
			}
			
			bwAtTimeKB = NetworkGenerator.getBandwidthAtTimeKB(currentClock);
			/*double bwAtTimeMb = NetworkGenerator.getBandwidthAtTimeKB(currentClock);
			bwAtTimeKB = convertMbToKB(bwAtTimeMb);*/
			
			//Find the left time between that b/w interval
			//Ex. if current time is 0.9 , then it has 0.1 second (1.1-0.9 = 0.2)
			//	before the changing of b/w interval (to b/w of the second 1.1 - 2) 
			//10 -> 11
			double intervalFraction = NetworkGenerator.calculateIntervalFraction(currentClock);
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
				//System.out.println("Ended");
			}
			else{
				roundSend = bwAtTimeKB * intervalFraction;
				usedTime = intervalFraction;
			}
			totalSentKB += roundSend;
			totalUsedTime += usedTime;
			currentClock += usedTime;
			/*System.out.println("\tSend " + roundSend + "/" + totalSentKB);
			System.out.println("\tTime " + usedTime + "/" + totalUsedTime);
			System.out.println("\tClock " + currentClock+ "/" + (currentClock - usedTime));
			System.out.println("\tBW " + bwAtTimeKB + "/" + (bwAtTimeKB / Constant.KILO_BYTE * 8));*/
		}
		return totalUsedTime;
	}
	
	/*public static double calculateDowntime(double dataKB, double startClock) {
		double totalSentKB = 0;
		double totalUsedTime = 0;
		double bwAtTimeKB = 0;
		double currentClock = startClock;
		
		while(totalSentKB < dataKB){
			//Check if the time exceeded the limit already
			if(currentClock >= Environment.migrationTimeLimit){
				return Double.MIN_VALUE;
			}
			
			bwAtTimeKB = NetworkGenerator.getBandwidthAtTimeKB(currentClock);
			//double bwAtTimeMb = NetworkGenerator.getBandwidthAtTimeKB(currentClock);
			//bwAtTimeKB = convertMbToKB(bwAtTimeMb);
			
			//Find the left time between that b/w interval
			//Ex. if current time is 0.9 , then it has 0.1 second (1.1-0.9 = 0.2)
			//	before the changing of b/w interval (to b/w of the second 1.1 - 2) 
			//10 -> 11
			double intervalFraction = NetworkGenerator.calculateIntervalFraction(currentClock);
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
				//System.out.println("Ended");
			}
			else{
				roundSend = bwAtTimeKB * intervalFraction;
				usedTime = intervalFraction;
			}
			totalSentKB += roundSend;
			totalUsedTime += usedTime;
			currentClock += usedTime;
			System.out.println("\tSend " + roundSend + "/" + totalSentKB);
			System.out.println("\tTime " + usedTime + "/" + totalUsedTime);
			System.out.println("\tClock " + currentClock+ "/" + (currentClock - usedTime));
			System.out.println("\tBW " + bwAtTimeKB + "/" + (bwAtTimeKB / Constant.KILO_BYTE * 8));
		}
		return totalUsedTime;
	}*/
	
	private double calculateByCloseLoop(){
		//Under studying
		double migrationTime = 0;
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
			vm.setDownTime(transferTime);
		}
	}
	
	/*private static double convertMbToKB(double number){
		return number * Constant.KILO_BYTE / 8;
	}*/
}
