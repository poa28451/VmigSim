package cloudsim_inherit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import message.MigrationMessage;

import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.lists.VmList;

import broker_collaborator.Scheduler;
import broker_collaborator.Controller;
import container.MigrationResults;
import variable.Constant;
import variable.Environment;

public class VmigSimBroker extends DatacenterBroker {
	private HashMap<String, Integer> migrationMap;
	private final String SOURCE = "src";
	private final String DESTINATION = "dest";

	private ArrayList<VmigSimVm> results;
	private Scheduler scheduler;
	/*private MigrationManager migManager;
	private MigrationCalculator timeCalculator;
	private double nextMigrationDelay = 0;*/
	
	//private Datacenter destDC;
	//boolean threadFirstRound = true;
	private Controller controller;
	
	public VmigSimBroker(String name) throws Exception {
		super(name);
		setResults(new ArrayList<VmigSimVm>());
		setScheduler(new Scheduler());
		/*setMigManager(new MigrationManager());
		setController(new MigrationCalculator());*/
	}

	@Override
	public void processEvent(SimEvent ev) {
		switch (ev.getTag()){
			case Constant.QUEUE_VM_MIGRATE:
				processQueueVmMigrate(ev);
				break;
			case Constant.REPORT_VM_MIGRATE:
				processReportVmMigrate(ev);
				break;
			default: 
				super.processEvent(ev);
				break;
		}
	}
	
	/**
	 * Use for queuing the VMs sent from the source DC. These VMs will be send to the destination by the
	 * algorithm of the controller
	 * @param ev The event sent from the source DC
	 */
	protected void processQueueVmMigrate(SimEvent ev){	
		MigrationMessage message = (MigrationMessage) ev.getData();
		//VmigSimVm migratedVm = message.getVm();
		double startClock = message.getSendClock();
		message.setReceiveClock(CloudSim.clock());

		scheduler.addMsgToWaitingList(message);
		
		/*System.out.println();
		System.out.println(CloudSim.clock() + " Broker: Received queue message from dc id" + ev.getSource() + " sent at " + startClock);
		System.out.println("VM id: " + migratedVm.getId() + " added to a waiting queue");
		System.out.println("Amount of VM in a waiting queue: " + scheduler.getMsgWaitingList().size());*/

		Datacenter destDC = (Datacenter) CloudSim.getEntity(3);
		
		//If every VM listed already, begin the migration
		if(isAllVmInWaitingList()){
			System.out.println();
			System.out.println(CloudSim.clock() + " Broker: Received all queue message from Source DC at " + startClock);
			System.out.println("Amount of VM in a waiting queue: " + scheduler.getMsgWaitingList().size());
			
			/*for(MigrationMessage migration : scheduler.scheduleMigration()){
				sendVmMigration(migration);
			}*/

			controller = new Controller(this, destDC, scheduler.scheduleMigration());
			controller.startControlling();
		}
	}
	
	/*private class ThreadMain{
		private ArrayList<ThreadWorker> workerList;
		private final CountDownLatch doneAlarm;
		public double highestMigTime;
		
		public ThreadMain(String name){
			//super(name);
			doneAlarm = new CountDownLatch(1);
			workerList = new ArrayList<>();
			highestMigTime = Double.MIN_VALUE;
			prepareThread();
		}
		
		public void run(){
			ThreadWorker freeWorker;
			try {
				for(MigrationMessage migration : scheduler.scheduleMigration()){
					while((freeWorker = findFreeThread()) == null){
						//System.out.println("go to sleep");
						doneAlarm.await();
					}
					
					int threadId = freeWorker.getThreadId();
					//System.out.println("assign work to thread " + threadId);
					if(freeWorker.isTerminated()){
						//System.out.println("create new thread " + threadId);
						workerList.remove(freeWorker);
						freeWorker = new ThreadWorker(threadId, doneAlarm, freeWorker.getNextMigrationDelay());
						workerList.add(freeWorker);
					}
					freeWorker.setData(migration);
					freeWorker.start();
				}
				synchroWorkerThreads();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			findHighestMigTime();
		}
		
		*//**
		 * Find the first free worker (done its current execution) and return it.
		 * @return the free worker if it is existed, null if no free worker.
		 *//*
		private ThreadWorker findFreeThread(){
			for(ThreadWorker worker : workerList){
				if(!worker.isAlive()){
					//return worker;
				}
			}
			return null;
			
			double least = Double.MAX_VALUE;
			ThreadWorker next = null;
			for(ThreadWorker worker : workerList){
				if(!worker.isAlive() && least > worker.nextMigrationDelay){
					least = worker.nextMigrationDelay;
					next = worker;
				}
			}
			return next;
			
			for(ThreadWorker worker : workerList){
				if(worker.isAlive()) return null;
			}
			return workerList.get(0);
		}
		
		private double findHighestMigTime(){
			for(ThreadWorker t : workerList){
				if(t.nextMigrationDelay > highestMigTime){
					highestMigTime = t.nextMigrationDelay;
				}
			}
			return highestMigTime;
		}
		
		*//**
		 * Wait for all worker thread to be done the execution.
		 * @throws InterruptedException
		 *//*
		private void synchroWorkerThreads() throws InterruptedException{
			for(ThreadWorker t : workerList){
				t.join();
			}
		}
		
		*//**
		 * Initialize the threads, the number of threads depends on number defined from the user.
		 *//*
		private void prepareThread(){
			for(int i=0; i<Environment.threadNum; i++){
				workerList.add(new ThreadWorker(i, doneAlarm, 0));
			}
		}
	}
	
	private class ThreadWorker extends Thread{
		private MigrationMessage data;
		private final CountDownLatch doneAlarm;
		private boolean isTerminated;
		private double nextMigrationDelay = 0;
		private int threadId;
		
		public ThreadWorker(int threadId, CountDownLatch doneAlarm, double nextMigrationDelay){
			super(String.valueOf(threadId));
			this.threadId = threadId;
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
				if(!threadFirstRound) latch.countDown();
				else threadFirstRound = false;
				
				msg = migManager.manageMigration();
				msg.setSendClock(nextMig);
				double migrationTime = controller.calculateMigrationTime(msg, nextMig);
				//Stop sending if the time exceeded the limit
				if(migrationTime == Double.MIN_VALUE){
					break;
				}
				
				if(maxThread > 1){
					try {
						latch.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				nextMigrationDelay += migrationTime;
				//synchronized (this) {
					send(destDC.getId(), 
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
	}*/
	
