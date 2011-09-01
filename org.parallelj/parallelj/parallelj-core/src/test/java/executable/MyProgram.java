package executable;

import org.parallelj.AndSplit;
import org.parallelj.Attribute;
import org.parallelj.Begin;
import org.parallelj.Program;

@Program
public class MyProgram {
	
	@Attribute
	String name;
	
	@Attribute
	String value = "a complex string with special char &;<>";
	
	@Begin
	public MyExecutable run() {
		MyExecutable executable = new MyExecutable();
		executable.name = "run " + this;
		return executable;
	}
	
	@AndSplit("end")
	public void run(MyExecutable executable) {
		
	}

}
