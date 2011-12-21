package org.parallelj.tracknrestart.test.quartz.pjj;


public class LaunchTest extends AbstractTest {
	
	@Override
	String getProgramQN() {
		return "org.parallelj.tracknrestart.test.quartz.pjj.flow.Prog1";
	}

	public static void main(String[] args) {
		LaunchTest lt = new LaunchTest();
		lt.test1();
	}

	
}
