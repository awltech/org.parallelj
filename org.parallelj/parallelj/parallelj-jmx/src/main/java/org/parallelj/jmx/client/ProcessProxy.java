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
package org.parallelj.jmx.client;

import java.io.StringReader;
import java.util.List;

import javax.xml.bind.JAXB;

import org.parallelj.mirror.Event;
import org.parallelj.mirror.MachineKind;
import org.parallelj.mirror.Process;
import org.parallelj.mirror.ProcessState;
import org.parallelj.mirror.Processor;
import org.parallelj.mirror.ProgramType;

class ProcessProxy extends ElementProxy implements Process {

	ProcessProxy() {
	}

	@Override
	public ProcessState getState() {
		return Enum.valueOf(ProcessState.class, this.getContent().get("State")
				.getAsString());
	}

	@Override
	public List<Event<ProcessState>> getEvents() {
		return null;
	}

	@Override
	public ProgramType getProgram() {
		return this.reflection.getProgram(this.getContent().get("Program")
				.getAsString());
	}

	@Override
	public Object getContext() {
		try {
			Class<?> c = Class.forName(this.getProgram().getName());
			return JAXB.unmarshal(
					new StringReader(this.getContent().get("Context")
							.getAsString()), c);
		} catch (Exception e) {
			// TODO add message kind
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Processor getProcessor() {
		return this.reflection.getProcessor(this.getContent().get("Processor")
				.getAsString());
	}

	@Override
	public void abort() {
		this.reflection.abortProcess(this);
	}

	@Override
	public void terminate() {
		this.reflection.terminateProcess(this);
	}

	@Override
	public MachineKind getKind() {
		return MachineKind.PROCESS;
	}

}
