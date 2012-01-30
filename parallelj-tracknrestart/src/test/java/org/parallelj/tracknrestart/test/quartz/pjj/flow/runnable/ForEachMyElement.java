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
package org.parallelj.tracknrestart.test.quartz.pjj.flow.runnable;

import javax.annotation.Generated;
import org.parallelj.Program;

import java.util.Arrays;
import java.util.List;
import org.parallelj.Begin;
import org.parallelj.Capacity;
import org.parallelj.ForEach;
import org.parallelj.AndSplit;
import org.parallelj.launching.In;
import org.parallelj.launching.QuartzExecution;
//import org.parallelj.tracknrestart.databinding.In;

import org.parallelj.tracknrestart.test.quartz.pjj.parsers.ListPeopleParser;

/**
 * Program tutorial.ForEachMyElement
 * Description :
 **/
@Generated("//J")
@Program
@QuartzExecution
public class ForEachMyElement {
	/**
	 * elementList field
	 * Description :
	 **/
	@In(parser=ListPeopleParser.class)
	private List<People> data1 = Arrays.asList(new People[]{});

	public List<People> getData1() {
		return data1;
	}

	public void setData1(List<People> elementList) {
		this.data1 = elementList;
	}

	/**
	 * Exit method of procedure forEachLoop. This procedure is bound to
	 * tutorial.MyExecutable Description :
	 * 
	 * @generated //J
	 */
	@Generated(value = "//J", comments = "3677788")
	@AndSplit({ "end" })
	public void forEachLoop(MyExecutable executable) {
		System.out.println("ForEachMyElement::forEachLoop :: @AndSplit({ 'end' }) avec un executable :" + executable.getSource());
	}

	/**
	 * Entry method of procedure forEachLoop. This procedure is bound to
	 * tutorial.MyExecutable Description :
	 * 
	 * @generated //J
	 */
	@Generated(value = "//J", comments = "316001396")
	@Begin
	@Capacity(1)
	public MyExecutable forEachLoop(@ForEach("data1") People val) {
		System.out.println("ForEachMyElement::forEachLoop :: @ForEach with val :" +val);
		MyExecutable m = new MyExecutable();
		m.setSource(val);
		return m;
	}
}
