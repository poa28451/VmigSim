import static org.junit.Assert.*;

import org.junit.Test;

import broker_collaborator.NetworkGenerator;
import variable.Constant;
import variable.Environment;


public class NetworkGeneratorTest {

	@Test
	public void testNetworkGeneratorDoubleDoubleDoubleDoubleStatic() {
		Environment.setThreadNum(1);
		Environment.setNetworkType(Constant.STATIC);
		Environment.setMigrationTimeLimit(21600);
		
		new NetworkGenerator(64, 64, 1, 15);
		assertTrue(NetworkGenerator.getMaxBandwidth() == 64);
		assertTrue(NetworkGenerator.getMeanBandwidth() == 64);
		assertTrue(NetworkGenerator.getNetworkInterval() == 1);
		assertTrue(NetworkGenerator.getStddevPercent() == Constant.STATIC_NETWORK_SD_PERCENTAGE);
		assertTrue(NetworkGenerator.getBwTrace().get(0).size() == 21600);
	}
	
	@Test
	public void testNetworkGeneratorDoubleDoubleDoubleDoubleDynamic() {
		Environment.setThreadNum(1);
		Environment.setNetworkType(Constant.DYNAMIC);
		Environment.setMigrationTimeLimit(21600);
		
		new NetworkGenerator(64, 64, 1, 15);
		assertTrue(NetworkGenerator.getMaxBandwidth() == 64);
		assertTrue(NetworkGenerator.getMeanBandwidth() == 64);
		assertTrue(NetworkGenerator.getNetworkInterval() == 1);
		assertTrue(NetworkGenerator.getStddevPercent() == 15);
		assertTrue(NetworkGenerator.getBwTrace().get(0).size() == 21600);
	}

	@Test
	public void testNetworkGeneratorString() {
		Environment.setThreadNum(1);
		Environment.setMigrationTimeLimit(7200);
		
		new NetworkGenerator("testcase-1.txt");
		assertTrue(NetworkGenerator.getBwTrace().get(0).size() == 7200);
		assertTrue(NetworkGenerator.getNetworkInterval() == 1);
	}

	@Test
	public void testNetworkGeneratorStringInt() {
		Environment.setMigrationTimeLimit(7200);
		
		new NetworkGenerator("testcase-1.txt", 1);
		assertTrue(NetworkGenerator.getBwTrace().size() == 1);
		assertTrue(NetworkGenerator.getBwTrace().get(0).size() == 7200);
		assertTrue(NetworkGenerator.getNetworkInterval() == 1);
	}

	@Test
	public void testGenerateBandwidth() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetBandwidthAtTimeKB() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetBandwidthAtTimeMB() {
		fail("Not yet implemented");
	}

	@Test
	public void testCalculateIntervalFraction() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetMaxBandwidth() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetMaxBandwidth() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetMeanBandwidth() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetMeanBandwidth() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetBwTrace() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetBwTrace() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetStddevPercent() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetNetworkInterval() {
		fail("Not yet implemented");
	}

}
