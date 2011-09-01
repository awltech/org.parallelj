package mirror;

import org.junit.Assert;
import org.junit.Test;
import org.parallelj.internal.kernel.KProcessor;
import org.parallelj.internal.kernel.KProgram;
import org.parallelj.internal.kernel.KReflection;

public class EventListenerImplTest {

	@Test
	public void checkInstance() {
		// force the load of listeners
		KReflection reflection = KReflection.getInstance();

		// check instance is loaded
		Assert.assertNotNull(EventListenerImpl.instance);
	}

	@Test
	public void checkCalled() {
		KProgram program = new KProgram();
		new KProcessor().execute(program.newProcess(null));
		Assert.assertTrue(EventListenerImpl.instance.called);
	}

}
