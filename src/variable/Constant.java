package variable;

import java.util.HashMap;


public class Constant {
	public static final int KILO_BYTE = 1024;
	public static final double START_INTERVAL = 0.1;
	
	//The precision of decimal used in calculating the network interval
	// Ex. 10 means the number will be 10-digit decimal number
	public static final int DECIMAL_SCALE = 10;
	
	/**
	 * Custom array indices
	 */
	public static final int FIFO = 0, PRIORITY_BASED = 1;
	public static final int OFFLINE = 0, PRECOPY = 1;
	public static final int  OPEN_LOOP = 0, CLOSE_LOOP = 1;
	public static final int STATIC = 0, DYNAMIC = 1;
	public static final int PRIORITY_1 = 1, PRIORITY_2 = 2, PRIORITY_3 = 3;
	
	/**
	 * JSON key code
	 */
	private static final int KEY_BASE = 0;
	public static final int ENVIRONMENT = KEY_BASE + 1;
	public static final int VM_SPEC_LIST = KEY_BASE + 2;
	
	public static final int MAX_BANDWIDTH = KEY_BASE + 11;
	public static final int TIME_LIMIT = KEY_BASE + 12;
	public static final int SCHEDULE_TYPE = KEY_BASE + 13;
	public static final int MIGRATION_TYPE = KEY_BASE + 14;
	public static final int CONTROL_TYPE = KEY_BASE + 15;
	public static final int NETWORK_TYPE = KEY_BASE + 16;
	public static final int PAGE_SIZE = KEY_BASE + 17;
	
	public static final int WWS_RATIO = KEY_BASE + 18;
	public static final int WWS_DIRTY_RATE = KEY_BASE + 19;
	public static final int NORMAL_DIRTY_RATE = KEY_BASE + 20;
	public static final int MAX_PRECOPY_ROUND = KEY_BASE + 21;
	public static final int MIN_DIRTY_PAGE = KEY_BASE + 22;
	public static final int MAX_NO_PROGRESS = KEY_BASE + 23;
	
	public static final int NETWORK_INTERVAL = KEY_BASE + 24;
	public static final int NETWORK_SD = KEY_BASE + 25;
	
	public static final int VM_AMOUNT = KEY_BASE + 26;
	public static final int RAM = KEY_BASE + 27;
	public static final int PRIORITY = KEY_BASE + 28;
	public static final int QOS = KEY_BASE + 29;
	public static final int MEAN_BANDWIDTH = KEY_BASE + 30;
	
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
	
	/**
	 * Key name used for reading JSON input file
	 */
	private static final HashMap<Integer, String> jsonKeyName;
	static{
		jsonKeyName = new HashMap<Integer, String>();
		jsonKeyName.put(Constant.ENVIRONMENT, "environment");
		jsonKeyName.put(Constant.VM_SPEC_LIST, "vmSpecList");
		
		jsonKeyName.put(Constant.MAX_BANDWIDTH, "maxBandwidth");
		jsonKeyName.put(Constant.MEAN_BANDWIDTH, "meanBandwidth");
		jsonKeyName.put(Constant.TIME_LIMIT, "timeLimit");
		jsonKeyName.put(Constant.SCHEDULE_TYPE, "scheduleType");
		jsonKeyName.put(Constant.MIGRATION_TYPE, "migrationType");
		jsonKeyName.put(Constant.CONTROL_TYPE, "controlType");
		jsonKeyName.put(Constant.NETWORK_TYPE, "networkType");
		jsonKeyName.put(Constant.PAGE_SIZE, "pageSize");
		jsonKeyName.put(Constant.WWS_RATIO, "wwsRatio");
		jsonKeyName.put(Constant.WWS_DIRTY_RATE, "wwsDirtyRate");
		jsonKeyName.put(Constant.NORMAL_DIRTY_RATE, "normalDirtyRate");
		jsonKeyName.put(Constant.MAX_PRECOPY_ROUND, "maxPreCopyRound");
		jsonKeyName.put(Constant.MIN_DIRTY_PAGE, "minDirtyPage");
		jsonKeyName.put(Constant.MAX_NO_PROGRESS, "maxNoProgressRound");
		jsonKeyName.put(Constant.NETWORK_INTERVAL, "networkInterval");
		jsonKeyName.put(Constant.NETWORK_SD, "networkSD");
		jsonKeyName.put(Constant.VM_AMOUNT, "vmAmount");
		jsonKeyName.put(Constant.RAM, "ram");
		jsonKeyName.put(Constant.PRIORITY, "priority");
		jsonKeyName.put(Constant.QOS, "qos");
	}
	
	public static final HashMap<Integer, String> scheduleKeyName;
	static{
		scheduleKeyName = new HashMap<Integer, String>();
		scheduleKeyName.put(Constant.FIFO, "fifo");
		scheduleKeyName.put(Constant.DYNAMIC, "priority");
	}
	
	public static final HashMap<Integer, String> migrationKeyName;
	static{
		migrationKeyName = new HashMap<Integer, String>();
		migrationKeyName.put(Constant.OFFLINE, "offline");
		migrationKeyName.put(Constant.PRECOPY, "precopy");
	}
	
	public static final HashMap<Integer, String> controlKeyName;
	static{
		controlKeyName = new HashMap<Integer, String>();
		controlKeyName.put(Constant.OPEN_LOOP, "openloop");
		controlKeyName.put(Constant.CLOSE_LOOP, "closeloop");
	}
	
	public static final HashMap<Integer, String> networkKeyName;
	static{
		networkKeyName = new HashMap<Integer, String>();
		networkKeyName.put(Constant.STATIC, "static");
		networkKeyName.put(Constant.DYNAMIC, "dynamic");
	}
	
	public static String getJSONKeyName(int code){
		return jsonKeyName.get(code);
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
