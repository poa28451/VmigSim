package broker_collaborator;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import org.cloudbus.cloudsim.core.SimEntity;

import closed_loop.FuzzyLogic;
import cloudsim_inherit.VmigSimVm;
import message.MigrationMessage;
import variable.Constant;
import variable.Environment;

public class Controller {
	private ArrayList<MigrationMessage> vmQueue;
	private ArrayList<ControllerWorker> workerList;
	//private final CountDownLatch doneAlarm;
	//private CyclicBarrier doneAlarm;
	private CountDownLatch threadLocker;
	private double highestMigTime;
	private FuzzyLogic fuzzy;
	private int threadNum;
	
	public Controller(SimEntity srcEnt, SimEntity destEnt, ArrayList<MigrationMessage> vmQueue){
		this.vmQueue = vmQueue;
		//doneAlarm = new CountDownLatch(1);
		//doneAlarm = new CyclicBarrier(threadNum + 1);
		threadNum = Environment.threadNum;
		threadLocker = new CountDownLatch(1);
		highestMigTime = Double.MIN_VALUE;
		prepareThread(srcEnt, destEnt);
		
		if(Environment.controlType == Constant.CLOSED_LOOP){
			fuzzy = new FuzzyLogic();
		}
	}
	
	public void startControlling(){
		ControllerWorker freeWorker;
		boolean isDone = false;
		int round = 0;
		try {
			for(MigrationMessage migration : vmQueue){
				/*while((freeWorker = findFreeThread()) == null){
					//System.out.println("go to sleep");
					doneAlarm.await();
				}*/
				freeWorker = findFreeThread();
				
				/*System.out.println("assign work to thread " + freeWorker.getThreadId());
				System.out.println("\t clock: " + freeWorker.getNextMigrationDelay());
				System.out.println("\t vm id:  " + migration.getVm().getId() + " ram: " + migration.getVm().getRam());*/
				//freeWorker = manageTerminatedThread(freeWorker);
				
				freeWorker.setData(migration);
				freeWorker.start();
				
				round++;
				if(round == threadNum){
					//Wait for all thread to be done its job.
					//doneAlarm.await();
					//synchroWorkerThreads(isDone);
					control(isDone);
					round = 0;
				}
			}
			isDone = true;
			synchroWorkerThreads(isDone);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(0);
		}
		highestMigTime = findHighestMigTime();
	}
	
	private void control(boolean isDone) throws InterruptedException{
		if(Environment.controlType == Constant.OPEN_LOOP){
			controlByOpenLoop(isDone);
		}
		else{
			controlByClosedLoop();
		}
	}
	
	private void controlByOpenLoop(boolean isDone) throws InterruptedException{
		synchroWorkerThreads(isDone);
	}
	
	private void controlByClosedLoop() throws InterruptedException{
		fuzzyCalculate();
	}
	
	/**
	 * Control with open-loop's algorithm, use constant thread number until the end of controlling.
	 * @throws InterruptedException 
	 * @throws BrokenBarrierException 
	 *//*
	private void controlByOpenLoop() throws InterruptedException{
		ControllerWorker freeWorker;
		boolean isDone = false;
		int round = 0;

			for(MigrationMessage migration : vmQueue){
				while((freeWorker = findFreeThread()) == null){
					//System.out.println("go to sleep");
					doneAlarm.await();
				}
				freeWorker = findFreeThread();
				
				System.out.println("assign work to thread " + freeWorker.getThreadId());
				System.out.println("\t clock: " + freeWorker.getNextMigrationDelay());
				System.out.println("\t vm id:  " + migration.getVm().getId() + " ram: " + migration.getVm().getRam());
				//freeWorker = manageTerminatedThread(freeWorker);
				
				freeWorker.setData(migration);
				freeWorker.start();
				
				round++;
				if(round == threadNum){
					//Wait for all thread to be done its job.
					//doneAlarm.await();
					synchroWorkerThreads(isDone);
					round = 0;
				}
			}
			isDone = true;
			synchroWorkerThreads(isDone);
		highestMigTime = findHighestMigTime();
	}
	
	*//**
	 * Control with closed-loop's algorithm, adapt the thread number periodically according to bandwidth. 
	 * The controller will adapt for the highest benefit of total migration time (maximum VM number).
	 * @throws InterruptedException 
	 * @throws BrokenBarrierException 
	 *//*
	private void controlByClosedLoop() throws InterruptedException{
		ControllerWorker freeWorker;
		boolean isDone = false;
		int round = 0;

		for(MigrationMessage migration : vmQueue){
			while((freeWorker = findFreeThread()) == null){
				//System.out.println("go to sleep");
				doneAlarm.await();
			}
			freeWorker = findFreeThread();
			
			System.out.println("assign work to thread " + freeWorker.getThreadId());
			System.out.println("\t clock: " + freeWorker.getNextMigrationDelay());
			System.out.println("\t vm id:  " + migration.getVm().getId() + " ram: " + migration.getVm().getRam());
			//freeWorker = manageTerminatedThread(freeWorker);
			
			freeWorker.setData(migration);
			freeWorker.start();
			
			round++;
			if(round == threadNum){
				//Wait for all thread to be done its job.
				//doneAlarm.await();
				fuzzyCalculate();
				round = 0;
			}
		}
		isDone = true;
		synchroWorkerThreads(isDone);
		
		highestMigTime = findHighestMigTime();
	}*/
	
