package main;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import broker_collaborator.NetworkGenerator;
import cloudsim_inherit.DatacenterBrokerTest;
import cloudsim_inherit.DatacenterDstTest;
import cloudsim_inherit.DatacenterSrcTest;
import cloudsim_inherit.VmAllocationPolicyTest;
import cloudsim_inherit.VmTest;
import container.Parameters;
import container.VmSpec;
import variable.Constant;
import variable.Environment;


public class VmigSim {
	//private List<Vm> vmlistPri1, vmlistPri2, vmlistPri3;
	
	@SuppressWarnings("unused")
	private Datacenter datacenterSrc, datacenterDest;
	
	private DatacenterBroker broker;
	
	private int currentVm = 0;

	public void startSimulation(Parameters param){
		setEnvironment(param);
		initCloudSim(1, false);
		initDatacenter();
		initBroker();
		initVm();
		
		simulateMigration();
	}
	
	public void setEnvironment(Parameters param){
		double bandwidth = param.getBandwidth();
		double timeLimit = param.getTimeLimit();
		int scheduleType = param.getScheduleType();
		int migrationType = param.getMigrationType();
		int controlType = param.getControlType();
		int networkType = param.getNetworkType();
		ArrayList<VmSpec> vmSpecList = param.getVmSpecList();
		
		double wwsPageRatio = param.getWwsPageRatio();
		double wwsDirtyRate = param.getWwsDirtyRate();
		double normalDirtyRate = param.getNormalDirtyRate();
		int pageSizeKB = param.getPageSizeKB();
		int maxPreCopyRound = param.getMaxPreCopyRound();
		int minDirtyPage = param.getMinDirtyPage();
		int maxNoProgressRound = param.getMaxNoProgressRound();
		double networkInterval = param.getNetworkInterval();
		double networkSD = param.getNetworkSD();
	
		Environment.setMigrationTimeLimit(timeLimit);
		Environment.setScheduleType(scheduleType);
		Environment.setMigrationType(migrationType);
		Environment.setControlType(controlType);
		Environment.setNetworkType(networkType);
		Environment.setPageSizeKB(pageSizeKB);
		Environment.setVmSpecList(vmSpecList);
		
		Environment.setWwsPageRatio(wwsPageRatio);
		Environment.setWwsDirtyRate(wwsDirtyRate);
		Environment.setNormalDirtyRate(normalDirtyRate);
		Environment.setMaxPreCopyRound(maxPreCopyRound);
		Environment.setMinDirtyPage(minDirtyPage);
		Environment.setMaxNoProgressRound(maxNoProgressRound);
		Environment.setNetworkInterval(networkInterval);
		Environment.setNetworkSD(networkSD);
		
		new NetworkGenerator(bandwidth);
	}
	
	public void initVm(){
		ArrayList<VmSpec> vmSpecList = Environment.vmSpecList;
		for(VmSpec spec : vmSpecList){
			int amount = spec.getVmAmount();
			int ram = spec.getRam();
			int priority = spec.getPriority();
			int qos = spec.getQos();
			
			createVms(amount, ram, priority, qos);
		}
	}
	
	public void createVms(int vmAmount, int ram, int priority, int qos){
		List<Vm> vmlist = createVmList(broker.getId(), vmAmount, ram, priority, qos);
		broker.submitVmList(vmlist);
	}
	
	private void initCloudSim(int numUser, boolean traceFlag){
		Calendar calendar = Calendar.getInstance();		
		CloudSim.init(numUser, calendar, traceFlag);
		CloudSim.terminateSimulation(Environment.migrationTimeLimit);
	}
	
	private void initDatacenter(){
		datacenterSrc = createDatacenter(Constant.SOURCE_DC, "Datacenter_0");
		datacenterDest = createDatacenter(Constant.DESTINATION_DC, "Datacenter_1");
	}
	
	private void initBroker(){
		broker = createBroker(1);
	}
	
	private void simulateMigration(){
		CloudSim.startSimulation();
		System.out.println("VmigSim finished!");
	}

	private Datacenter createDatacenter(int dcType, String name){
		List<Host> hostList;
		hostList = createHostList();

		String arch = "x86";      // system architecture
		String os = "Linux";          // operating system
		String vmm = "Xen";
		double time_zone = 10.0;         // time zone this resource located
		double cost = 3.0;              // the cost of using processing in this resource
		double costPerMem = 0.05;		// the cost of using memory in this resource
		double costPerStorage = 0.001;	// the cost of using storage in this resource
		double costPerBw = 0.0;			// the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>();	//we are not adding SAN devices by now

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);

