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
package org.parallelj.launching.inout;

import java.util.ArrayList;
import java.util.List;

public aspect InOutManagement {

	/*
	 * Add the interface IProgramArguments to the KProgram
	*/
	declare parents: org.parallelj.internal.kernel.KProgram implements IProgramInputOutputs;

	public List<Argument> IProgramInputOutputs.arguments = new ArrayList<Argument>();
	public List<Output> IProgramInputOutputs.outputs = new ArrayList<Output>();
	
	public List<Argument> IProgramInputOutputs.getArguments() {
		return this.arguments;
	}
	
	public void IProgramInputOutputs.addArgument(Argument argument) {
		this.arguments.add(argument);
	}
	
	public Argument IProgramInputOutputs.getArgument(String name) {
		for (Argument argument : this.arguments) {
			if (argument.getName().equals(name)) {
				return argument;
			}
		}
		return null;
	}
	
	public List<Output> IProgramInputOutputs.getOutputs() {
		return this.outputs;
	}
	
	public void IProgramInputOutputs.addOutput(Output output) {
		this.outputs.add(output);
	}
	
	public Output IProgramInputOutputs.getOutput(String name) {
		for (Output output : this.outputs) {
			if (output.getName().equals(name)) {
				return output;
			}
		}
		return null;
	}

}
