package container;

/**
 * This class will be contained in Parameters class for passing the arguments to the simulator.
 * This describe the VM specification entered by the user from the UI.
 * @author tawee_000
 *
 */
public class VmSpec {
	private int vmAmount;
	private int ram;
	private int priority;
	private int qos;
	
	public VmSpec(int vmAmouunt, int ram, int priority, int qos) {
		super();
		this.vmAmount = vmAmouunt;
		this.ram = ram;
		this.priority = priority;
		this.qos = qos;
	}

	public int getVmAmount() {
		return vmAmount;
	}

	public int getRam() {
		return ram;
	}

	public int getPriority() {
		return priority;
	}

	public int getQos() {
		return qos;
	}
}