	/**
	 * Find the first free worker (done its current execution) and return it.
	 * @return the free worker if it is existed, null if no free worker.
	 */
	private ControllerWorker findFreeThread(){
		//Concurrent style
		double least = Double.MAX_VALUE;
		ControllerWorker next = null;
		for(ControllerWorker worker : workerList){ 
			if(!worker.isAlive() && !worker.isTerminated() && least > worker.getNextMigrationDelay()){
				//System.out.println("found thread " + worker.getThreadId() + " with time " + worker.getNextMigrationDelay());
				least = worker.getNextMigrationDelay();
				next = worker;
			}
		}
		return next;
		
		//Sequential style
		/*double least = Double.MAX_VALUE;
		ControllerWorker next = null;
		for(ControllerWorker worker : workerList){
			if(worker.isAlive()) return null;
			if(least > worker.getNextMigrationDelay()){
				least = worker.getNextMigrationDelay();
				next = worker;
			}
		}
		return next;*/
	}
	
	private void fuzzyCalculate() throws InterruptedException{
		double totalBwMBps = 0;
		double currentTime = 0;
		double totalLeftRamMB = 0;
		
		//Find the highest clock among the threads, this time will be set to all thread after the calculation.
		for(ControllerWorker t : workerList){
			t.join();
			if(currentTime < t.getNextMigrationDelay())
				currentTime = t.getNextMigrationDelay();
		}
		
		//Find the current total bw used by threads, also the current migration time.
		for(ControllerWorker t : workerList){
			//currentTime = t.getNextMigrationDelay();
			totalBwMBps += NetworkGenerator.getBandwidthAtTimeMB(t.getThreadId(), currentTime);
		}
		
		//Find the total being-migrated RAM of VM (i.e. total RAM of VMs those are not migrated yet).
		for(MigrationMessage migration : vmQueue){
			VmigSimVm vm = migration.getVm();
			if(!vm.isMigratedOut()){
				totalLeftRamMB += vm.getRam();
			}
		}
		
		double need;
		if(totalBwMBps <= 0){
			need = 0;
		}
		else{
			need = totalLeftRamMB / totalBwMBps;
		}
		double predictedTime = currentTime + need;
		double error = Environment.migrationTimeLimit - predictedTime;
		double errorPercent = error * 100 / Environment.migrationTimeLimit;
		
		int newThreadNum = fuzzy.evaluateResult(errorPercent, threadNum);
		System.out.println("old: " + threadNum + " new: " + newThreadNum + " error%: " + errorPercent + "/" + error + " predic: " + predictedTime + " totalBW: " + totalBwMBps + " leftRAM: " + totalLeftRamMB);
		System.out.println();		
		manageThreadAdaption(newThreadNum);
	}
	
