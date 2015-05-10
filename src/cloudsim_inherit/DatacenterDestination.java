package cloudsim_inherit;

import java.util.List;

import message.MigrationMessage;
import message.PreCopyMessage;

import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEvent;

import variable.Constant;
import variable.Environment;

public class DatacenterDestination extends Datacenter{	
	public DatacenterDestination(String name,
			DatacenterCharacteristics characteristics,
			VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList,
			double schedulingInterval) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList,
				schedulingInterval);
	}

	@Override
	public void processEvent(SimEvent ev) {		
		switch (ev.getTag()) {
		case Constant.SEND_VM_MIGRATE:
			processSendVmMigrate(ev);
			break;

		default:
			super.processEvent(ev);
			break;
		}
	}
	
	/**
	 * Code for the destination DC, used to handle the VM sending signal 
	 * @param ev The sending event sent from the controller
	 */
	public void processSendVmMigrate(SimEvent ev){
		if(Environment.migrationType == Constant.OFFLINE){
			handleOfflineMigration(ev);
		}
		else{
			handlePreCopyMigration(ev);
		}
	}
	
	protected void handleOfflineMigration(SimEvent ev){
		MigrationMessage message = (MigrationMessage) ev.getData();
		VmigSimVm migratedVm = message.getVm();
		double downTime = migratedVm.getDowntime();
		
		System.out.println();
		System.out.println(CloudSim.clock() + " Destination DC: Recieved migrated VM from Broker");
		System.out.println("\tVM id: " + migratedVm.getId());
		
		boolean result = allocateResourceForVm(migratedVm);
		migratedVm.setStopClock(CloudSim.clock());
		migratedVm.setViolated(isViolateQos(migratedVm.getQos(), downTime));
		migratedVm.setDoneMigration(result);
		sendNow(ev.getSource(), Constant.REPORT_VM_MIGRATE, message); 
	}
	
	protected void handlePreCopyMigration(SimEvent ev){
		PreCopyMessage message = (PreCopyMessage) ev.getData();
		VmigSimVm migratedVm = message.getVm();
		double downTime = migratedVm.getDowntime();
		
		double transferred = migratedVm.getTotalTransferredKB();
		double totalTransferred = transferred + message.getDataSizeKB();
		migratedVm.setTotalTransferredKB(totalTransferred);
		
		/*System.out.println();
		System.out.println(CloudSim.clock() + " Destination DC: Recieved VM pages from Broker");
		System.out.println("\tVM id: " + migratedVm.getId());
		if(message.getDirtyPageAmount() == Integer.MIN_VALUE){
			//This means it's a first round of pre-copy
			System.out.println("\tReceived all memory page of VM: " + migratedVm.getMemoryPageNum() + " pages");
		}
		else{
			System.out.println("\tDirty page: " + message.getDirtyPageAmount());
		}
		printTotalTransferData(totalTransferred);*/
		
		//If this is the Stop-and-Copy phase's message
		if(message.isLastMigrationMsg()){
			System.out.println("\tVM has done the migration");
			boolean result = allocateResourceForVm(migratedVm);
			migratedVm.setStopClock(CloudSim.clock());
			migratedVm.setViolated(isViolateQos(migratedVm.getQos(), downTime));
			migratedVm.setDoneMigration(result);
			sendNow(ev.getSource(), Constant.REPORT_VM_MIGRATE, message); 
		}
	}
	
	protected boolean allocateResourceForVm(VmigSimVm migratedVm){
		boolean result = getVmAllocationPolicy().allocateHostForVm(migratedVm);
		if (result) {
			getVmList().add(migratedVm);

			if (migratedVm.isBeingInstantiated()) {
				migratedVm.setBeingInstantiated(false);
			}

			/*migratedVm.updateVmProcessing(CloudSim.clock(), getVmAllocationPolicy().getHost(migratedVm).getVmScheduler()
					.getAllocatedMipsForVm(migratedVm));*/
			
			System.out.println("VM id: " + migratedVm.getId() + " allocated into Destination DC's host");
		}
		else{
			System.out.println("VM id: " + migratedVm.getId() + " failed to allocate into Destination DC's host");
		}
		
		return result;
	}
	
	public boolean isViolateQos(double qos, double downTime){
		if(qos < downTime) return true;
		return false;
	}
	
	private void printTotalTransferData(double totalTransferred){
		System.out.println("\tTotal transferred data = " + totalTransferred + " KB");
	}
}
