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
package org.parallelj.launching.remote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.parallelj.Programs;
import org.parallelj.Programs.ProcessHelper;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.kernel.KProgram;
import org.parallelj.internal.reflect.ProcessHelperImpl;
import org.parallelj.internal.reflect.Adapter;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.inout.Argument;
import org.parallelj.launching.inout.ArgumentComparator;
import org.parallelj.launching.inout.IProgramInputOutputs;
import org.parallelj.launching.inout.Output;

public class RemoteProgram {
	private Class<? extends Adapter> adapterClass;
	
	private List<Argument> arguments = new ArrayList<Argument>();
	private List<Output> output = new ArrayList<Output>();
	
	public RemoteProgram(final Class<? extends Adapter> adapterClass) {
		this.adapterClass = adapterClass;
		
		// Instanciate an instance of Program to get its arguments and output..
		try {
			Object obj = adapterClass.newInstance();
			ProcessHelper<?> p = Programs.as(obj);
			this.arguments = ((IProgramInputOutputs) ((KProgram) ((KProcess) ((ProcessHelperImpl<?>) p)
					.getProcess()).getProgram())).getArguments();
			this.output = ((IProgramInputOutputs) ((KProgram) ((KProcess) ((ProcessHelperImpl<?>) p)
					.getProcess()).getProgram())).getOutputs();
			
			// Sorts Arguments
			Collections.sort(this.arguments, new ArgumentComparator());
		} catch (Exception e) {
			LaunchingMessageKind.ELAUNCH0002.format(e);
		}
	}

	public Class<? extends Adapter> getAdapterClass() {
		return this.adapterClass;
	}

	public List<Argument> getArguments() {
		return this.arguments;
	}

	public List<Output> getOutput() {
		return this.output;
	}
	
}
