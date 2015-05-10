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
		double startClock = message.getSendClock();
		message.setReceiveClock(CloudSim.clock());

		scheduler.addMsgToWaitingList(message);//Add VM to the queue

		//If every VM listed already, begin the migration
		if(isAllVmInWaitingList()){
			System.out.println();
			System.out.println(CloudSim.clock() + " Broker: Received all queue message from Source DC at " + startClock);
			System.out.println("Amount of VM in a waiting queue: " + scheduler.getMsgWaitingList().size());

			Datacenter destDC = (Datacenter) CloudSim.getEntity(3);
			//Send the scheduled VM list to the controller.
			controller = new Controller(this, destDC, scheduler.scheduleMigration());
			controller.startControlling();
		}
	}
	
	
	protected boolean isAllVmInWaitingList(){
		return scheduler.getMsgWaitingList().size() == getVmList().size();		
	}
	
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
