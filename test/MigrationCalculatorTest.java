import static org.junit.Assert.*;
import message.MigrationMessage;

import org.junit.Test;

import cloudsim_inherit.VmigSimVm;
import variable.Environment;
import broker_collaborator.MigrationCalculator;
import broker_collaborator.NetworkGenerator;


public class MigrationCalculatorTest {

	@Test
	public void testMigrationCalculator() {
		MigrationCalculator mig = new MigrationCalculator();
		assertEquals(0, mig.getThreadId());
	}

	@Test
	public void testMigrationCalculatorInt() {
		for(int i=0; i< 100; i++){
			MigrationCalculator mig = new MigrationCalculator(i);
			assertEquals(i, mig.getThreadId());
		}
	}

	@Test
	public void testCalculateMigrationTime() {
		int limit = 7200;
		
		Environment.setMigrationTimeLimit(limit);
		new NetworkGenerator("testcase-1.txt", 1);
		VmigSimVm vm = new VmigSimVm(0, 1, 1000, 1, 512, 1000, 1000, 300, 1, "", null);
		MigrationMessage msg = new MigrationMessage(vm);
		msg.setDataSizeKB(4096);
		MigrationCalculator mig = new MigrationCalculator();
		
		double time = mig.calculateMigrationTime(msg, 0.4, 0.1);
		System.out.println(time);
		assertTrue(time == ((1*0.6) + (3.4/4)));
	}

	/*@Test
	public void testUpdateMigrationTime() {
		VmigSimVm vm = new VmigSimVm(0, 1, 1000, 1, 512, 1000, 1000, 300, 1, "", null);
		MigrationMessage msg = new MigrationMessage(vm);
		msg.setDataSizeKB(4096);
		
		MigrationCalculator mig = new MigrationCalculator();		
		mig.updateMigrationTime(msg, 50);
		assertTrue(msg.getMigrationTime() == 50);
		System.out.println(msg.getVm().getMigrationTime());
		assertTrue(msg.getVm().getMigrationTime() == 50);
		assertTrue(msg.getVm().getDowntime() == 0);
	}

	@Test
	public void testUpdateMigrationTimeDowntime(){
		VmigSimVm vm = new VmigSimVm(0, 1, 1000, 1, 512, 1000, 1000, 300, 1, "", null);
		MigrationMessage msg = new MigrationMessage(vm);
		msg.setDataSizeKB(4096);
		
		MigrationCalculator mig = new MigrationCalculator();
		
		msg.setLastMigrationMsg(true);
		mig.updateMigrationTime(msg, 50);
		assertTrue(msg.getMigrationTime() == 50);
		assertTrue(msg.getVm().getMigrationTime() == 50);
		assertTrue(msg.getVm().getDowntime() == 50);
	}*/
}
