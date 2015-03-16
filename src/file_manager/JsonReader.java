package file_manager;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.json.*;

import container.Parameters;
import container.VmSpec;
import variable.Constant;
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
		double maxBandwidth = environment.getDouble(JsonKeyName.getJSONInputKeyName(JsonKeyName.MAX_BANDWIDTH));
		double meanBandwidth = environment.getDouble(JsonKeyName.getJSONInputKeyName(JsonKeyName.MEAN_BANDWIDTH));
		String scheduleType = environment.getString(JsonKeyName.getJSONInputKeyName(JsonKeyName.SCHEDULE_TYPE));
		String migrationType = environment.getString(JsonKeyName.getJSONInputKeyName(JsonKeyName.MIGRATION_TYPE));
		String controlType = environment.getString(JsonKeyName.getJSONInputKeyName(JsonKeyName.CONTROL_TYPE));
		String networkType = environment.getString(JsonKeyName.getJSONInputKeyName(JsonKeyName.NETWORK_TYPE));
		int pageSizeKB = environment.getInt(JsonKeyName.getJSONInputKeyName(JsonKeyName.PAGE_SIZE));
		
		double wwsPageRatio = environment.getDouble(JsonKeyName.getJSONInputKeyName(JsonKeyName.WWS_PERCENTAGE));
		double wwsDirtyRate = environment.getDouble(JsonKeyName.getJSONInputKeyName(JsonKeyName.WWS_DIRTY_RATE));
		double normalDirtyRate = environment.getDouble(JsonKeyName.getJSONInputKeyName(JsonKeyName.NORMAL_DIRTY_RATE));
		int maxPreCopyRound = environment.getInt(JsonKeyName.getJSONInputKeyName(JsonKeyName.MAX_PRECOPY_ROUND));
		int minDirtyPage = environment.getInt(JsonKeyName.getJSONInputKeyName(JsonKeyName.MIN_DIRTY_PAGE));
		int maxNoProgressRound = environment.getInt(JsonKeyName.getJSONInputKeyName(JsonKeyName.MAX_NO_PROGRESS));
		
		double networkInterval = environment.getDouble(JsonKeyName.getJSONInputKeyName(JsonKeyName.NETWORK_INTERVAL));
		double networkSD = environment.getDouble(JsonKeyName.getJSONInputKeyName(JsonKeyName.NETWORK_SD));

		param.setMaxBandwidth(maxBandwidth);
		param.setMeanBandwidth(meanBandwidth);
		param.setTimeLimit(timeLimit);
		param.setScheduleType(Constant.getScheduleKeyCode(scheduleType));
		param.setMigrationType(Constant.getMigrationKeyCode(migrationType));
		param.setControlType(Constant.getControlKeyCode(controlType));
		param.setNetworkType(Constant.getNetworkKeyCode(networkType));
		param.setPageSizeKB(pageSizeKB);
		
		
		param.setWwsPageRatio(wwsPageRatio);
		param.setWwsDirtyRate(wwsDirtyRate);
		param.setNormalDirtyRate(normalDirtyRate);
		param.setMaxPreCopyRound(maxPreCopyRound);
		param.setMinDirtyPage(minDirtyPage);
		param.setMaxNoProgressRound(maxNoProgressRound);
		param.setNetworkInterval(networkInterval);
		param.setNetworkSD(networkSD);
	}
	
	private void readVmSpecList(Parameters param, JSONArray vmSpecList){
		for(int i=0; i<vmSpecList.length(); i++){
			JSONObject spec = vmSpecList.getJSONObject(i);
			int vmAmount = spec.getInt(JsonKeyName.getJSONInputKeyName(JsonKeyName.VM_AMOUNT));
			int ram = spec.getInt(JsonKeyName.getJSONInputKeyName(JsonKeyName.RAM));
			int priority = spec.getInt(JsonKeyName.getJSONInputKeyName(JsonKeyName.PRIORITY));
			int qos = spec.getInt(JsonKeyName.getJSONInputKeyName(JsonKeyName.QOS));

			param.addVmSpec(new VmSpec(vmAmount, ram, priority, qos));
		}
	}
}