	protected boolean isAllVmInWaitingList(){
		return scheduler.getMsgWaitingList().size() == getVmList().size();		
	}
	
	/*protected void sendVmMigration(MigrationMessage data){
		Datacenter destination = (Datacenter) CloudSim.getEntity(migrationMap.get(DESTINATION));
		migManager.setMigrationData(data);
		MigrationMessage msg;
		do{
			msg = migManager.manageMigration();
			msg.setSendClock(nextMigrationDelay);
			double migrationTime = timeCalculator.calculateMigrationTime(msg, nextMigrationDelay);
			
			//Stop sending if the time exceeded the limit
			if(migrationTime == Double.MIN_VALUE){
				break;
			}
			
			nextMigrationDelay += migrationTime;
			send(destination.getId(), 
					nextMigrationDelay, 
					Constant.SEND_VM_MIGRATE, 
					msg);
			
		} while(!msg.isLastMigrationMsg());
	}*/
	
	/**
	 * Use for collecting the VMs' migration result sent from the destination
	 * @param ev The event sent from the destination
	 */
	protected void processReportVmMigrate(SimEvent ev){
		MigrationMessage report = (MigrationMessage) ev.getData();
		if(report.getVm().isDoneMigration()){
			results.add(report.getVm());
		}
	}
	
	protected void sendStartMigrationSignal(){		
		setMigrationMap(new HashMap<String, Integer>());
		//Send a start signal to source datacenter
		sendNow(migrationMap.get(SOURCE), Constant.START_VM_MIGRATE, null);
	}
	
	public void saveMigrationResult(){
		MigrationResults summary = new MigrationResults(scheduler.getVmWaitingList(), results);
		summary.setRealMigTime(controller.getHighestMigrationTime());
		Environment.setMigrationResult(summary);
	}

	@Override
	public void shutdownEntity() {
		saveMigrationResult();
		System.out.println(getName() + " is shutting down...");
	}
	
	@Override
	public void submitVmList(List<? extends Vm> list) {
		super.submitVmList(list);
		Collections.shuffle(getVmList());
	}

	/**
	 * Override to insert the starting-custom migration code.
	 */
	@Override
	protected void processVmCreate(SimEvent ev) {
		int[] data = (int[]) ev.getData();
		int datacenterId = data[0];
		int vmId = data[1];
		int result = data[2];

		if (result == CloudSimTags.TRUE) {
			getVmsToDatacentersMap().put(vmId, datacenterId);
			getVmsCreatedList().add(VmList.getById(getVmList(), vmId));
			Log.printLine(CloudSim.clock() + ": " + getName() + ": VM #" + vmId
					+ " has been created in Source DC, Host #"
					+ VmList.getById(getVmsCreatedList(), vmId).getHost().getId());
		} else {
			Log.printLine(CloudSim.clock() + ": " + getName() + ": Creation of VM #" + vmId
					+ " failed in Datacenter #" + datacenterId);
		}
		incrementVmsAcks();

		// all the requested VMs have been created
		if (getVmsCreatedList().size() == getVmList().size() - getVmsDestroyed()) {
			/**
			 * Start the migration simulation here.
			 */
			sendStartMigrationSignal();
		} else {
			// all the acks received, but some VMs were not created
			if (getVmsRequested() == getVmsAcks()) {
				// find id of the next datacenter that has not been tried
				for (int nextDatacenterId : getDatacenterIdsList()) {
					if (!getDatacenterRequestedIdsList().contains(nextDatacenterId)) {
						createVmsInDatacenter(nextDatacenterId);
						return;
					}
				}

				// all datacenters already queried
				if (getVmsCreatedList().size() > 0) { // if some vm were created
					
					submitCloudlets();
				} else { // no vms created. abort
					Log.printLine(CloudSim.clock() + ": " + getName()
							+ ": none of the required VMs could be created. Aborting");
					finishExecution();
				}
			}
		}
	}
	
	/**
	 * Override to remove some unnecessary log
	 */
	protected void createVmsInDatacenter(int datacenterId) {
		// send as much vms as possible for this datacenter before trying the next one
		int requestedVms = 0;
		for (Vm vm : getVmList()) {
			if (!getVmsToDatacentersMap().containsKey(vm.getId())) {
				sendNow(datacenterId, CloudSimTags.VM_CREATE_ACK, vm);
				requestedVms++;
			}
		}

		getDatacenterRequestedIdsList().add(datacenterId);

		setVmsRequested(requestedVms);
		setVmsAcks(0);
	}

	public void setMigrationMap(HashMap<String, Integer> migrationMap) {
		migrationMap.put(SOURCE, getDatacenterIdsList().get(0));
		migrationMap.put(DESTINATION, getDatacenterIdsList().get(1));
		this.migrationMap = migrationMap;
	}

	public void setResults(ArrayList<VmigSimVm> results) {
		this.results = results;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	/*protected void setMigManager(MigrationManager migManager) {
		this.migManager = migManager;
	}

	protected void setController(MigrationCalculator controller) {
		this.timeCalculator = controller;
	}*/
}
