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

import java.util.List;

import org.parallelj.mirror.Event;
import org.parallelj.mirror.MachineKind;
import org.parallelj.mirror.Process;
import org.parallelj.mirror.Processor;
import org.parallelj.mirror.ProcessorState;

class ProcessorProxy extends ElementProxy implements Processor {

	ProcessorProxy() {
	}

	@Override
	public ProcessorState getState() {
		return Enum.valueOf(ProcessorState.class, this.getContent()
				.get("State").getAsString());
	}

	@Override
	public List<Event<ProcessorState>> getEvents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(Process process) {
		this.reflection.executeProcess(this, ((ProcessProxy) process));
	}

	@Override
	public void suspend() {
		this.reflection.suspendProcessor(this);
	}

	@Override
	public void resume() {
		this.reflection.resumeProcessor(this);
	}

	@Override
	public MachineKind getKind() {
		return MachineKind.PROCESSOR;
	}

}
