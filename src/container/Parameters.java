package container;

import java.util.ArrayList;

/**
 * The class used by ExperimentRunner for passing arguments of simulation to VmigSim.
 * 	The values contained in this class will be set into Environment class by VmigSim
 * @author tawee_000
 *
 */
public class Parameters {
	private double maxBandwidth;//Mbps unit
	private double meanBandwidth;//Mbps unit
	private double timeLimit;//Second unit
	private int scheduleType;//Constant value
	private int migrationType;//Constant value
	private int controlType;//Constant value
	private int networkType;//Constant value
	private int pageSizeKB;//KB unit
	
	private double wwsPageRatio, wwsDirtyRate;//Percent unit
	private double normalDirtyRate;//Percent unit
	private int maxPreCopyRound;
	private int minDirtyPage;
	private int maxNoProgressRound;
	private double networkInterval;//Second unit
	private double networkSD;//Percent unit
	
	private ArrayList<VmSpec> vmSpecList;
	
	public Parameters(){
		setMaxBandwidth(0);
		setMeanBandwidth(0);
		setNetworkType(0);
		setTimeLimit(0);
		setScheduleType(0);
		setMigrationType(0);
		setControlType(0);
		setPageSizeKB(0);
		setVmSpecList(new ArrayList<VmSpec>());
		
		setWwsPageRatio(0);
		setWwsDirtyRate(0);
		setNormalDirtyRate(0);
		setMaxPreCopyRound(0);
		setMinDirtyPage(0);
		setMaxNoProgressRound(0);
		setNetworkInterval(0);
		setNetworkSD(0);
	}
	
	public double getMaxBandwidth() {
		return maxBandwidth;
	}
	
	public void setMaxBandwidth(double maxBandwidth) {
		this.maxBandwidth = maxBandwidth;
	}
	
	public double getMeanBandwidth() {
		return meanBandwidth;
	}
	
	public void setMeanBandwidth(double meanBandwidth) {
		this.meanBandwidth = meanBandwidth;
	}
	
	public double getTimeLimit() {
		return timeLimit;
	}
	
	public void setTimeLimit(double timeLimit) {
		this.timeLimit = timeLimit;
	}
	
	public int getScheduleType() {
		return scheduleType;
	}
	
	public void setScheduleType(int scheduleType) {
		this.scheduleType = scheduleType;
	}
	
	public int getMigrationType() {
		return migrationType;
	}
	
	public void setMigrationType(int migrationType) {
		this.migrationType = migrationType;
	}
	
	public int getControlType() {
		return controlType;
	}

	public void setControlType(int controlType) {
		this.controlType = controlType;
	}

	public int getNetworkType() {
		return networkType;
	}
	
	public void setNetworkType(int networkType) {
		this.networkType = networkType;
	}
	
	public double getWwsPageRatio() {
		return wwsPageRatio;
	}
	
	public void setWwsPageRatio(double wwsPageRatio) {
		this.wwsPageRatio = wwsPageRatio;
	}
	
	public double getWwsDirtyRate() {
		return wwsDirtyRate;
	}
	
	public void setWwsDirtyRate(double wwsDirtyRate) {
		this.wwsDirtyRate = wwsDirtyRate;
	}
	
	public double getNormalDirtyRate() {
		return normalDirtyRate;
	}
	
	public void setNormalDirtyRate(double normalDirtyRate) {
		this.normalDirtyRate = normalDirtyRate;
	}
	
	public int getPageSizeKB() {
		return pageSizeKB;
	}
	
	public void setPageSizeKB(int pageSizeKB) {
		this.pageSizeKB = pageSizeKB;
	}
	
	public int getMaxPreCopyRound() {
		return maxPreCopyRound;
	}
	
	public void setMaxPreCopyRound(int maxPreCopyRound) {
		this.maxPreCopyRound = maxPreCopyRound;
	}
	
	public int getMinDirtyPage() {
		return minDirtyPage;
	}
	
	public void setMinDirtyPage(int minDirtyPage) {
		this.minDirtyPage = minDirtyPage;
	}
	
	public int getMaxNoProgressRound() {
		return maxNoProgressRound;
	}
	
	public void setMaxNoProgressRound(int maxNoProgressRound) {
		this.maxNoProgressRound = maxNoProgressRound;
	}
	
	public double getNetworkInterval() {
		return networkInterval;
	}
	
	public void setNetworkInterval(double networkInterval) {
		this.networkInterval = networkInterval;
	}
	
	public double getNetworkSD() {
		return networkSD;
	}
	
	public void setNetworkSD(double networkSD) {
		this.networkSD = networkSD;
	}
	
	public ArrayList<VmSpec> getVmSpecList() {
		return vmSpecList;
	}
	
	public void setVmSpecList(ArrayList<VmSpec> vmSpecList) {
		this.vmSpecList = vmSpecList;
	}
	
	public void addVmSpec(VmSpec vmSpec){
		vmSpecList.add(vmSpec);
	}
}
