package cloudsim_inherit;

import java.util.List;

import message.MigrationMessage;

import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEvent;

import variable.Constant;

public class DatacenterSrcTest extends Datacenter{	
	public DatacenterSrcTest(String name,
			DatacenterCharacteristics characteristics,
			VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList,
			double schedulingInterval) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList,
				schedulingInterval);
	}

	@Override
	public void processEvent(SimEvent ev) {		
		switch (ev.getTag()) {
		case Constant.START_VM_MIGRATE:
			processStartVmMigrate(ev);
			break;
		/*case CustomTags.SEND_VM_MIGRATE:
			processSendVmMigrate(ev);
			break;*/

		default:
			super.processEvent(ev);
			break;
		}
	}
	
	/**
	 * Code for the source DC, used to handle the migration start signal
	 * @param ev The start event sent from the controller
	 */
	public void processStartVmMigrate(SimEvent ev){
		System.out.println();
		System.out.println(CloudSim.clock() + " DC id: " + ev.getDestination() + ": Recieved start migration message from controller");
		System.out.println("Starting the migration with vm amount: " + getVmList().size());
		

		for(Vm vm : getVmList()){
			//Deallocate the VM from the current datacenter
			getVmAllocationPolicy().deallocateHostForVm(vm);
			
			/*HashMap<String, Object> hash = new HashMap<String, Object>();
			hash.put("vm", vm); //Add VM to the map
			hash.put("clock", CloudSim.clock()); //Add start time of the migration (of this VM)
*/			
			VmTest migrate = (VmTest) vm;
			migrate.setStartClock(CloudSim.clock());
			MigrationMessage message = new MigrationMessage(migrate, CloudSim.clock());
			
			//Send the map back to the controller for queuing
			sendNow(ev.getSource(), Constant.QUEUE_VM_MIGRATE, message);
		}
		getVmList().clear();//Since we migrate all the vm, the list should be cleared
	}
	/*
	*//**
	 * Code for the destination DC, used to handle the VM sending signal 
	 * @param ev The sending event sent from the controller
	 *//*
	public void processSendVmMigrate(SimEvent ev){
		
		HashMap<String, Object> received = (HashMap<String, Object>)ev.getData();		
		VmTest migratedVm = (VmTest) received.get("vm");
		double startClock = (double) received.get("clock");
		double migrationTime = CloudSim.clock() - startClock;
		
		//MigrationMessage message = (MigrationMessage) ev.getData();
		//VmTest migratedVm = message.getVm();
		//double startClock = migratedVm.getStartClock();
		//double migrationTime = CloudSim.clock() - startClock;
		//double migrationTime = migratedVm.getMigrationTime();
		
		//if(isDoneSending){
		if(Environment.migrationType == Constant.OFFLINE){
			handleOfflineMigration(ev);
		}
		else{
			handlePreCopyMigration(ev);
		}
	}
	
	protected void handleOfflineMigration(SimEvent ev){
		MigrationMessage message = (MigrationMessage) ev.getData();
		VmTest migratedVm = message.getVm();
		//double migrationTime = migratedVm.getMigrationTime();
		double downTime = migratedVm.getDownTime();
		
		System.out.println();
		System.out.println(CloudSim.clock() + " DC id: " + ev.getDestination() + ": Recieved migrated VM from controller");
		System.out.println("\tVM id: " + migratedVm.getId());
		
		boolean result = allocateResourceForVm(migratedVm);
		migratedVm.setStopClock(CloudSim.clock());
		migratedVm.setViolated(isViolateQos(migratedVm.getQos(), downTime));
		migratedVm.setMigrated(result);
		sendNow(ev.getSource(), CustomTags.REPORT_VM_MIGRATE, message); 
	}
	
	protected void handlePreCopyMigration(SimEvent ev){
		PreCopyMessage message = (PreCopyMessage) ev.getData();
		VmTest migratedVm = message.getVm();
		//double migrationTime = migratedVm.getMigrationTime();
		double downTime = migratedVm.getDownTime();
		System.out.println();
		
		if(!message.isDoneMigrating()){
			System.out.println(CloudSim.clock() + " DC id: " + ev.getDestination() + ": Recieved VM pages from controller");
			System.out.println("\tVM id: " + migratedVm.getId());
			if(message.getDirtyPage() == Integer.MIN_VALUE){
				//This means it's a first round of pre-copy
				System.out.println("\tReceived all memory page of VM: " + migratedVm.getMemoryPageNum() + " pages");
			}
			else{
				//System.out.println("\tDirty page amount: " + message.getMigratedPageIndices().size());
				System.out.println("\tDirty page amount: " + message.getDirtyPage());
			}
		}
		else{
			System.out.println(CloudSim.clock() + " DC id: " + ev.getDestination() + ": Recieved dirty pages and VM state from controller");
			System.out.println("\tVM id: " + migratedVm.getId());
			//System.out.println("\tDirty page amount: " + message.getMigratedPageIndices().size());
			System.out.println("\tDirty page amount: " + message.getDirtyPage());
			
			boolean result = allocateResourceForVm(migratedVm);
			migratedVm.setStopClock(CloudSim.clock());
			migratedVm.setViolated(isViolateQos(migratedVm.getQos(), downTime));
			migratedVm.setMigrated(result);
			sendNow(ev.getSource(), CustomTags.REPORT_VM_MIGRATE, message); 
		}
	}
	
	protected boolean allocateResourceForVm(VmTest migratedVm){
		boolean result = getVmAllocationPolicy().allocateHostForVm(migratedVm);
		if (result) {
			getVmList().add(migratedVm);

			if (migratedVm.isBeingInstantiated()) {
				migratedVm.setBeingInstantiated(false);
			}

			migratedVm.updateVmProcessing(CloudSim.clock(), getVmAllocationPolicy().getHost(migratedVm).getVmScheduler()
					.getAllocatedMipsForVm(migratedVm));
			
			System.out.println("VM id: " + migratedVm.getId() + " allocated into Host id " + migratedVm.getHost().getId());
			//System.out.println("Migration time: " + migrationTime);
		}
		else{
			System.out.println("VM id: " + migratedVm.getId() + " failed to allocated");
		}
		System.out.println("Current VM number: " + getVmList().size());	
		for(Host host : getHostList()){//Just check the hosts if there are vms migrated from another dc
			System.out.println("Host " + host.getId() + " vm left: " + host.getVmList().size());
			for(Vm vm : host.getVmList()){
				System.out.println("\t vm" + vm.getId());
			}
		}
		
		return result;
	}
	
	public boolean isViolateQos(double qos, double downTime){
		if(qos < downTime) return true;
		return false;
	}*/
}
