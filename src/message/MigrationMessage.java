package message;

import cloudsim_inherit.VmigSimVm;

public class MigrationMessage {
	private VmigSimVm vm;
	private double sendClock;
	private double receiveClock;
	private double migrationTime;
	private double dataSizeKB;
	private boolean isLastMigrationMsg;
	
	public MigrationMessage(VmigSimVm vm, double startClock) {
		setVm(vm);
		setSendClock(startClock);
		setReceiveClock(0);
		setMigrationTime(0);
		setLastMigrationMsg(false);
		setDataSizeKB(0);
	}
	
	public MigrationMessage(VmigSimVm vm) {
		setVm(vm);
		setSendClock(0);
		setReceiveClock(0);
		setMigrationTime(0);
		setLastMigrationMsg(false);
		setDataSizeKB(0);
	}

	public VmigSimVm getVm() {
		return vm;
	}

	public void setVm(VmigSimVm vm) {
		this.vm = vm;
	}

	public double getSendClock() {
		return sendClock;
	}

	public void setSendClock(double sendClock) {
		this.sendClock = sendClock;
	}

	public double getReceiveClock() {
		return receiveClock;
	}

	public void setReceiveClock(double receiveClock) {
		this.receiveClock = receiveClock;
	}

	public double getMigrationTime() {
		return migrationTime;
	}

	public void setMigrationTime(double migrationTime) {
		this.migrationTime = migrationTime;
	}

	public boolean isLastMigrationMsg() {
		return isLastMigrationMsg;
	}

	public void setLastMigrationMsg(boolean isDoneMigrating) {
		this.isLastMigrationMsg = isDoneMigrating;
	}

	public double getDataSizeKB() {
		return dataSizeKB;
	}

	public void setDataSizeKB(double dataSize) {
		this.dataSizeKB = dataSize;
	}
}
