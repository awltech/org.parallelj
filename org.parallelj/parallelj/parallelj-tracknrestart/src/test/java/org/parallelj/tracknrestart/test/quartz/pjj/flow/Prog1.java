/*
 *     ParallelJ, framework for parallel computing
 *
 *     Copyright (C) 2010, 2011, 2012 Atos Worldline or third-party contributors as
 *     indicated by the @author tags or express copyright attribution
 *     statements applied by the authors.
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
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
import org.parallelj.launching.In;
import org.parallelj.launching.QuartzExecution;
//import org.parallelj.tracknrestart.databinding.In;
import org.parallelj.tracknrestart.databinding.Out;

import org.parallelj.tracknrestart.test.quartz.pjj.parsers.ListStringParser;

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
	@In(parser=ListStringParser.class)
	private List<String> data1 = Arrays.asList(new String[]{});

	@Out
	int processed = 0;

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
		processed++;
		System.out.println("exit forEach4(Prog2)");
	}


}
