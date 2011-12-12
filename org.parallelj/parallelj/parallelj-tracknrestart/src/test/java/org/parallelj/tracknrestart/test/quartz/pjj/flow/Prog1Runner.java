package org.parallelj.tracknrestart.test.quartz.pjj.flow;

import java.util.List;
import java.util.concurrent.Executors;

import org.parallelj.Programs;

public class Prog1Runner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Prog1 prog1 = new Prog1();
		List<String> data1 = prog1.getData1();
		data1.add("a");
		data1.add("b");
		data1.add("c");
		Programs.as(prog1).execute(Executors.newCachedThreadPool()).join();
	}

}
