package org.parallelj.tracknrestart.test.quartz.pjj.flow;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;
import org.parallelj.Program;
import org.parallelj.Begin;
import org.parallelj.AndSplit;
import org.parallelj.launching.QuartzExecution;
import org.parallelj.tracknrestart.databinding.Out;

/**
 * Program sampleprog.MyProg2
 * Description :
 **/
@Generated("//J")
@Program
public class SimpleProcedure {
	
	String data1 = null;

	/**
	 * Entry method of procedure myprc.
	 * This procedure is bound to sampleprog.MyProg3
	 * Description :
	 * 
	 * @generated //J
	 */
	@Generated(value = "//J", comments = "1034177481")
	@Begin
	public Prog2 myProcedure() {
		// TODO : to be implemented
		Prog2 p =  new Prog2();
		p.prog2DataIn=data1;
		return p;
	}

	/**
	 * Exit method of procedure myprc.
	 * This procedure is bound to sampleprog.MyProg3
	 * Description :
	 * 
	 * @generated //J
	 */
	@Generated(value = "//J", comments = "3677788")
	@AndSplit({ "end" })
	public void myProcedure(Prog2 executable) {
		// TODO : to be implemented
	}
}
