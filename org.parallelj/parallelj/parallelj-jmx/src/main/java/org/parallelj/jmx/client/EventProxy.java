/*
 *     ParallelJ, framework for parallel computing
 *
 *     Copyright (C) 2010, 2011 Atos Worldline or third-party contributors as
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

import org.parallelj.mirror.Event;
import org.parallelj.mirror.Machine;
import org.parallelj.mirror.MachineKind;
import org.parallelj.mirror.ProcessState;
import org.parallelj.mirror.ProcessorState;

public class EventProxy extends ElementProxy implements Event {

	@Override
	public Machine getSource() {
		// TODO
		return null;
	}

	@Override
	public MachineKind getMachineKind() {
		return Enum.valueOf(MachineKind.class,
				this.getContent().get("MachineKind").getAsString());
	}

	@Override
	public Enum getState() {
		switch (this.getMachineKind()) {
		case PROCESS:
			return Enum.valueOf(ProcessState.class,
					this.getContent().get("State").getAsString());
		case PROCESSOR:
			return Enum.valueOf(ProcessorState.class,
					this.getContent().get("State").getAsString());
		}
		return null;
	}

	@Override
	public String getWorker() {
		return this.getContent().get("Worker").getAsString();
	}

	@Override
	public long getTimestamp() {
		return this.getContent().get("Timestamp").getAsLong();
	}

}
