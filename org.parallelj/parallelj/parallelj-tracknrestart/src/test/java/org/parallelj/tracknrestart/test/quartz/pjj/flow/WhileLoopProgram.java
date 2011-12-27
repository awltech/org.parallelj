package org.parallelj.tracknrestart.test.quartz.pjj.flow;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Generated;

import org.parallelj.AndJoin;
import org.parallelj.AndSplit;
import org.parallelj.Begin;
import org.parallelj.Capacity;
import org.parallelj.Program;
import org.parallelj.While;
import org.parallelj.launching.In;
import org.parallelj.launching.QuartzExecution;
import org.parallelj.tracknrestart.databinding.Out;
import org.parallelj.tracknrestart.test.quartz.pjj.parsers.ListStringParser;

/**
 * Program net.sample.ProgramWhileLoop
 * Description :
 **/
@Generated("//J")
@Program
@QuartzExecution
public class WhileLoopProgram {
	@In(parser=ListStringParser.class)
	private List<String> data1 = Arrays.asList(new String[]{});

	@Out
	int processed = 0;

	Iterator<String> iterator = null;
	
	
	public List<String> getData1() {
		return data1;
	}

	public void setData1(List<String> data1) {
		this.data1 = data1;
	}
	
	
	@Generated(value = "//J", comments = "3677788")
	@Begin
	@AndSplit({ "whileLoopProcedure" })
	public void init() {
		System.out.println(" data1 = " + data1);
		iterator = data1.iterator();
	}

	@AndJoin
	@Generated(value = "//J", comments = "134010567")
	@While("again")
	@Capacity(1)
	public Prog2 whileLoopProcedure() {
		Prog2 prog2 = new Prog2();
		prog2.prog2DataIn = iterator.next();
		return prog2;
	}

	/**
	 * Exit method of procedure whileLoopProcedure. This procedure is bound to
	 * net.sample.ProgramSayHello Description :
	 * 
	 * @generated //J
	 */
	@Generated(value = "//J", comments = "3677788")
	@AndSplit({ "end" })
	public void whileLoopProcedure(Prog2 executable) {
		processed++;
	}

	/**
	 * Predicate again Description :
	 * 
	 * @generated //J
	 */
	@Generated(value = "//J", comments = "-1225371522")
	public boolean isAgain() {
		return iterator.hasNext();
	}

}