		Datacenter datacenter = null;
		try {
			switch (dcType) {
				case Constant.SOURCE_DC:
					datacenter = new DatacenterSrcTest(name, characteristics, new VmAllocationPolicyTest(hostList), storageList, 0);
					break;
				case Constant.DESTINATION_DC:
					datacenter = new DatacenterDstTest(name, characteristics, new VmAllocationPolicyTest(hostList), storageList, 0);
					break;
				default:
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datacenter;
	}
	
	/**
	 * 
	 * @param amountHost Number of host for this data center
	 * @param amountPe Number of PEs per host
	 * @param ram Amount of RAM for each host
	 * @param storage Amount of storage for each host
	 * @param bw Amount of bandwidth for each host
	 * @param mips Amount of MIPS for each PE
	 * @return
	 */
	private List<Host> createHostList(){
		
		int numPePerHost = 4;
		//Set host memory (MB) to the highest RAM of VMs entered by user multiply by PE of host
		//This make hosts contain 4 VM each for sure (1 VM has 1 PE)
		int hostRam = Environment.getHighestVmRam() * numPePerHost;
		int numHost = calculateHostAmount(numPePerHost);
		long storage = 1000000; //host storage
		int bw = 10000; //Mbps
		int mips=1000;
		List<Host> hostList = new ArrayList<Host>();
		for(int i=0; i<numHost; i++){
			List<Pe> peList = createPeList(numPePerHost, mips);
			hostList.add(new Host(
    				i,
    				new RamProvisionerSimple(hostRam),
    				new BwProvisionerSimple(bw),
    				storage,
    				peList,
    				new VmSchedulerSpaceShared(peList)
    			)
    		);
		}
		return hostList;
	}
	
	private int calculateHostAmount(int hostPe){
		int totalVmAmount = Environment.getTotalVmAmount();
		//Each VM will get 1 PE
		int hostAmount = totalVmAmount / hostPe;
		while((hostAmount * hostPe) < totalVmAmount){
			hostAmount++;
		}
		
		return hostAmount;
	}
	
	private List<Pe> createPeList(int amountPe, int mips){
		List<Pe> peList = new ArrayList<Pe>();
		for(int i=0; i<amountPe; i++){
			peList.add(new Pe(i, new PeProvisionerSimple(mips)));
		}
		return peList;
	}

	//We strongly encourage users to develop their own broker policies, to submit vms and cloudlets according
	//to the specific rules of the simulated scenario
	private DatacenterBroker createBroker(int id){
		DatacenterBroker broker = null;
		try {
			broker = new DatacenterBrokerTest("Broker"+id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}
	
	private List<Vm> createVmList(int userId, int vmAmount, int ram, int priority, int qos) {
		LinkedList<Vm> list = new LinkedList<Vm>();
		long size = 10000;// Image size in MB
		//int ram = Environment.vmRam;// RAM size in MB
		int mips = 250;
		long bw = 1000;// bw of VM in Mbps
		int pesNumber = 1;
		String vmm = "Xen";
		//int qos = 0;

		Vm[] vm = new Vm[vmAmount];
		for(int i=0;i<vmAmount;i++, currentVm++){
			//qos = Environment.generateQos();
			//qos = Environment.qos;
			vm[i] = new VmTest(currentVm, userId, mips, pesNumber, ram, bw, size, qos, priority, vmm, new CloudletSchedulerTimeShared());
			list.add(vm[i]);
		}
		return list;
	}
	
	/**
	 * Generate a random value of QoS for VMs, scale in seconds
	 * @return
	 */
	/*private int generateQos(){
		Random r = new Random();
		int min = 10;
		int max = 30;
		//double result = min + (r.nextDouble() * (max - min));
		int result = min + (r.nextInt(max - min + 1));
		return result;
	}*/
	
/*	private static List<Cloudlet> createCloudlet(int userId, int cloudletNum, int idStart){
		LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();
		long length = 400000000;
		long fileSize = 300;
		long outputSize = 300;
		int pesNumber = 1;
		UtilizationModel utilizationModel = new UtilizationModelFull();

		Cloudlet[] cloudlet = new Cloudlet[cloudletNum];
		for(int i=0;i<cloudletNum;i++){
			cloudlet[i] = new Cloudlet(idStart + i, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
			cloudlet[i].setUserId(userId);
			list.add(cloudlet[i]);
		}
		return list;
	}*/
	
	/**
	 * Prints the Cloudlet objects
	 * @param list  list of Cloudlets
	 */
	/*private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
				"Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);
			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS){
				Log.print("SUCCESS");
				Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() +
						indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent + dft.format(cloudlet.getExecStartTime())+
						indent + indent + dft.format(cloudlet.getFinishTime()) + 
						indent + indent + dft.format(cloudlet.getWallClockTime()) +
						indent + indent + dft.format(cloudlet.getSubmissionTime()) + 
						indent + indent + CloudSim.getEntityName(cloudlet.getResourceId()));
			}
		}

	}*/
}
