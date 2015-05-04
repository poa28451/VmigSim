package file_manager;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.json.JSONObject;

import cloudsim_inherit.VmigSimVm;
import variable.FilePathManager;
import variable.JsonKeyName;
import container.MigrationResults;

public class ResultWriter {
	private static MigrationResults migResult;
	
	public static void writeResultToFile(MigrationResults result){
		migResult = result;
		String filePath = FilePathManager.getResultFilePath();
		PrintWriter writer;
		try {
			writer = new PrintWriter(filePath, "UTF-8");
			JsonWriter(writer);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	private static void JsonWriter(PrintWriter writer){
		JSONObject mainObject = new JSONObject();
		writeVmDetails(mainObject);
		writeOverallDetails(mainObject);
		writer.write(mainObject.toString(4));
	}
	
	private static void writeVmDetails(JSONObject mainObject){
		JSONObject allMigrated = new JSONObject();
		
		for(VmigSimVm vm : migResult.getMigratedVm()){
			JSONObject vmDetail = new JSONObject();
			vmDetail.put(JsonKeyName.getJSONInputKeyName(JsonKeyName.RAM), vm.getRam());
			vmDetail.put(JsonKeyName.getJSONInputKeyName(JsonKeyName.PRIORITY), vm.getPriority());
			vmDetail.put(JsonKeyName.getJSONInputKeyName(JsonKeyName.QOS), vm.getQos());
			vmDetail.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.MIGRATION_TIME), vm.getMigrationTime());
			vmDetail.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.DOWNTIME), vm.getDowntime());
			vmDetail.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.VIOLATED), vm.isViolated());
			
			allMigrated.put(String.valueOf(vm.getId()), vmDetail);
		}
		
		mainObject.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.MIGRATED_VM), allMigrated);
	}
	
	private static void writeOverallDetails(JSONObject mainObject){
		JSONObject overall = new JSONObject();
		
		overall.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.TOTAL_VM), migResult.getTotalVm());
		overall.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.TOTAL_MIGRATED), migResult.getTotalMigratedVm());
		writeMigratedPriorityDetail(overall);
		
		overall.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.TOTAL_VIOLATED), migResult.getTotalViolatedVm());
		writeViolatedPriorityDetail(overall);
		
		overall.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.EXCESS_DOWNTIME), migResult.getAllAvgExcessDowntime());
		writeExcessDowntimePriorityDetail(overall);
		
		writeMigrationTimeDetail(overall);
		writeDowntimeDetail(overall);
		
		mainObject.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.OVERALL), overall);
	}
	
	private static void writeMigratedPriorityDetail(JSONObject overallObject){
		JSONObject priDetail = new JSONObject();
		JSONObject pri1 = new JSONObject();
		JSONObject pri2 = new JSONObject();
		JSONObject pri3 = new JSONObject();
		
		pri1.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.TOTAL), migResult.getTotalPriority1());
		pri1.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.MIGRATED), migResult.getMigratedPriority1());
		
		pri2.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.TOTAL), migResult.getTotalPriority2());
		pri2.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.MIGRATED), migResult.getMigratedPriority2());
		
		pri3.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.TOTAL), migResult.getTotalPriority3());
		pri3.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.MIGRATED), migResult.getMigratedPriority3());
		
		priDetail.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.PRIORITY_1), pri1);
		priDetail.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.PRIORITY_2), pri2);
		priDetail.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.PRIORITY_3), pri3);
		
		overallObject.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.TOTAL_MIGRATED_PRIORITY), priDetail);
	}
	
	private static void writeViolatedPriorityDetail(JSONObject overallObject){
		JSONObject priDetail = new JSONObject();
		
		priDetail.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.PRIORITY_1), migResult.getViolatedPriority1());
		priDetail.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.PRIORITY_2), migResult.getViolatedPriority2());
		priDetail.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.PRIORITY_3), migResult.getViolatedPriority3());
		
		overallObject.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.TOTAL_VIOLATED_PRIORITY), priDetail);
	}
	
	private static void writeExcessDowntimePriorityDetail(JSONObject overallObject){
		JSONObject excessDetail = new JSONObject();
		
		excessDetail.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.PRIORITY_1), migResult.getAvgExcessPriority1());
		excessDetail.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.PRIORITY_2), migResult.getAvgExcessPriority2());
		excessDetail.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.PRIORITY_3), migResult.getAvgExcessPriority3());
		
		overallObject.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.EXCESS_DOWNTIME_PRIORITY), excessDetail);
	}
	
	private static void writeMigrationTimeDetail(JSONObject overallObject){
		JSONObject migTime = new JSONObject();
		//double totalMigTime = migResult.getTotalMigrationTime();
		double totalMigTime = migResult.getRealMigTime();
		double avgMigTime = migResult.getAverageMigrationTime();
		
		migTime.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.TOTAL), totalMigTime);	
		migTime.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.AVERAGE), avgMigTime);
		
		overallObject.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.MIGRATION_TIME), migTime);
	}
	
	private static void writeDowntimeDetail(JSONObject overallObject){
		JSONObject downtime = new JSONObject();
		double totalDowntime = migResult.getTotalDowntime();
		double avgDowntime = migResult.getAverageDownTime();

		
		downtime.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.TOTAL), totalDowntime);	
		downtime.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.AVERAGE), avgDowntime);
		
		overallObject.put(JsonKeyName.getJSONOutputKeyName(JsonKeyName.DOWNTIME), downtime);
	}
}
