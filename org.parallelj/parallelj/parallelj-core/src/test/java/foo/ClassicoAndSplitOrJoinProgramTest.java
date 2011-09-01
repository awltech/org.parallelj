package foo;

import org.junit.Assert;
import org.junit.Test;
import org.parallelj.Programs;
import org.parallelj.Programs.ProcessHelper;

public class ClassicoAndSplitOrJoinProgramTest {

	@Test
	public void classico() {
		ProcessHelper<ClassicoAndSplitOrJoinProgram> instance = Programs.as(new ClassicoAndSplitOrJoinProgram());
		Assert.assertNotNull(instance);
		instance.execute();
		Assert.assertEquals(instance.context().a, 1);
		Assert.assertEquals(instance.context().b, 1);
		Assert.assertEquals(instance.context().c, 1);
		Assert.assertEquals(instance.context().d, 1);
	}

}
