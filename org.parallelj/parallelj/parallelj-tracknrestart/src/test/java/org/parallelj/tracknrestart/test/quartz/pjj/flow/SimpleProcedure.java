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
