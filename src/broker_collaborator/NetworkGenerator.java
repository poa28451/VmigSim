package broker_collaborator;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;

import variable.Constant;
import variable.Environment;
import variable.FilePathContainer;

public class NetworkGenerator {
	private static double originalBandwidth;
	private static ArrayList<Double> bwTrace, allList = new ArrayList<>();
	private double stddevPercent;

	public NetworkGenerator(double originalBandwidth){
		setOriginalBandwidth(originalBandwidth);
		setBwTrace(new ArrayList<Double>());
		setStddevPercent(Environment.networkSD);
		generateBandwidth();
	}
	
	public void generateBandwidth(){
		double intervalAmount = getIntervalAmount();
		switch (Environment.networkType) {		
			case Constant.STATIC:
				generateStaticBandwidth(intervalAmount);
				break;
				
			case Constant.DYNAMIC:
				//Generate the dynamic b/w
				generateDynamicBandwidth(intervalAmount);
				break;
				
			default:
				generateStaticBandwidth(intervalAmount);
				break;
		}
		//Print the bandwidth output to the file
		printBwTraceToFile();
		/*printBwTraceToFile(FilePathContainer.networkPath + "network.txt");
		printAllBwTraceToFile(FilePathContainer.networkPath + "all.txt");*/
	}
	
	private void generateStaticBandwidth(double intervalAmount){
		for(int i=0; i<intervalAmount; i++){
			//Bandwidth always be the same as original
			double bw = getOriginalBandwidth();
			getBwTrace().add(bw);
		}
	}
	
	private void generateDynamicBandwidth(double intervalAmount){
		Random ran = new Random();
		for(int i=0; i<intervalAmount; i++){			
			double deviation = randomDeviation(ran);
			// Plus 1 for making the number into % (e.g. 120%)
			double bw = getOriginalBandwidth() - deviation;
			getBwTrace().add(bw);
			/*int bw;
			//do {
			  double val = (ran.nextGaussian() * 35) + 64;
			  bw = (int) Math.round(val);
			//} while ((bw < 0) || (bw > 64));
			getBwTrace().add((double)bw);*/
		}
	}
	
	/*private double randomChance(Random ran){
		double chance = 0;
		do{
			chance = ran.nextGaussian();
			if(chance > 0){
				chance = chance * -1;
			}
		} while (chance < -1);
		
		return chance;
	}*/
	
	/**
	 * Randomize the value of instability by using normal distribution
	 * and control the result for being in the [-1, 1] range
	 * @param ran Random object being used for randomization
	 * @return the value of instability
	 */
	private double randomDeviation(Random ran){
		double chance;
		double deviation = 0;

		do{
			chance = ran.nextGaussian();
			deviation = chance * ((stddevPercent / 100) * getOriginalBandwidth());
			allList.add(getOriginalBandwidth() - deviation);
		//Do the loop until we get the deviation that is less than the bandwidth
		}while(deviation > getOriginalBandwidth() || deviation < 0);
		return deviation;
	}
	
	public static double getBandwidthAtTime(double time){
		/*switch (Environment.networkType) {
			case Constant.DYNAMIC:
				//B/W is various by time on dynamic mode
				//
				return getBwTrace().get((int)Math.floor(time));
				
			default:
				//B/W is the same in any time on static mode
				return getOriginalBandwidth();
		}*/
		return getBwTrace().get(calculateIntervalNum(time));
	}
	
