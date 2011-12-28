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
package foo.join;

import org.parallelj.AndJoin;
import org.parallelj.AndSplit;
import org.parallelj.Link;
import org.parallelj.OrJoin;
import org.parallelj.OrSplit;
import org.parallelj.Program;

@Program
public class OrJoinProgram {

	int a, b, c, d, e = 0;

	boolean toB, toC, toD;

	@AndJoin("begin")
	@OrSplit( { @Link(to = "b", predicate = "toB"),
			@Link(to = "c", predicate = "toC"),
			@Link(to = "d", predicate = "toD") })
	public void a() {
		a++;
	}

	@AndJoin
	@AndSplit("e")
	public void b() {
		b++;
	}

	@AndJoin
	@AndSplit("e")
	public void c() {
		c++;
	}

	@AndJoin
	@AndSplit("e")
	public void d() {
		d++;
	}

	@OrJoin
	@AndSplit("end")
	public void e() {
		e++;
	}

	public boolean isToB() {
		return toB;
	}

	public void setToB(boolean toB) {
		this.toB = toB;
	}

	public boolean isToC() {
		return toC;
	}

	public void setToC(boolean toC) {
		this.toC = toC;
	}

	public boolean isToD() {
		return toD;
	}

	public void setToD(boolean toD) {
		this.toD = toD;
	}

}
