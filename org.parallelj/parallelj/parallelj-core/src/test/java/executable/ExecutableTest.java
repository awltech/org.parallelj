package executable;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.parallelj.Executables;
import org.parallelj.Programs;

public class ExecutableTest {

	@Test
	public void executable() {
		MyExecutable executable = new MyExecutable();
		executable.name = "name";

		Map<String, String> map = Executables.attributes(executable);
		Assert.assertTrue(map.containsKey("name"));
	}

	@Test
	public void program() {
		MyProgram program = new MyProgram();
		program.name = "name";

		Map<String, String> map = Executables.attributes(program);
		Assert.assertTrue(map.containsKey("name"));
	}
	
	@Test
	public void run() {
		MyProgram program = new MyProgram();
		program.name = "name";

		Programs.as(program).execute();
	}
	
	
	public void empty() {
		Assert.assertTrue(Executables.attributes(new Object()).isEmpty());
	}

}
