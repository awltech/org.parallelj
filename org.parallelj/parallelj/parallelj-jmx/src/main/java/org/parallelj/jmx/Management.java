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

package org.parallelj.jmx;

import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;
import javax.xml.bind.JAXB;

import org.parallelj.internal.kernel.KReflection;
import org.parallelj.mirror.Event;
import org.parallelj.mirror.EventListener;
import org.parallelj.mirror.ExecutorServiceKind;
import org.parallelj.mirror.Process;
import org.parallelj.mirror.Processor;
import org.parallelj.mirror.ProgramType;
import org.parallelj.mirror.Reflection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

public class Management extends NotificationBroadcasterSupport implements
		ManagementMBean, MBeanRegistration, EventListener {

	/**
	 * Sequence for processor ids.
	 */
	private AtomicLong sequence = new AtomicLong(1);
	
	ObjectName name;

	Reflection reflection = KReflection.getInstance();

	Map<String, Processor> processors = Collections
			.synchronizedMap(new HashMap<String, Processor>());

	Map<String, Process> processes = Collections
			.synchronizedMap(new HashMap<String, Process>());

	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	
	

	public Management() {
		this.reflection.addEventListener(this);
	}

	@Override
	public String getPrograms() {
		return gson.toJson(new Marshaller().programs(this.reflection
				.getPrograms()));
	}

	ProgramType findProgram(String id) {
		for (ProgramType type : this.reflection.getPrograms()) {
			if (type.getId().equals(id)) {
				return type;
			}
		}
		return null;
	}

	@Override
	public String getProgram(String programId) {
		ProgramType program = this.findProgram(programId);

		return (program == null) ? null : gson.toJson(new Marshaller()
				.program(program));
	}

	@Override
	public String newProcessor(String kind, int nThreads) {
		Processor processor = null;
		ExecutorServiceKind k = Enum.valueOf(ExecutorServiceKind.class, kind);
		switch (k) {
		case CACHED_THREAD_POOL:
		case SINGLE_THREAD_EXECUTOR:
			processor = this.reflection.newProcessor(k);
			break;
		case FIXED_THREAD_POOL:
			processor = this.reflection.newProcessor(k, nThreads);
			break;
		}
		if (processor == null) {
			// TODO add message kind
			return null;
		}
		this.processors.put(processor.getId(), processor);
		return gson.toJson(new Marshaller().processor(processor));
	}

	@Override
	public String getProcessor(String processorId) {
		// TODO check if not in map
		return gson.toJson(new Marshaller().processor(this.processors
				.get(processorId)));
	}

	@Override
	public String newProcess(String programId, String context) {
		ProgramType type = this.findProgram(programId);
		if (type == null) {
			return null;
		}
		try {
			Class<?> c = Class.forName(type.getName());
			Object o = JAXB.unmarshal(new StringReader(context), c);
			Process process = type.newProcess(o);
			this.processes.put(process.getId(), process);
			return gson.toJson(new Marshaller().process(process));
		} catch (Exception e) {
			// TODO add message kind
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String getProcess(String processId) {
		// TODO check if not in map
		return gson.toJson(new Marshaller().process(this.processes
				.get(processId)));
	}

	@Override
	public String executeProcess(String processorId, String processId) {
		Processor processor = this.processors.get(processorId);
		Process process = this.processes.get(processId);
		processor.execute(process);
		JsonArray array = new JsonArray();
		array.add(new Marshaller().processor(processor));
		array.add(new Marshaller().process(process));
		return gson.toJson(array);
	}

	@Override
	public String suspendProcessor(String processorId) {
		Processor processor = this.processors.get(processorId);
		processor.suspend();
		return gson.toJson(new Marshaller().processor(processor));
	}

	@Override
	public String resumeProcessor(String processorId) {
		Processor processor = this.processors.get(processorId);
		processor.resume();
		return gson.toJson(new Marshaller().processor(processor));
	}

	@Override
	public String terminateProcess(String processId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String abortProcess(String processId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObjectName preRegister(MBeanServer server, ObjectName name)
			throws Exception {
		this.name = (name == null) ? new ObjectName(
				"org.parallelj:type=Management") : name;
		return this.name;
	}

	@Override
	public void postRegister(Boolean registrationDone) {
		// TODO Auto-generated method stub

	}

	@Override
	public void preDeregister() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void postDeregister() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleEvent(Event<?> event) {
		boolean dispatch = false;
		switch (event.getMachineKind()) {
		case PROCESS:
			dispatch = this.processes.containsKey(event.getSource().getId());
			break;
		case PROCESSOR:
			dispatch = this.processors.containsKey(event.getSource().getId());
			break;
		}
		if (dispatch) {
			String message = this.gson.toJson(new Marshaller().event(event));
			this.sendNotification(new Notification("event", name, this.sequence.getAndIncrement(), message));
		}
	}

}
