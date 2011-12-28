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
import org.parallelj.Begin;
import org.parallelj.AndSplit;
import org.parallelj.tracknrestart.annotations.TrackNRestart;

/**
 * Program tbr.groupid.jjp1.pack1.Prog2
 * Description :
 **/
@Generated("//J")
@Program
@TrackNRestart(filteredExceptions={RuntimeException.class, BusinessException.class})
//@TrackNRestart(filteredExceptions={RuntimeException.class, SubBusinessException.class})
//@TrackNRestart(filteredExceptions={SubBusinessException.class})
public class Prog2 {
	/**
	 * prog2DataIn field
	 * Description :
	 **/
	String prog2DataIn;

	/**
	 * Noop Procedure proc4. This procedure is not bound to an executable.
	 * Description :
	 * @throws BusinessException 
	 * 
	 * @generated //J
	 */
	@Generated(value = "//J", comments = "3677788")
	@Begin
	@AndSplit({ "end" })
	public void proc4() throws BusinessException {
		System.out.println("proc4");
		System.out.println("prog2DataIn=" + prog2DataIn);
//		if(prog2DataIn.equals("0")) 
//			throw new BusinessException("The letter is '0', which is not permitted !");
//		if(prog2DataIn.toUpperCase().equals(prog2DataIn)) 
//			throw new RuntimeException("The letter is upcased, which is not permitted !");
		if(!simulateBusiness()) 
			throw new BusinessException("simulated business process unsuccessful for OID=" + prog2DataIn);
	}

	private boolean simulateBusiness() {
		boolean success = (Math.floor(Math.random()*1000))%2==0;
		return success;
	}

	public String getOID(){
		return prog2DataIn;
	}
}