	private void manageThreadAdaption(int threadNum){
		if(this.threadNum != threadNum){
			changeTraceFile(threadNum);	
			this.threadNum = threadNum;
			Environment.setThreadNum(threadNum);
			//doneAlarm = new CyclicBarrier(threadNum + 1);
		}
		
		double highestMigTime = findHighestMigTime();
		SimEntity srcEnt = workerList.get(0).getSrcEntity();
		SimEntity destEnt = workerList.get(0).getDestEntity();
		
		//Change the number of thread, along with adjusting the migration time to the highest one.
		prepareThread(srcEnt, destEnt, highestMigTime);
	}
	
	private void changeTraceFile(int threadNum){
		String oldName = Environment.traceFile;
		String oldThread = String.valueOf(Environment.threadNum) + "t";
		String newThread = String.valueOf(threadNum) + "t";
		String newname = oldName.replaceAll(oldThread, newThread);
		
		//System.out.println(oldThread + " " + newThread + " " + newname + " " + threadNum + " " + Environment.threadNum);
		Environment.setTraceFile(newname);
		new NetworkGenerator(newname, threadNum);
	}
	
	/**
	 *  Prepare the threads for the calculation.
	 * @param isDone true if the threads are needed to refresh the status.
	 * @throws InterruptedException
	 */
	private void synchroWorkerThreads(boolean isDone) throws InterruptedException{
		for(ControllerWorker t : workerList){
			t.join();
		}
		if(!isDone){
			/*double max = 0;
			for(ControllerWorker t : workerList){
				if(max < t.getNextMigrationDelay()) max = t.getNextMigrationDelay();
			}*/
			ArrayList<ControllerWorker> newList = new ArrayList<ControllerWorker>();
			for(ControllerWorker t : workerList){
				int threadId = t.getThreadId();
				SimEntity srcEnt = t.getSrcEntity();
				SimEntity destEnt = t.getDestEntity();
				double nextMigrationDelay = t.getNextMigrationDelay();
				//nextMigrationDelay = max;
				//t = new ControllerWorker(threadId, srcEnt, destEnt, nextMigrationDelay, doneAlarm, threadLocker);
				t = new ControllerWorker(threadId, srcEnt, destEnt, nextMigrationDelay, threadLocker);
				newList.add(t);
			}
			workerList = newList;
		}
	}
	
	/**
	 * Initialize the threads, the number of threads depends on number defined from the user.
	 */
	private void prepareThread(SimEntity srcEnt, SimEntity destEnt){
		workerList = new ArrayList<>();
		threadLocker.countDown();
		for(int i=0; i<threadNum; i++){
			//ControllerWorker worker = new ControllerWorker(i, srcEnt, destEnt, 0, doneAlarm, threadLocker);
			ControllerWorker worker = new ControllerWorker(i, srcEnt, destEnt, 0, threadLocker);
			workerList.add(worker);
		}
	}
	
	private void prepareThread(SimEntity srcEnt, SimEntity destEnt, double migrationTime){
		workerList = new ArrayList<>();
		for(int i=0; i<threadNum; i++){
			//ControllerWorker worker = new ControllerWorker(i, srcEnt, destEnt, migrationTime, doneAlarm, threadLocker);
			ControllerWorker worker = new ControllerWorker(i, srcEnt, destEnt, migrationTime, threadLocker);
			workerList.add(worker);
		}
	}
	
	/*private ControllerWorker manageTerminatedThread(ControllerWorker freeWorker){
		if(freeWorker.isTerminated()){
			//System.out.println("create new thread " + threadId);
			int threadId = freeWorker.getThreadId();
			SimEntity srcEnt = freeWorker.getSrcEntity();
			SimEntity destEnt = freeWorker.getDestEntity();
			double nextMigrationDelay = freeWorker.getNextMigrationDelay();
			
			workerList.remove(freeWorker);
			freeWorker = new ControllerWorker(threadId, srcEnt, destEnt, nextMigrationDelay, doneAlarm, threadLocker);
			workerList.add(freeWorker);
		}
		return freeWorker;
	}*/
	
	private double findHighestMigTime(){
		double highest = Double.MIN_VALUE;
		for(ControllerWorker t : workerList){
			if(t.getNextMigrationDelay() > highest){
				highest = t.getNextMigrationDelay();
			}
		}
		return highest;
	}
	
	public double getHighestMigrationTime(){
		return highestMigTime;
	}
}
