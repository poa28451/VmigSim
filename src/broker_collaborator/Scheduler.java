package broker_collaborator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cloudsim_inherit.VmTest;
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
	
	public void countPriority(VmTest vm){
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
		//HashMap<String, Object> vmData = vmWaitingList.remove(0);
		return msgWaitingList;
	}
	
	protected ArrayList<MigrationMessage> scheduleByPriorityBased(){
		sortWaitingListByPriority();
		//HashMap<String, Object> vmData = vmWaitingList.remove(0);
		return msgWaitingList;
	}
	
	protected void sortWaitingListByPriority(){
		Collections.sort(msgWaitingList, new Comparator<MigrationMessage>() {
	        @Override
	        public int compare(MigrationMessage msg1, MigrationMessage  msg2)
	        {
	        	/*VmTest vm1 = (VmTest) hash1.get("vm");
	        	VmTest vm2 = (VmTest) hash2.get("vm");
	        	int firstPri = vm1.getPriority();
	        	int secondPri = vm2.getPriority();*/
	        	VmTest vm1 = msg1.getVm();
	        	VmTest vm2 = msg2.getVm();
	        	int firstPri = vm1.getPriority();
	        	int secondPri = vm2.getPriority();
	        	return  firstPri - secondPri;
	        }
	    });
	}
	
	public ArrayList<VmTest> getVmWaitingList(){
		ArrayList<VmTest> vmWaitingList = new ArrayList<VmTest>();
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
