package broker_collaborator;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import variable.Constant;
import variable.Environment;

public class NetworkGenerator {
	private static double maxBandwidth;
	private static double meanBandwidth;
	private static double networkInterval;
	private static ArrayList<ArrayList<Double>> bwTrace;
	private static double stddevPercent;
	
	public NetworkGenerator(double maxBandwidth, double meanBandwidth, double networkInterval, double networkSD){
		setMaxBandwidth(maxBandwidth);
		setMeanBandwidth(meanBandwidth);
		setBwTrace(new ArrayList<ArrayList<Double>>(), Environment.threadNum);
		setNetworkInterval(networkInterval);

		if(Environment.networkType == Constant.STATIC){
			//If it's a static network, use default network STDDEV
			setStddevPercent(Constant.STATIC_NETWORK_SD_PERCENTAGE);
		}
		else{
			setStddevPercent(networkSD);
		}
		
		generateBandwidth();
	}
	
	public NetworkGenerator(String inputFile){
		setBwTrace(new ArrayList<ArrayList<Double>>(), Environment.threadNum);
		
		Scanner scan;
		try {
			scan = new Scanner(new FileReader(inputFile));
			while(scan.hasNextDouble()){
				for(int i=0; i<Environment.threadNum; i++){
					getBwTrace().get(i).add(scan.nextDouble());
				}
			}
			scan.close();
			
			double networkInterval = Environment.migrationTimeLimit / getBwTrace().get(0).size();
			setNetworkInterval(networkInterval);			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public NetworkGenerator(String inputFile, int threadNum){
		setBwTrace(new ArrayList<ArrayList<Double>>(), threadNum);
		
		Scanner scan;
		try {
			scan = new Scanner(new FileReader(inputFile));
			while(scan.hasNextDouble()){
				for(int i=0; i<threadNum; i++){
					getBwTrace().get(i).add(scan.nextDouble());
				}
			}
			scan.close();
			
			double networkInterval = Environment.migrationTimeLimit / getBwTrace().get(0).size();
			setNetworkInterval(networkInterval);			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void generateBandwidth(){
		double intervalAmount = getIntervalAmount();
		
		Random ran = new Random();
		double deviation;
		double bw;
		
		for(int i=0; i<intervalAmount; i++){
			for(int j=0; j<Environment.threadNum; j++){
				deviation = randomDeviation(ran);
				bw = getMeanBandwidth() + deviation;
				getBwTrace().get(j).add(bw);
			}
		}
	}
	
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
			deviation = chance * ((stddevPercent / 100) * getMaxBandwidth());
		}
		while(!checkIfBandwidthInRange(deviation));
		return deviation;
	}
	
	private boolean checkIfBandwidthInRange(double deviation){
		//Cannot be neither more than max B/W nor less than 0
		if(deviation + getMeanBandwidth() > getMaxBandwidth()){
			return false;
		}
		else if(deviation + getMeanBandwidth() < 0){
			return false;
		}
		return true;
	}
	
	/**
	 * Get bandwidth at the specific time. This function will convert the time into the interval number
	 * and use it to retrieve the bandwidth trace.
	 * @param time The time to get a bandwidth.
	 * @return bandwidth in KBps.
	 */
	public static double getBandwidthAtTimeKB(int threadNum, double time){
		return getBwTrace().get(threadNum).get(calculateIntervalNum(time)) * Constant.KILO_BYTE / 8;
	}
	
	/**
	 * Get bandwidth at the specific time. This function will convert the time into the interval number
	 * and use it to retrieve the bandwidth trace.
	 * @param time The time to get a bandwidth.
	 * @return bandwidth in MBps.
	 */
	public static double getBandwidthAtTimeMB(int threadNum, double time){
		return getBwTrace().get(threadNum).get(calculateIntervalNum(time)) / 8;
	}
	
	/**
	 * Find the interval position of the current clock
	 * @param clock Current clock of the simulation
	 * @return the position of the current interval (range 0 to ceil(time limit / interval))
	 */
	private static int calculateIntervalNum(double clock){		
		double num = 0;
		double interval = networkInterval;
		
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
		double nextIntervalClock = 0;
		double intervalFraction = 0;
		double timeLimit = Environment.migrationTimeLimit;
		double interval = networkInterval;
		//If the entire interval has still not reached the time limit yet,
		//	find the fraction normally (time left until reaches the next interval)
		//	stop of the current interval = start of the next interval
		if(clock + interval <= timeLimit){
			int nextIntervalNum = calculateIntervalNum(clock) + 1;
			nextIntervalClock = new BigDecimal((nextIntervalNum * interval) + Constant.START_INTERVAL)
				.setScale(Constant.DECIMAL_SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		//If the interval has reached the time limit already,
		//	find the fraction by time limit (time left until reaches the time limit)
		//	stop of the current interval = time limit
		else{
			nextIntervalClock = timeLimit;
		}
		
		//Find the leftover time of current interval
		intervalFraction = new BigDecimal(nextIntervalClock - clock)
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
		double amount = Environment.migrationTimeLimit / networkInterval;
		//If there is decimal of interval, always add 1 additional interval to the list
		//Ex. if intervalNum is 15.5, the total interval is 16.
		return Math.ceil(amount);
	}
	
	public static double getMaxBandwidth() {
		return maxBandwidth;
	}
	
	public void setMaxBandwidth(double originalBandwidth) {
		NetworkGenerator.maxBandwidth = originalBandwidth;
	}

	public static double getMeanBandwidth() {
		return meanBandwidth;
	}

	public static void setMeanBandwidth(double meanBandwidth) {
		NetworkGenerator.meanBandwidth = meanBandwidth;
	}

	public static ArrayList<ArrayList<Double>> getBwTrace() {
		return bwTrace;
	}

	protected void setBwTrace(ArrayList<ArrayList<Double>> bwTrace, int threadNum) {
		for(int i=0; i<threadNum; i++){
			bwTrace.add(new ArrayList<Double>());
		}
		NetworkGenerator.bwTrace = bwTrace;
	}

	public static double getStddevPercent() {
		return stddevPercent;
	}

	private void setStddevPercent(double stddevPercent) {
		NetworkGenerator.stddevPercent = stddevPercent;
	}

	public static double getNetworkInterval() {
		return networkInterval;
	}

	private void setNetworkInterval(double networkInterval) {
		NetworkGenerator.networkInterval = networkInterval;
	}
}
