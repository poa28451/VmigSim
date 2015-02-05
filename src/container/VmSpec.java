package container;

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
