package variable;

import java.util.HashMap;

public class ExperimentParameters {
	public static final int PAGESIZE_KB = 20;
	public static final int MIN_DIRTY_PAGE = 50;
	public static final int MAX_PRECOPY_ROUND = 30;
	public static final int MAX_NO_PROGRESS = 2;
	public static final double NORMAL_DIRTY_RATE = 20;
	public static final double WWS_RATE = 1;
	public static final double WWS_DIRTY_RATE = 90;
	
	public static final int COMMON_TIME_LIMIT = 21600;

	public static final int SCENE_A = 0, SCENE_B = 1, SCENE_C = 2;
	public static final int PRIORITY_1_AMOUNT = 200;
	public static final int PRIORITY_2_AMOUNT = 200;
	public static final int PRIORITY_3_AMOUNT = 200;
	public static final int PRIORITY_1_QOS = 300; //5 mins
	public static final int PRIORITY_2_QOS = 2700; //45 mins
	public static final int PRIORITY_3_QOS = 32400; //9 hours
	
	public static final double NETWORK_INTERVAL = 1; //every 1 second
	public static final double NETWORK_SD = 54.8222; //54.8222 of deviation
	
	public static final HashMap<Integer, String> sceneName;
	static{
		sceneName = new HashMap<Integer, String>();
		sceneName.put(ExperimentParameters.SCENE_A, "A");
		sceneName.put(ExperimentParameters.SCENE_B, "B");
		sceneName.put(ExperimentParameters.SCENE_C, "C");
	}
	
	
}
