package broker_collaborator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cloudsim_inherit.VmigSimVm;
import variable.Constant;
import variable.Environment;
import message.MigrationMessage;

public class Scheduler {
	private ArrayList<MigrationMessage> msgWaitingList;
	private int totalPriority1, totalPriority2, totalPriority3;
	
	public Scheduler(){
		setMsgWaitingList(new ArrayList<MigrationMessage>());
		setTotalPriority1(0);
		setTotalPriority2(0);
		setTotalPriority3(0);
	}
	
	public void addMsgToWaitingList(MigrationMessage vmData){
		msgWaitingList.add(vmData);
		countPriority(vmData.getVm());
	}
	
	public void countPriority(VmigSimVm vm){
		int priority = vm.getPriority();
		switch (priority) {
			case Constant.PRIORITY_1:
				setTotalPriority1(getTotalPriority1() + 1);
				break;
			
			case Constant.PRIORITY_2:
				setTotalPriority2(getTotalPriority2() + 1);
				break;
				
			default:
				setTotalPriority3(getTotalPriority3() + 1);
				break;
		}
	}
	
	/**
	 * Order the VMs being migrated by scheduling policy
	 * @return
	 */
	public ArrayList<MigrationMessage> scheduleMigration(){
		switch (Environment.scheduleType) {
			case Constant.FIFO:
				
				return scheduleByFifo();
			
			case Constant.PRIORITY_BASED:
				return scheduleByPriorityBased();
				
			default:
				return scheduleByFifo();
		}
	}

	protected ArrayList<MigrationMessage> scheduleByFifo(){
		//Do nothing with the list.
		return msgWaitingList;
	}
	
	protected ArrayList<MigrationMessage> scheduleByPriorityBased(){
		//Sort the list by priority
		sortWaitingListByPriority();
		return msgWaitingList;
	}
	
	protected void sortWaitingListByPriority(){
		Collections.sort(msgWaitingList, new Comparator<MigrationMessage>() {
	        @Override
	        public int compare(MigrationMessage msg1, MigrationMessage  msg2)
	        {
	        	VmigSimVm vm1 = msg1.getVm();
	        	VmigSimVm vm2 = msg2.getVm();
	        	int firstPri = vm1.getPriority();
	        	int secondPri = vm2.getPriority();
	        	//If vm1's priority is less than vm2's
	        	//	vm1 will be migrated before vm2
	        	return  firstPri - secondPri;
	        }
	    });
	}
	
	public ArrayList<VmigSimVm> getVmWaitingList(){
		ArrayList<VmigSimVm> vmWaitingList = new ArrayList<VmigSimVm>();
		for(MigrationMessage msg : getMsgWaitingList()){
			vmWaitingList.add(msg.getVm());
		}
		return vmWaitingList;
	}
	
	public ArrayList<MigrationMessage> getMsgWaitingList() {
		return msgWaitingList;
	}

	protected void setMsgWaitingList(ArrayList<MigrationMessage> vmWaitingList) {
		this.msgWaitingList = vmWaitingList;
	}

	protected int getTotalPriority1() {
		return totalPriority1;
	}

	protected void setTotalPriority1(int totalPriority1) {
		this.totalPriority1 = totalPriority1;
	}

	protected int getTotalPriority2() {
		return totalPriority2;
	}

	protected void setTotalPriority2(int totalPriority2) {
		this.totalPriority2 = totalPriority2;
	}

	protected int getTotalPriority3() {
		return totalPriority3;
	}

	protected void setTotalPriority3(int totalPriority3) {
		this.totalPriority3 = totalPriority3;
	}
}
