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

public class VmigSimBroker extends DatacenterBroker {
	private HashMap<String, Integer> migrationMap;
	private final String SOURCE = "src";
	private final String DESTINATION = "dest";

	private ArrayList<VmTest> results;
	private Scheduler scheduler;
	private MigrationManager migManager;
	private Controller controller;
	private double nextMigrationDelay = 0;
	
	public VmigSimBroker(String name) throws Exception {
		super(name);
		setResults(new ArrayList<VmTest>());
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
		MigrationMessage message = (MigrationMessage) ev.getData();
		VmTest migratedVm = message.getVm();
		double startClock = message.getSendClock();
		message.setReceiveClock(CloudSim.clock());

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
			
		} while(!msg.isLastMigrationMsg());
	}
	
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
					+ " has been created in Datacenter #" + datacenterId + ", Host #"
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

	public void setMigrationMap(HashMap<String, Integer> migrationMap) {
		migrationMap.put(SOURCE, getDatacenterIdsList().get(0));
		migrationMap.put(DESTINATION, getDatacenterIdsList().get(1));
		this.migrationMap = migrationMap;
	}

	public void setResults(ArrayList<VmTest> results) {
		this.results = results;
	}

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
