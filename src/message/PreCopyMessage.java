package message;

import cloudsim_inherit.VmTest;
import variable.Constant;

public class PreCopyMessage extends MigrationMessage{
	//private ArrayList<Integer> migratedPageIndices;
	private int dirtyPage;
	
	public PreCopyMessage(VmTest vm, double startClock) {
		super(vm, startClock);
		//setMigratedPageIndices(null);
		setDirtyPage(0);
	}

	/*public ArrayList<Integer> getMigratedPageIndices() {
		return migratedPageIndices;
	}*/
	
	public int getDirtyPage() {
		return dirtyPage;
	}

	/*public void setMigratedPageIndices(ArrayList<Integer> migratedPage) {
		this.migratedPageIndices = migratedPage;
	}
	*/
	private void setDirtyPage(int dirtyPage) {
		this.dirtyPage = dirtyPage;
	}

	@Override
	public void setDataSizeKB(double dataSize) {
		super.setDataSizeKB(dataSize);
		setDirtyPage((int)(dataSize / Constant.KILO_BYTE));
	}
	
	
}
