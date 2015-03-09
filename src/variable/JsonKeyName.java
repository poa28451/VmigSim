package variable;

import java.util.HashMap;

public class JsonKeyName {
	/**
	 * JSON reader key code
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
	 * JSON writer key code
	 */
	public static final int MIGRATED_VM = KEY_BASE + 100;
	public static final int OVERALL = KEY_BASE + 101;
	
	public static final int MIGRATION_TIME = KEY_BASE + 110;
	public static final int DOWNTIME = KEY_BASE + 111;
	public static final int VIOLATED = KEY_BASE + 112;
	
	public static final int TOTAL_VM = KEY_BASE + 150;
	public static final int TOTAL_MIGRATED = KEY_BASE + 151;
	public static final int TOTAL_MIGRATED_PRIORITY = KEY_BASE + 152;
	public static final int PRIORITY_1 = KEY_BASE + 153;
	public static final int PRIORITY_2 = KEY_BASE + 154;
	public static final int PRIORITY_3 = KEY_BASE + 155;
	public static final int TOTAL = KEY_BASE + 156;
	public static final int MIGRATED = KEY_BASE + 157;
	
	public static final int TOTAL_VIOLATED = KEY_BASE + 160;
	public static final int AVERAGE = KEY_BASE + 170;
	
	
	/**
	 * Key name used for reading JSON input file
	 */
	private static final HashMap<Integer, String> inputKeyName;
	static{
		inputKeyName = new HashMap<Integer, String>();
		inputKeyName.put(ENVIRONMENT, "environment");
		inputKeyName.put(VM_SPEC_LIST, "vmSpecList");
		
		inputKeyName.put(MAX_BANDWIDTH, "maxBandwidth");
		inputKeyName.put(MEAN_BANDWIDTH, "meanBandwidth");
		inputKeyName.put(TIME_LIMIT, "timeLimit");
		inputKeyName.put(SCHEDULE_TYPE, "scheduleType");
		inputKeyName.put(MIGRATION_TYPE, "migrationType");
		inputKeyName.put(CONTROL_TYPE, "controlType");
		inputKeyName.put(NETWORK_TYPE, "networkType");
		inputKeyName.put(PAGE_SIZE, "pageSize");
		inputKeyName.put(WWS_RATIO, "wwsRatio");
		inputKeyName.put(WWS_DIRTY_RATE, "wwsDirtyRate");
		inputKeyName.put(NORMAL_DIRTY_RATE, "normalDirtyRate");
		inputKeyName.put(MAX_PRECOPY_ROUND, "maxPreCopyRound");
		inputKeyName.put(MIN_DIRTY_PAGE, "minDirtyPage");
		inputKeyName.put(MAX_NO_PROGRESS, "maxNoProgressRound");
		inputKeyName.put(NETWORK_INTERVAL, "networkInterval");
		inputKeyName.put(NETWORK_SD, "networkSD");
		inputKeyName.put(VM_AMOUNT, "vmAmount");
		inputKeyName.put(RAM, "ram");
		inputKeyName.put(PRIORITY, "priority");
		inputKeyName.put(QOS, "qos");
	}	
	
	private static final HashMap<Integer, String> outputKeyName;
	static{
		outputKeyName = new HashMap<Integer, String>();
		outputKeyName.put(MIGRATED_VM, "migratedVm");
		outputKeyName.put(OVERALL, "overall");
		
		outputKeyName.put(MIGRATION_TIME, "migrationTime");
		outputKeyName.put(DOWNTIME, "downtime");
		outputKeyName.put(VIOLATED, "violated");

		outputKeyName.put(TOTAL_VM, "totalVm");
		outputKeyName.put(TOTAL_MIGRATED, "totalMigrated");
		outputKeyName.put(TOTAL_MIGRATED_PRIORITY, "totalMigratedPriority");
		outputKeyName.put(PRIORITY_1, "priority1");
		outputKeyName.put(PRIORITY_2, "priority2");
		outputKeyName.put(PRIORITY_3, "priority3");
		outputKeyName.put(TOTAL, "total");
		outputKeyName.put(MIGRATED, "migrated");
		outputKeyName.put(TOTAL_VIOLATED, "totalViolated");
		outputKeyName.put(AVERAGE, "average");
	}
	
	public static String getJSONInputKeyName(int code){
		return inputKeyName.get(code);
	}
	
	public static String getJSONOutputKeyName(int code){
		return outputKeyName.get(code);
	}
}
