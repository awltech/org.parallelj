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
package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.parallelj.AndSplit;
import org.parallelj.Begin;
import org.parallelj.ForEach;
import org.parallelj.Program;

@Program
public class MyProgram {

	List<String> lower = new ArrayList<String>();

	List<String> upper = Collections.synchronizedList(new ArrayList<String>());

	public MyProgram() {
		lower.addAll(Arrays.asList("a", "b", "c"));
	}

	@Begin
	@AndSplit("end")
	public void run(@ForEach("lower") String s) {
		System.out.println(Thread.currentThread() + ": " + this + ": " + s);
		this.upper.add(s.toUpperCase());

	}

	@Override
	public String toString() {
		return ": " + this.lower + ":" + this.upper;
	}

}
