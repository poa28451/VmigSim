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
		
			VmTest migrate = (VmTest) vm;
			migrate.setStartClock(CloudSim.clock());
			MigrationMessage message = new MigrationMessage(migrate, CloudSim.clock());
			
			//Send the map back to the controller for queuing
			sendNow(ev.getSource(), Constant.QUEUE_VM_MIGRATE, message);
		}
		getVmList().clear();//Since we migrate all the vm, the list should be cleared
	}
}
