package tutorial;

import org.parallelj.Begin;
import org.parallelj.Program;

@Program
public class MyProgram {

	boolean called;

	@Begin
	public void myProcedure() {
		this.called = true;
	}

}
