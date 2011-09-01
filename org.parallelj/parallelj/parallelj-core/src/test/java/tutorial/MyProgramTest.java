package tutorial;

import junit.framework.Assert;

import org.junit.Test;
import org.parallelj.Programs;
import org.parallelj.Programs.ProcessHelper;

public class MyProgramTest {

	@Test
	public void test() {
		MyProgram program = new MyProgram();
		ProcessHelper<MyProgram> process = Programs.as(program);
		process.execute();

		Assert.assertTrue(program.called);

	}

}
