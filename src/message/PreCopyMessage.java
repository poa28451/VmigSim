package message;

import cloudsim_inherit.VmigSimVm;

/**
 * Message used for Pre-copy migration policy.
 * The additional part is just it contains the dirty page amount, and its size is 
 * 	the 
 * @author tawee_000
 *
 */
public class PreCopyMessage extends MigrationMessage{
	private int dirtyPageAmount;
	
	public PreCopyMessage(VmigSimVm vm, double startClock) {
		super(vm, startClock);
		setDirtyPageAmount(0);
	}
	
	public int getDirtyPageAmount() {
		return dirtyPageAmount;
	}
	
	public void setDirtyPageAmount(int dirtyPageAmount) {
		this.dirtyPageAmount = dirtyPageAmount;
	}

	@Override
	public void setDataSizeKB(double dataSizeKB) {
		super.setDataSizeKB(dataSizeKB);
	}
}
