package variable;

import java.util.HashMap;


public class Constant {
	public static final int KILO_BYTE = 1024;
	//public static final int KILO_BIT = 1000;
	public static final double START_INTERVAL = 0.1;
	public static final double STATIC_NETWORK_SD_PERCENTAGE = 1.588500677;
	
	public static final int PAGE_SIZE_KB = 4;
	private static final int GPR_SIZE_BYTE = 16*(64/8);
	private static final int FP_SIZE_BYTE = 16*(128/8);
	public static final int REGISTER_SIZE_BYTE = GPR_SIZE_BYTE + FP_SIZE_BYTE;
	
	//The precision of decimal used in calculating the network interval
	// Ex. 10 means the number will be 10-digit decimal number
	public static final int DECIMAL_SCALE = 10;
	
	public static final String CLOSED_LOOP_FILE_PATH = "run_simulation/closed-loop/";
	public static final String BW_1P_FILENAME = "bandwidth-1p.txt";
	public static final String FUZZY_RULE_FILENAME = "fuzzy-rule.txt";
	public static final int CLOSED_LOOP_START_THREAD = 1;
	
	/**
	 * Custom array indices
	 */
	public static final int FIFO = 0, PRIORITY_BASED = 1;
	public static final int OFFLINE = 0, PRECOPY = 1;
	public static final int  OPEN_LOOP = 0, CLOSED_LOOP = 1;
	public static final int STATIC = 0, DYNAMIC = 1;
	public static final int PRIORITY_1 = 1, PRIORITY_2 = 2, PRIORITY_3 = 3;
	
	
	/**
	 * Custom tags
	 */
	private static final int BASE = 0;
	//This will be sent from the controller to the source, used as a simulation start signal
	public static final int START_VM_MIGRATE = BASE + 200; 
	//This will be sent from the source back to the controller, to queue the VM
	public static final int QUEUE_VM_MIGRATE = BASE + 201; 
	//This will be sent from the controller to the destination, to send the VM
	public static final int SEND_VM_MIGRATE = BASE + 202;
	//This will be sent from the destination to the controller, to report the migration time
	public static final int REPORT_VM_MIGRATE = BASE + 203;
	
	public static final int SOURCE_DC = BASE + 300, DESTINATION_DC = BASE + 301;
	
	public static final HashMap<Integer, String> scheduleKeyName;
	static{
		scheduleKeyName = new HashMap<Integer, String>();
		scheduleKeyName.put(FIFO, "fifo");
		scheduleKeyName.put(DYNAMIC, "priority");
	}
	
	public static final HashMap<Integer, String> migrationKeyName;
	static{
		migrationKeyName = new HashMap<Integer, String>();
		migrationKeyName.put(OFFLINE, "offline");
		migrationKeyName.put(PRECOPY, "precopy");
	}
	
	public static final HashMap<Integer, String> controlKeyName;
	static{
		controlKeyName = new HashMap<Integer, String>();
		controlKeyName.put(OPEN_LOOP, "openloop");
		controlKeyName.put(CLOSED_LOOP, "closedloop");
	}
	
	public static final HashMap<Integer, String> networkKeyName;
	static{
		networkKeyName = new HashMap<Integer, String>();
		networkKeyName.put(STATIC, "static");
		networkKeyName.put(DYNAMIC, "dynamic");
	}
	
	/**
	 * Used to get the constant number code of the string key entered
	 * 	Ex. getScheduleKeyCode("fifo") will return 0
	 * @param name the name of scheduling like "fifo" or "priority"
	 * @return the number code of scheduling
	 */
	public static int getScheduleKeyCode(String name){
		int keyCode = -1;
		for(Integer code : scheduleKeyName.keySet()){
			if(scheduleKeyName.get(code).equalsIgnoreCase(name)){
				keyCode = code;
				break;
			}
		}
		return keyCode;
	}
	
	public static int getMigrationKeyCode(String name){
		int keyCode = -1;
		for(Integer code : migrationKeyName.keySet()){
			if(migrationKeyName.get(code).equalsIgnoreCase(name)){
				keyCode = code;
				break;
			}
		}
		return keyCode;
	}
	
	public static int getControlKeyCode(String name){
		int keyCode = -1;
		for(Integer code : controlKeyName.keySet()){
			if(controlKeyName.get(code).equalsIgnoreCase(name)){
				keyCode = code;
				break;
			}
		}
		return keyCode;
	}
	
	public static int getNetworkKeyCode(String name){
		int keyCode = -1;
		for(Integer code : networkKeyName.keySet()){
			if(networkKeyName.get(code).equalsIgnoreCase(name)){
				keyCode = code;
				break;
			}
		}
		return keyCode;
	}
}
