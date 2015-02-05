package parser;

import java.io.FileNotFoundException;
import java.io.FileReader;

import org.json.*;

import container.Parameters;
import container.VmSpec;
import variable.Constant;

public class JsonDecoder {
	
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
		JSONObject environment = mainObj.getJSONObject(Constant.getJSONKeyName(Constant.ENVIRONMENT));
		JSONArray vmSpecList = mainObj.getJSONArray(Constant.getJSONKeyName(Constant.VM_SPEC_LIST));
		
		readEnvironment(param, environment);
		readVmSpecList(param, vmSpecList);
	}
	
	private void readEnvironment(Parameters param, JSONObject environment){
		double timeLimit = environment.getDouble(Constant.getJSONKeyName(Constant.TIME_LIMIT));
		double bandwidth = environment.getDouble(Constant.getJSONKeyName(Constant.BANDWIDTH));
		String scheduleType = environment.getString(Constant.getJSONKeyName(Constant.SCHEDULE_TYPE));
		String migrationType = environment.getString(Constant.getJSONKeyName(Constant.MIGRATION_TYPE));
		String controlType = environment.getString(Constant.getJSONKeyName(Constant.CONTROL_TYPE));
		String networkType = environment.getString(Constant.getJSONKeyName(Constant.NETWORK_TYPE));
		int pageSizeKB = environment.getInt(Constant.getJSONKeyName(Constant.PAGE_SIZE));
		
		double wwsPageRatio = environment.getDouble(Constant.getJSONKeyName(Constant.WWS_RATIO));
		double wwsDirtyRate = environment.getDouble(Constant.getJSONKeyName(Constant.WWS_DIRTY_RATE));
		double normalDirtyRate = environment.getDouble(Constant.getJSONKeyName(Constant.NORMAL_DIRTY_RATE));
		int maxPreCopyRound = environment.getInt(Constant.getJSONKeyName(Constant.MAX_PRECOPY_ROUND));
		int minDirtyPage = environment.getInt(Constant.getJSONKeyName(Constant.MIN_DIRTY_PAGE));
		int maxNoProgressRound = environment.getInt(Constant.getJSONKeyName(Constant.MAX_NO_PROGRESS));
		
		double networkInterval = environment.getDouble(Constant.getJSONKeyName(Constant.NETWORK_INTERVAL));
		double networkSD = environment.getDouble(Constant.getJSONKeyName(Constant.NETWORK_SD));

		//param.setVmRam(1000);
		//param.setScenarioType(2);
		param.setBandwidth(bandwidth);
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
			int vmAmount = spec.getInt(Constant.getJSONKeyName(Constant.VM_AMOUNT));
			int ram = spec.getInt(Constant.getJSONKeyName(Constant.RAM));
			int priority = spec.getInt(Constant.getJSONKeyName(Constant.PRIORITY));
			int qos = spec.getInt(Constant.getJSONKeyName(Constant.QOS));

			param.addVmSpec(new VmSpec(vmAmount, ram, priority, qos));
		}
	}
}
