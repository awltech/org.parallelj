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

import java.util.HashSet;
import java.util.Set;

import org.parallelj.AndSplit;
import org.parallelj.Begin;
import org.parallelj.Capacity;
import org.parallelj.ForEach;
import org.parallelj.Program;

/**
 * A program that convert an iterable of String to their upper case values.  
 * 
 * @author Laurent Legrand
 *
 */
@Program
public class ForEachTutorial {

	/**
	 * The collection of String to convert
	 */
	Iterable<String> input;

	/**
	 * The set containing the upper case values
	 */
	Set<String> output = new HashSet<String>();

	/**
	 * The entry method of the toUpperCase procedure.
	 * 
	 * Will be called for each element in {@link #input}
	 * 
	 * @param s a string to convert
	 * @return a callable that will perform the conversion
	 */
	@Begin
	@Capacity(3)
	public ToUpperCase toUpperCase(@ForEach("input") String s) {
		ToUpperCase toUpperCase = new ToUpperCase();
		toUpperCase.setSource(s);
		return toUpperCase;
	}

	/**
	 * The exit method of the toUpperCase procedure.
	 * 
	 * @param toUpperCase the callable that did the conversion
	 * @param result the return value of the callable
	 */
	@AndSplit("end")
	public void toUpperCase(ToUpperCase toUpperCase, String result) {
		this.output.add(result);
		System.out.println("RES: " + result);
	}

	public Iterable<String> getInput() {
		return input;
	}

	public void setInput(Iterable<String> input) {
		this.input = input;
	}

	public Set<String> getOutput() {
		return output;
	}

}
