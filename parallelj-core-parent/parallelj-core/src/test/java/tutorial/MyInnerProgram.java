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
package tutorial;

import javax.annotation.Generated;

import org.parallelj.Capacity;
import org.parallelj.Program;
import org.parallelj.Begin;
import org.parallelj.AndSplit;
import org.parallelj.AndJoin;

/**
 * Program org.parallelj.test.demo.MyInnerProgram Description :
 **/
@Generated("//J")
@Program
public class MyInnerProgram {

	private String val;

	public MyInnerProgram(String val) {
		super();
		this.val = val;
	}

	@Begin
	@Capacity(1)
	public ToUpperCase procone() {
		System.out.println("Display procone 1a: " + val);
		ToUpperCase toUpperCase = new ToUpperCase();
		toUpperCase.setSource(val);
		return toUpperCase;
	}

	@AndSplit({ "proctwo" })
	@Capacity(1)
	public void procone(ToUpperCase toUpperCase, String result) {
		System.out.println("RES one: " + result);
	}

	@AndJoin
	@Capacity(1)
	public ToUpperCase proctwo() {
		System.out.println("Display proctwo 1b: " + val);
		ToUpperCase toUpperCase = new ToUpperCase();
		toUpperCase.setSource(val);
		return toUpperCase;
	}

	@AndSplit({ "procthree" })
	@Capacity(1)
	public void proctwo(ToUpperCase toUpperCase, String result) {
		// System.out.println("Display proctwo 1b: " + iteratortwo.next());
		System.out.println("RES two: " + result);
	}

	@AndJoin
	@Capacity(1)
	public ToUpperCase procthree() {
		System.out.println("Display procthree 1c: " + val);

		ToUpperCase toUpperCase = new ToUpperCase();
		toUpperCase.setSource(val);
		return toUpperCase;
	}

	@AndSplit({ "end" })
	@Capacity(1)
	public void procthree(ToUpperCase toUpperCase, String result) {
		System.out.println("RES three: " + result);
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}
}
