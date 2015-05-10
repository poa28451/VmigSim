package closed_loop;

import variable.FilePathManager;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.rule.Variable;

public class FuzzyLogic {
	private String filename;
	private FIS fis;
	private double prevError = 0;
	private double prevAction = 1;
	
	public FuzzyLogic(){
		filename = FilePathManager.getFuzzyRuleFilePath();
		fis = FIS.load(filename);
		handleFindNotFound(fis, filename);
	}
	
	/**
	 * Evaluate the result of the controller and return it in form of thread number.
	 * @param errorPercent the error percentage, i.e. the proportion of the difference between time limitation and predicted migration time.
	 * @param currentThreadNum current number of the thread using in the migration.
	 * @return the number of thread that should be used in the migration.
	 */
	public int evaluateResult(double errorPercent, int currentThreadNum){
		fis.setVariable("status", errorPercent);
		fis.setVariable("delta_status", errorPercent - prevError);
		fis.evaluate();
		
		Variable var = fis.getVariable("delta_n_multiplier");
		int threadResult = adaptThreadNum(var.getValue(), errorPercent, currentThreadNum);
		return threadResult;
	}
	
	/**
	 * Adapt the thread number according to fuzzy's result.
	 * @param fuzzyResult the result of fuzzy logic.
	 * @param currentThreadNum the thread number currently using in the migration.
	 * @return the number of thread that should be used in the migration.
	 */
	private int adaptThreadNum(double fuzzyResult, double currentError, int currentThreadNum){
		int newThreadNum = currentThreadNum;
		double multiplier = 2;
		double action = fuzzyResult * prevAction;
		
		if(action > 0) newThreadNum = (int) (currentThreadNum * multiplier);
		else if(action < 0) newThreadNum = (int) (currentThreadNum / multiplier);
		
		prevAction = action;
		prevError = currentError;
		return checkThreadValue(newThreadNum);
	}
	
	/**
	 * Keep the thread number stay in the range.
	 * @param threadNum thread number being checked.
	 * @return the thread number that is checked with the value boundary.
	 */
	private int checkThreadValue(int threadNum){
		int maxThread = 64;
		int minThread = 1;
		if(threadNum > maxThread) threadNum = maxThread;
		if(threadNum < minThread) threadNum = minThread;
		return threadNum;
	}
	
	/**
	 * Check if the fuzzy rule file is existed. If not, shut the system down.
	 * @param fis fuzzy variable.
	 * @param filename fuzzy rule filename that is being used.
	 */
	private void handleFindNotFound(FIS fis, String filename){
		if(fis == null){
			System.out.println("Fuzzy rule file: '" + filename + "' not found");
			System.exit(1);
		}
	}
	
	//System.out.println("differr: " + (currentError - prevError) + " newerr: " + currentError + " olderr: " + prevError + " action: " + action + "/" + Math.ceil(action) + " fuzzRes: " + fuzz + " prevAct: " + prevAction);
}
