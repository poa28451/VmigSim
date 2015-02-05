package container;

import java.util.ArrayList;

public class Parameters {
	private double bandwidth;
	private double timeLimit;
	private int scheduleType;
	private int migrationType;
	private int controlType;
	private int networkType;
	private int pageSizeKB;
	
	private double wwsPageRatio, wwsDirtyRate;
	private double normalDirtyRate;
	private int maxPreCopyRound;
	private int minDirtyPage;
	private int maxNoProgressRound;
	private double networkInterval;
	private double networkSD;
	
	private ArrayList<VmSpec> vmSpecList;
	
	public Parameters(){
		//setScenarioType(0);
		//setVmRam(0);
		setBandwidth(0);
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
	
	/*public int getVmRam() {
		return vmRam;
	}
	
	public void setVmRam(int vmRam) {
		this.vmRam = vmRam;
	}*/
	
	public double getBandwidth() {
		return bandwidth;
	}
	
	public void setBandwidth(double bandwidth) {
		this.bandwidth = bandwidth;
	}
	
	public double getTimeLimit() {
		return timeLimit;
	}
	
	public void setTimeLimit(double timeLimit) {
		this.timeLimit = timeLimit;
	}
	
	/*public int getScenarioType() {
		return scenarioType;
	}
	
	public void setScenarioType(int scenarioType) {
		this.scenarioType = scenarioType;
	}*/
	
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
