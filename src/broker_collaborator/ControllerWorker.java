package broker_collaborator;

import java.util.concurrent.CountDownLatch;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEntity;

import message.MigrationMessage;
import variable.Constant;

public class ControllerWorker extends Thread{
	private SimEntity srcEnt, destEnt;
	private MigrationMessage data;
	//private final CountDownLatch doneAlarm;
	//private CyclicBarrier doneAlarm;
	private final CountDownLatch lock;
	private boolean isTerminated;
	private double nextMigrationDelay = 0;
	private int threadId;
	
	public ControllerWorker(int threadId, SimEntity srcEnt, SimEntity destEnt, double nextMigrationDelay, CountDownLatch lock){
		super(String.valueOf(threadId));
		this.threadId = threadId;
		this.srcEnt = srcEnt;
		this.destEnt = destEnt;
		this.nextMigrationDelay = nextMigrationDelay;
		//this.doneAlarm = doneAlarm;
		this.lock = lock;
		isTerminated = false;
	}
	
	public void setData(MigrationMessage migration){
		data = migration;
		migration.getVm().setMigratedOut(true);
	}
	
	public void run() {
		MigrationManager migManager = new MigrationManager();
		MigrationCalculator calculator = new MigrationCalculator(threadId);
		
		migManager.setMigrationData(data);
		MigrationMessage msg;
		do{
			double nextMig = nextMigrationDelay;
			double currentClock = CloudSim.clock();
			
			msg = migManager.manageMigration(currentClock);
			msg.setSendClock(nextMig);
			double migrationTime = calculator.calculateMigrationTime(msg, nextMig, currentClock);
			
			//Stop sending if the time exceeded the limit
			if(migrationTime == Double.MIN_VALUE){
				break;
			}
			
			nextMigrationDelay += migrationTime;
			try {
				//To prevent the event list from the race conditions occurred by threads
				synchronized (lock) {
					lock.await();
					CloudSim.send(srcEnt.getId(), destEnt.getId(), 
							nextMig + migrationTime, 
							Constant.SEND_VM_MIGRATE, 
							msg);
					lock.countDown();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(1);
			}
		} while(!msg.isLastMigrationMsg());
		isTerminated = true;
		
		//Notify the Controller that the work's done.
		//doneAlarm.countDown();
		//blockAtBarrier();
	}
	
	/*private void blockAtBarrier(){
		try {
			doneAlarm.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
	}*/
	
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
	
	public CountDownLatch getLock(){
		return lock;
	}
	
	/*public void setDoneAlarm(CyclicBarrier doneAlarm){
		this.doneAlarm = doneAlarm;
	}*/
}
