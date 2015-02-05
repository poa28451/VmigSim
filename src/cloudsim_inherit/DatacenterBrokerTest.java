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

import broker_collaborator.Controller;
import broker_collaborator.MigrationManager;
import broker_collaborator.Scheduler;
import container.MigrationResults;
import variable.Constant;
import variable.Environment;

public class DatacenterBrokerTest extends DatacenterBroker {
	//private ArrayList<HashMap<String, Object>> vmMigrationWaitingList;
	//private ArrayList<VmTest> vmMigrationSentList;
	private ArrayList<VmTest> results;//, violated;
	private Scheduler scheduler;
	private MigrationManager migManager;
	private Controller controller;
	private HashMap<String, Integer> migrationMap;
	private final String SOURCE = "src";
	private final String DESTINATION = "dest";
	private double nextMigrationDelay = 0;
	
	public DatacenterBrokerTest(String name) throws Exception {
		super(name);
		//setVmMigrationWaitingList(new ArrayList<HashMap<String, Object>>());
		//setVmMigrationSentList(new ArrayList<VmTest>());
		setResults(new ArrayList<VmTest>());
		//setViolated(new ArrayList<VmTest>());
		setScheduler(new Scheduler());
		setMigManager(new MigrationManager());
		setController(new Controller());
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
		/*HashMap<String, Object> hash = (HashMap<String, Object>)ev.getData();		
		Vm migratedVm = (Vm) hash.get("vm");
		double startClock = (double) hash.get("clock");*/
		
		MigrationMessage message = (MigrationMessage) ev.getData();
		VmTest migratedVm = message.getVm();
		double startClock = message.getSendClock();
		message.setReceiveClock(CloudSim.clock());

		
		//vmMigrationWaitingList.add(hash);
		//scheduler.addVmToWaitingList(hash);
		scheduler.addMsgToWaitingList(message);
		
		System.out.println();
		System.out.println(CloudSim.clock() + " Broker: Received queue message from dc id" + ev.getSource() + " sent at " + startClock);
		System.out.println("VM id: " + migratedVm.getId() + " added to a waiting queue");
		System.out.println("Amount in a waiting queue: " + scheduler.getMsgWaitingList().size());
		
		//If every VM listed already, begin the migration
		if(isAllVmInWaitingList()){
			for(MigrationMessage migration : scheduler.scheduleMigration()){
				sendVmMigration(migration);
			}
		}
	}
	
	protected boolean isAllVmInWaitingList(){
		return scheduler.getMsgWaitingList().size() == getVmList().size();		
	}
	
	protected void sendVmMigration(MigrationMessage data){
		Datacenter destination = (Datacenter) CloudSim.getEntity(migrationMap.get(DESTINATION));
		//Vm vm = (Vm) data.get("vm");
		/*VmTest vm = data.getVm();
		int vmRam = vm.getRam();
		double wanBw = (double) Environment.bandwidth / 8; //Make Mbps to MBps
		double delay = vmRam / wanBw; // scale in seconds
*/		
		//Check if the total migration time has exceeded the time limit
		//if(checkIfExceedTimeLimit(totalMigrationTime, delay)){
		//vmMigrationSentList.add(data.getVm());
		migManager.setMigrationData(data);
		MigrationMessage msg;
		do{
			msg = migManager.manageMigration();
			msg.setSendClock(nextMigrationDelay);
			double migrationTime = controller.calculateMigrationTime(msg, nextMigrationDelay);
			
			//Stop sending if the time exceeded the limit
			if(migrationTime == Double.MIN_VALUE){
				break;
			}
			
			nextMigrationDelay += migrationTime;
			send(destination.getId(), 
					nextMigrationDelay, 
					Constant.SEND_VM_MIGRATE, 
					msg);
					//vm);
			
		} while(!msg.isLastMigrationMsg());
			
		//}
	}
	
	/*protected boolean checkIfExceedTimeLimit(double currentMigrationTime, double vmMigrationTime){
		return currentMigrationTime + vmMigrationTime <= Environment.migrationTimeLimit;
	}*/
	
	/**
	 * Use for collecting the VMs' migration result sent from the destination
	 * @param ev The event sent from the destination
	 */
	protected void processReportVmMigrate(SimEvent ev){
		MigrationMessage report = (MigrationMessage) ev.getData();
		results.add(report.getVm());
	}
	
	protected void sendStartMigrationSignal(){		
		setMigrationMap(new HashMap<String, Integer>());
		//Send a start signal to source datacenter
		sendNow(migrationMap.get(SOURCE), Constant.START_VM_MIGRATE, null);
	}
	
