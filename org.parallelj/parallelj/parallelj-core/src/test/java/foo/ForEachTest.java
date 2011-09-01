package foo;

import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.parallelj.Programs;
import org.parallelj.Programs.ProcessHelper;

public class ForEachTest {

	static Logger logger = Logger.getRootLogger();

	@Test
	public void foreach() throws Exception {
		ProcessHelper<ForEachProgram> instance = Programs.as(new ForEachProgram());
		Assert.assertNotNull(instance);
		instance.execute(Executors.newCachedThreadPool());
		instance.join();
		logger.info("completed");
	}

}
