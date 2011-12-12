package org.parallelj.tracknrestart.test.quartz.pjj.flow;

import javax.annotation.Generated;
import org.parallelj.Program;
import java.util.Arrays;
import java.util.List;

import org.parallelj.Begin;
import org.parallelj.AndSplit;
import org.parallelj.AndJoin;
import org.parallelj.Capacity;
import org.parallelj.ForEach;
import org.parallelj.launching.QuartzExecution;

/**
 * Program tbr.groupid.jjp1.pack1.Prog1
 * Description :
 **/
@Generated("//J")
@QuartzExecution
@Program
public class Prog1 {
	/**
	 * data1 field
	 * Description :
	 **/
//	private List<String> data1 = Arrays.asList(new String[]{"a","B","c","0"});
//	private List<String> data1 = Arrays.asList(new String[]{"a","b","a"});
	private List<String> data1 = Arrays.asList(new String[]{});

	public List<String> getData1() {
		return data1;
	}

	public void setData1(List<String> data1) {
		this.data1 = data1;
	}

	/**
	 * Noop Procedure proc1. This procedure is not bound to an executable.
	 * Description :
	 * 
	 * @generated //J
	 */
	@Generated(value = "//J", comments = "3677788")
	@Begin
	@AndSplit({ "forEach4", "proc3" })
	public void proc1() {
		System.out.println("proc1");
	}

	/**
	 * Noop Procedure proc2. This procedure is not bound to an executable.
	 * Description :
	 * 
	 * @generated //J
	 */
	@Generated(value = "//J", comments = "3677788")
	@AndJoin
	@AndSplit({ "end" })
	public void proc2() {
		System.out.println("proc2");
	}

	/**
	 * Noop Procedure proc3. This procedure is not bound to an executable.
	 * Description :
	 * 
	 * @generated //J
	 */
	@Generated(value = "//J", comments = "3677788")
	@AndJoin
	@AndSplit({ "proc2" })
	public void proc3() {
		System.out.println("proc3");
	}

	/**
	 * Entry method of procedure forEach4. This procedure is bound to
	 * tbr.groupid.jjp1.pack1.Prog2 Description :
	 * 
	 * @generated //J
	 */
	@Generated(value = "//J", comments = "-1696000620")
	@AndJoin
	@Capacity(1)
	public Prog2 forEach4(@ForEach("data1") String val) {
		System.out.println("entry forEach4(Prog2)");
		Prog2 prog2 = new Prog2();
		prog2.prog2DataIn = val;
		return prog2;
	}

	/**
	 * Exit method of procedure forEach4. This procedure is bound to
	 * tbr.groupid.jjp1.pack1.Prog2 Description :
	 * 
	 * @generated //J
	 */
	@Generated(value = "//J", comments = "3677788")
	@AndSplit({ "proc2" })
	public void forEach4(Prog2 executable) {
		System.out.println("exit forEach4(Prog2)");
	}


}