	public void saveMigrationResult(){
		MigrationResults summary = new MigrationResults(scheduler.getVmWaitingList(), results);
		Environment.setMigrationResult(summary);
		/*double totalMigTime = 0, totalDownTime = 0;
		int totalPri1 = 0, totalPri2 = 0, totalPri3 = 0;
		
		System.out.println();
		for(VmTest migratedVm : results){
			double migrationTime = migratedVm.getMigrationTime();
			int ram = migratedVm.getRam();
			boolean violate = migratedVm.isViolated();
			boolean result = migratedVm.isMigrated();
			int priority = migratedVm.getPriority();
			
			System.out.println("VM: " + migratedVm.getId() + " finished the migration with the time " + migrationTime + " seconds");
			System.out.println("\tRAM = " + ram + " MB");
			System.out.println("\tPriority = " + priority);
			System.out.println("\tDowntime = " + migratedVm.getDownTime() + " seconds");
			System.out.println("\tQoS = " +  migratedVm.getQos() + " seconds / violate = " + violate);
			System.out.println("\tMigration result = " + result);
			
			if(violate == true){
				violated.add(migratedVm);
			}
			if(priority == Constant.PRIORITY_1){
				totalPri1++;
			}
			else if(priority == Constant.PRIORITY_2){
				totalPri2++;
			}
			else{
				totalPri3++;
			}
			totalMigTime += migrationTime;
			totalDownTime += migratedVm.getDownTime();
		}
		System.out.println();
		System.out.println("Bandwidth = " + NetworkGenerator.getOriginalBandwidth() + " Mbps");
		System.out.println("Page size = " + Environment.pageSizeKB + " KB");
		System.out.println("Total migrated VM = " + results.size() + " / " + scheduler.getMsgWaitingList().size());
		System.out.println("Total migrated priority: ");
		System.out.println("\tPriority 1 = " + totalPri1 + " / " + scheduler.getTotalPriority1());
		System.out.println("\tPriority 2 = " + totalPri2 + " / " + scheduler.getTotalPriority2());
		System.out.println("\tPriority 3 = " + totalPri3 + " / " + scheduler.getTotalPriority3());
		System.out.println("Total violated VM = " + violated.size());
		System.out.println("Total migration time = " + totalMigTime + 
				" (Avg. = " + totalMigTime/results.size() + ") secs");
		System.out.println("Total down time = " + totalDownTime +
				" (Avg. = " + totalDownTime/results.size() + ") secs");
		System.out.println("Time limit = " + Environment.migrationTimeLimit + " secs");
		System.out.println("Schedule type = " + Constant.scheduleKeyName.get(Environment.scheduleType));
		System.out.println("Migration type = " + Constant.migrationKeyName.get(Environment.migrationType));
		if(Environment.migrationType == Constant.PRECOPY){
			System.out.println("\tNormal dirty rate = " + Environment.normalDirtyRate);
			System.out.println("\tWWS dirty rate = " + Environment.wwsDirtyRate);
			System.out.println("\tWWS page ratio = " + Environment.wwsPageRatio);
			System.out.println("\tMax pre-copy iteration = " + Environment.maxPreCopyRound);
			System.out.println("\tMin dirty page = " + Environment.minDirtyPage);
			System.out.println("\tMax no-progress round = " + Environment.maxNoProgressRound);
		}
		System.out.println("Control type = " + Constant.controlKeyName.get(Environment.controlType));
		System.out.println("Network type = " + Constant.networkKeyName.get(Environment.networkType));
		System.out.println();*/
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
					+ " has been created in Datacenter #" + datacenterId + ", Host #"
					+ VmList.getById(getVmsCreatedList(), vmId).getHost().getId());
		} else {
			Log.printLine(CloudSim.clock() + ": " + getName() + ": Creation of VM #" + vmId
					+ " failed in Datacenter #" + datacenterId);
		}
		incrementVmsAcks();

		// all the requested VMs have been created
		if (getVmsCreatedList().size() == getVmList().size() - getVmsDestroyed()) {
			sendStartMigrationSignal();
			
			submitCloudlets();
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

	/*public void setVmMigrationWaitingList(ArrayList<HashMap<String, Object>> vmMigrationList) {
		this.vmMigrationWaitingList = vmMigrationList;
	}*/


	/*public void setVmMigrationSentList(ArrayList<VmTest> vmMigrationSentList) {
		this.vmMigrationSentList = vmMigrationSentList;
	}*/

	public void setMigrationMap(HashMap<String, Integer> migrationMap) {
		migrationMap.put(SOURCE, getDatacenterIdsList().get(0));
		migrationMap.put(DESTINATION, getDatacenterIdsList().get(1));
		this.migrationMap = migrationMap;
	}

	public void setResults(ArrayList<VmTest> results) {
		this.results = results;
	}

	/*public void setViolated(ArrayList<VmTest> violated) {
		this.violated = violated;
	}*/

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	protected void setMigManager(MigrationManager migManager) {
		this.migManager = migManager;
	}

	protected void setController(Controller controller) {
		this.controller = controller;
	}
	
	
}