	/**
	 * Find the interval position of the current clock
	 * @param clock Current clock of the simulation
	 * @return the position of the current interval (range 0 to ceil(time limit / interval))
	 */
	private static int calculateIntervalNum(double clock){		
		double num = 0;
		double interval = Environment.networkInterval;
		
		//Use BigDecimal for the precisely calculation
		//startFraction indicates the fraction that is the interval joint
		double startFraction = new BigDecimal(Constant.START_INTERVAL / interval)
			.setScale(Constant.DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();
		//temp indicates the raw interval (have decimal) of current clock
		double temp = new BigDecimal(clock / interval)
			.setScale(Constant.DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();
		//tempFraction is the fraction of current clock
		double tempFraction = new BigDecimal(temp - Math.floor(temp))
		.setScale(Constant.DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();
		
		//if current clock's fraction is less than joint fraction,
		//then it's the previous interval, or else it's in the current interval
		//Ex. if the interval = 10 and start interval = 0.1
		//	that means the start of interval is every 10.1 (0.1-10.0, 10.1-20.0, 20.1-30.0)
		//	and the startFraction = 0.01 .
		//	And if current clock = 10.05 then temp = 10.05/10 = 1.005 and tempFraction = 1.005-1 = 0.005,
		//	the tempFraction is less then the startFraction,
		//	that means clock 10 is in the 1st interval (slot 0 of the list).
		if(tempFraction < startFraction){
			num = Math.floor(temp) - 1;
		}
		else{
			num = Math.floor(temp);
		}
		
		return (int) num;
	}
	
	/**
	 * Find the time left until changing the bandwidth interval
	 * @param clock Current clock of the simulation
	 * @return the time left (in seconds) before the interval changing
	 */
	public static double calculateIntervalFraction(double clock){
		double stopInterval = 0;
		double intervalFraction = 0;
		double timeLimit = Environment.migrationTimeLimit;
		double interval = Environment.networkInterval;
		//If the entire interval has still not reached the time limit yet,
		//	find the fraction normally (time left until reaches the next interval)
		//	stop of the current interval = start of the next interval
		if(clock + interval <= timeLimit){
			int nextIntervalNum = calculateIntervalNum(clock) + 1;
			stopInterval = new BigDecimal((nextIntervalNum * interval) + Constant.START_INTERVAL)
				.setScale(Constant.DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		//If the interval has reached the time limit already,
		//	find the fraction by time limit (time left until reaches the time limit)
		//	stop of the current interval = time limit
		else{
			stopInterval = timeLimit;
		}
		
		//Find the leftover time of current interval
		intervalFraction = new BigDecimal(stopInterval - clock)
			.setScale(Constant.DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();
		
		//If the double decimal issues (imprecise decimal) occurred,
		//	the leftover time will be 0.
		//So return the end-of-time value to notify the caller
		if(intervalFraction == 0){
			intervalFraction = Double.MIN_VALUE;
		}
		return intervalFraction;
	}
	
	private double getIntervalAmount(){
		double amount = Environment.migrationTimeLimit / Environment.networkInterval;
		//If there is decimal of interval, always add 1 additional interval to the list
		//Ex. if intervalNum is 15.5, the total interval is 16.
		return Math.ceil(amount);
	}
	
	private void printBwTraceToFile(){
		String filePath = FilePathContainer.getNetworkFilePath();
		PrintWriter writer;
		try {
			writer = new PrintWriter(filePath, "UTF-8");
			
			for(int i=0; i<getBwTrace().size(); i++){
				double bw = getBwTrace().get(i);
				//writer.println("Time " + (i+0.1) + "-" + (i+1) + ": " + bw);
				writer.println(bw);
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/*private void printAllBwTraceToFile(String filePath){
		PrintWriter writer;
		try {
			writer = new PrintWriter(filePath, "UTF-8");
			
			for(int i=0; i<allList.size(); i++){
				double bw = allList.get(i);
				//writer.println("Time " + (i+0.1) + "-" + (i+1) + ": " + bw);
				writer.println(bw);
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}*/
	
	public static double getOriginalBandwidth() {
		return originalBandwidth;
	}

	public void setOriginalBandwidth(double originalBandwidth) {
		NetworkGenerator.originalBandwidth = originalBandwidth;
	}

	protected void setBwTrace(ArrayList<Double> bwTrace) {
		NetworkGenerator.bwTrace = bwTrace;
	}

	public static ArrayList<Double> getBwTrace() {
		return bwTrace;
	}

	public void setStddevPercent(double stddevPercent) {
		this.stddevPercent = stddevPercent;
	}
	
	
}
