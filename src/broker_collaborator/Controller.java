package broker_collaborator;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import org.cloudbus.cloudsim.core.SimEntity;

import message.MigrationMessage;
import variable.Environment;

public class Controller {
	private ArrayList<MigrationMessage> vmQueue;
	private ArrayList<ControllerWorker> workerList;
	private final CountDownLatch doneAlarm;
	private double highestMigTime;
	
	public Controller(SimEntity srcEnt, SimEntity destEnt, ArrayList<MigrationMessage> vmQueue){
		this.vmQueue = vmQueue;
		doneAlarm = new CountDownLatch(1);
		workerList = new ArrayList<>();
		highestMigTime = Double.MIN_VALUE;
		prepareThread(srcEnt, destEnt);
	}
	
	public void startControlling(){
		ControllerWorker freeWorker;
		try {
			for(MigrationMessage migration : vmQueue){
				while((freeWorker = findFreeThread()) == null){
					//System.out.println("go to sleep");
					doneAlarm.await();
				}
				
				//System.out.println("assign work to thread " + threadId);
				freeWorker = manageTerminatedThread(freeWorker);
				
				freeWorker.setData(migration);
				freeWorker.start();
			}
			synchroWorkerThreads();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		findHighestMigTime();
	}
	
	/**
	 * Find the first free worker (done its current execution) and return it.
	 * @return the free worker if it is existed, null if no free worker.
	 */
	private ControllerWorker findFreeThread(){
		/*for(ThreadWorker worker : workerList){
			if(!worker.isAlive()){
				//return worker;
			}
		}
		return null;*/
		
		/*double least = Double.MAX_VALUE;
		ThreadWorker next = null;
		for(ThreadWorker worker : workerList){
			if(!worker.isAlive() && least > worker.nextMigrationDelay){
				least = worker.nextMigrationDelay;
				next = worker;
			}
		}
		return next;*/
		
		for(ControllerWorker worker : workerList){
			if(worker.isAlive()) return null;
		}
		return workerList.get(0);
	}
	
	private ControllerWorker manageTerminatedThread(ControllerWorker freeWorker){
		if(freeWorker.isTerminated()){
			//System.out.println("create new thread " + threadId);
			int threadId = freeWorker.getThreadId();
			SimEntity srcEnt = freeWorker.getSrcEntity();
			SimEntity destEnt = freeWorker.getDestEntity();
			double nextMigrationDelay = freeWorker.getNextMigrationDelay();
			
			workerList.remove(freeWorker);
			freeWorker = new ControllerWorker(threadId, srcEnt, destEnt, doneAlarm, nextMigrationDelay);
			workerList.add(freeWorker);
		}
		return freeWorker;
	}
	
	private double findHighestMigTime(){
		for(ControllerWorker t : workerList){
			if(t.getNextMigrationDelay() > highestMigTime){
				highestMigTime = t.getNextMigrationDelay();
			}
		}
		return highestMigTime;
	}
	
	/**
	 * Wait for all worker thread to be done the execution.
	 * @throws InterruptedException
	 */
	private void synchroWorkerThreads() throws InterruptedException{
		for(ControllerWorker t : workerList){
			t.join();
		}
	}
	
	/**
	 * Initialize the threads, the number of threads depends on number defined from the user.
	 */
	private void prepareThread(SimEntity srcEnt, SimEntity destEnt){
		for(int i=0; i<Environment.threadNum; i++){
			workerList.add(new ControllerWorker(i, srcEnt, destEnt, doneAlarm, 0));
		}
	}
	
	public double getHighestMigrationTime(){
		return highestMigTime;
	}
}
