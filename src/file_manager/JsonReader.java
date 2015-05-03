package file_manager;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import org.json.*;

import container.Parameters;
import container.VmSpec;
import variable.Constant;
import variable.Environment;
import variable.JsonKeyName;

public class JsonReader {
	
	public Parameters readInputFile(String filepath){
		Parameters param = new Parameters();
		try {
			JSONTokener tok = new JSONTokener(new FileReader(filepath));
			JSONObject mainObj = new JSONObject(tok);
			
			readSections(param, mainObj);
			return param;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void readSections(Parameters param, JSONObject mainObj){
		JSONObject environment = mainObj.getJSONObject(JsonKeyName.getJSONInputKeyName(JsonKeyName.ENVIRONMENT));
		JSONArray vmSpecList = mainObj.getJSONArray(JsonKeyName.getJSONInputKeyName(JsonKeyName.VM_SPEC_LIST));
		
		readEnvironment(param, environment);
		readVmSpecList(param, vmSpecList);
	}
	
	private void readEnvironment(Parameters param, JSONObject environment){
		double timeLimit = environment.getDouble(JsonKeyName.getJSONInputKeyName(JsonKeyName.TIME_LIMIT));
		String scheduleType = environment.getString(JsonKeyName.getJSONInputKeyName(JsonKeyName.SCHEDULE_TYPE));
		String migrationType = environment.getString(JsonKeyName.getJSONInputKeyName(JsonKeyName.MIGRATION_TYPE));
		String controlType = environment.getString(JsonKeyName.getJSONInputKeyName(JsonKeyName.CONTROL_TYPE));
		
		double wwsPageRatio = environment.getDouble(JsonKeyName.getJSONInputKeyName(JsonKeyName.WWS_PERCENTAGE));
		double wwsDirtyRate = environment.getDouble(JsonKeyName.getJSONInputKeyName(JsonKeyName.WWS_DIRTY_RATE));
		double normalDirtyRate = environment.getDouble(JsonKeyName.getJSONInputKeyName(JsonKeyName.NORMAL_DIRTY_RATE));
		int maxPreCopyRound = environment.getInt(JsonKeyName.getJSONInputKeyName(JsonKeyName.MAX_PRECOPY_ROUND));
		int minDirtyPage = environment.getInt(JsonKeyName.getJSONInputKeyName(JsonKeyName.MIN_DIRTY_PAGE));
		int maxNoProgressRound = environment.getInt(JsonKeyName.getJSONInputKeyName(JsonKeyName.MAX_NO_PROGRESS));

		boolean isRecordedTrace = environment.getBoolean(JsonKeyName.getJSONInputKeyName(JsonKeyName.IS_RECORDED_TRACE));
		String traceFile = environment.getString(JsonKeyName.getJSONInputKeyName(JsonKeyName.TRACE_FILE));
		String networkType = environment.getString(JsonKeyName.getJSONInputKeyName(JsonKeyName.NETWORK_TYPE));
		double networkInterval = environment.getDouble(JsonKeyName.getJSONInputKeyName(JsonKeyName.NETWORK_INTERVAL));
		double maxBandwidth = environment.getDouble(JsonKeyName.getJSONInputKeyName(JsonKeyName.MAX_BANDWIDTH));
		double meanBandwidth = environment.getDouble(JsonKeyName.getJSONInputKeyName(JsonKeyName.MEAN_BANDWIDTH));
		double networkSD = environment.getDouble(JsonKeyName.getJSONInputKeyName(JsonKeyName.NETWORK_SD));
		
		int threadNum = environment.getInt(JsonKeyName.getJSONInputKeyName(JsonKeyName.THREAD_NUM));
		
		Environment.setMigrationTimeLimit(timeLimit);
		Environment.setScheduleType(Constant.getScheduleKeyCode(scheduleType));
		Environment.setMigrationType(Constant.getMigrationKeyCode(migrationType));
		Environment.setControlType(Constant.getControlKeyCode(controlType));
		Environment.setNetworkType(Constant.getNetworkKeyCode(networkType));
	
		param.setMaxBandwidth(maxBandwidth);
		param.setMeanBandwidth(meanBandwidth);
		param.setNetworkInterval(networkInterval);
		param.setNetworkSD(networkSD);
		
		Environment.setWwsPageRatio(wwsPageRatio);
		Environment.setWwsDirtyRate(wwsDirtyRate);
		Environment.setNormalDirtyRate(normalDirtyRate);
		Environment.setMaxPreCopyRound(maxPreCopyRound);
		Environment.setMinDirtyPage(minDirtyPage);
		Environment.setMaxNoProgressRound(maxNoProgressRound);
		
		Environment.setThreadNum(threadNum);
		Environment.setIsRecordedTrace(isRecordedTrace);
		Environment.setTraceFile(traceFile);
	}
	
	private void readVmSpecList(Parameters param, JSONArray vmSpecList){
		ArrayList<VmSpec> specList = new ArrayList<VmSpec>();
		for(int i=0; i<vmSpecList.length(); i++){
			JSONObject spec = vmSpecList.getJSONObject(i);
			int vmAmount = spec.getInt(JsonKeyName.getJSONInputKeyName(JsonKeyName.VM_AMOUNT));
			int ram = spec.getInt(JsonKeyName.getJSONInputKeyName(JsonKeyName.RAM));
			int priority = spec.getInt(JsonKeyName.getJSONInputKeyName(JsonKeyName.PRIORITY));
			int qos = spec.getInt(JsonKeyName.getJSONInputKeyName(JsonKeyName.QOS));

			specList.add(new VmSpec(vmAmount, ram, priority, qos));
		}
		

		Environment.setVmSpecList(specList);
	}
}
