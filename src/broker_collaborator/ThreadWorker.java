package broker_collaborator;

import java.util.concurrent.CountDownLatch;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEntity;

import message.MigrationMessage;
import variable.Constant;

public class ThreadWorker extends Thread{
	private SimEntity srcEnt, destEnt;
	private MigrationMessage data;
	private final CountDownLatch doneAlarm;
	private boolean isTerminated;
	private double nextMigrationDelay = 0;
	private int threadId;
	
	public ThreadWorker(int threadId, SimEntity srcEnt, SimEntity destEnt, CountDownLatch doneAlarm, double nextMigrationDelay){
		super(String.valueOf(threadId));
		this.threadId = threadId;
		this.srcEnt = srcEnt;
		this.destEnt = destEnt;
		this.doneAlarm = doneAlarm;
		this.nextMigrationDelay = nextMigrationDelay;
		isTerminated = false;
	}
	
	public void setData(MigrationMessage migration){
		data = migration;
	}
	
	public void run(){
		MigrationManager migManager = new MigrationManager();
		Controller controller = new Controller(threadId);
		
		migManager.setMigrationData(data);
		MigrationMessage msg;
		do{
			double nextMig = nextMigrationDelay;
			/*if(!threadFirstRound) latch.countDown();
			else threadFirstRound = false;*/
			
			msg = migManager.manageMigration();
			msg.setSendClock(nextMig);
			double migrationTime = controller.calculateMigrationTime(msg, nextMig);
			//Stop sending if the time exceeded the limit
			if(migrationTime == Double.MIN_VALUE){
				break;
			}
			
			/*if(maxThread > 1){
				try {
					latch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}*/
			
			nextMigrationDelay += migrationTime;
			//synchronized (this) {
				CloudSim.send(srcEnt.getId(), destEnt.getId(), 
						nextMig + migrationTime, 
						Constant.SEND_VM_MIGRATE, 
						msg);
			//}
		} while(!msg.isLastMigrationMsg());
		doneAlarm.countDown();
		isTerminated = true;
	}
	
	public int getThreadId(){
		return threadId;
	}
	
	public boolean isTerminated(){
		return isTerminated;
	}
	
	public double getNextMigrationDelay(){
		return nextMigrationDelay;
	}
	
	public SimEntity getSrcEntity(){
		return srcEnt;
	}
	
	public SimEntity getDestEntity(){
		return destEnt;
	}
}
