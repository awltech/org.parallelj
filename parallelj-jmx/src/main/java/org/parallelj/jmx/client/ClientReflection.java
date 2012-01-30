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

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.InstanceNotFoundException;
import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.xml.bind.JAXB;

import org.parallelj.jmx.ManagementMBean;
import org.parallelj.mirror.EventListener;
import org.parallelj.mirror.ExecutorServiceKind;
import org.parallelj.mirror.MachineKind;
import org.parallelj.mirror.Process;
import org.parallelj.mirror.Processor;
import org.parallelj.mirror.ProgramType;
import org.parallelj.mirror.Reflection;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ClientReflection implements Reflection, NotificationListener {

	class Pool<E extends ElementProxy> {

		Map<String, E> instances = Collections.synchronizedMap(new HashMap<String, E>());

		Class<E> type;

		public Pool(Class<E> type) {
			super();
			this.type = type;
		}

		E parseInstance(String s) {
			return this.getInstance((JsonObject) new JsonParser().parse(s));
		}

		E getInstance(JsonObject object) {
			String id = object.get("Id").getAsString();
			E e = instances.get(id);
			if (e == null) {
				e = this.newInstance();
				e.setReflection(ClientReflection.this);
				this.instances.put(id, e);
			}
			e.setContent(object);
			return e;
		}

		E newInstance() {
			try {
				return this.type.newInstance();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

	}

	private Pool<ProgramTypeProxy> programs = new Pool<ProgramTypeProxy>(ProgramTypeProxy.class);

	private Pool<ProcessorProxy> processors = new Pool<ProcessorProxy>(ProcessorProxy.class);

	private Pool<ProcessProxy> processes = new Pool<ProcessProxy>(ProcessProxy.class);

	private ManagementMBean management;

	public ClientReflection(MBeanServerConnection connection, ObjectName name) throws InstanceNotFoundException,
			IOException {
		this.management = JMX.newMBeanProxy(connection, name, ManagementMBean.class);
		connection.addNotificationListener(name, this, null, null);
	}

	@Override
	public List<ProgramType> getPrograms() {
		List<ProgramType> list = new ArrayList<ProgramType>();
		JsonArray array = (JsonArray) new JsonParser().parse(this.management.getPrograms());
		for (JsonElement element : array) {
			list.add(this.programs.getInstance((JsonObject) element));
		}
		return list;
	}

	ProgramType getProgram(String id) {
		return this.programs.parseInstance(this.management.getProgram(id));
	}

	@Override
	public Processor newProcessor(ExecutorServiceKind executorKind, Object... args) throws IllegalArgumentException {
		int n = 0;
		switch (executorKind) {
		case FIXED_THREAD_POOL:
			n = (Integer) args[0];
			break;
		case PROVIDED:
			executorKind = ExecutorServiceKind.SINGLE_THREAD_EXECUTOR;
			break;
		}
		return this.processors.parseInstance(this.management.newProcessor(executorKind.toString(), n));
	}

	@Override
	public void addEventListener(EventListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeEventListener(EventListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleNotification(Notification notification, Object handback) {
		JsonObject event = (JsonObject) new JsonParser().parse(notification.getMessage());
		MachineKind kind = Enum.valueOf(MachineKind.class, event.get("MachineKind").getAsString());
		String id = event.get("Source").getAsString();
		System.out.println("going to refresh: " + kind + ":" + id) ;
		switch (kind) {
		case PROCESS:
			this.getProcess(id);
			break;
		case PROCESSOR:
			this.getProcessor(id);
			break;
		}
	}

	void executeProcess(ProcessorProxy processorProxy, ProcessProxy processProxy) {
		JsonArray array = (JsonArray) new JsonParser().parse(this.management.executeProcess(processorProxy.getId(),
				processProxy.getId()));
		this.processors.getInstance((JsonObject) array.get(0));
		this.processes.getInstance((JsonObject) array.get(1));
	}

	void suspendProcessor(ProcessorProxy processorProxy) {
		this.processors.parseInstance(this.management.suspendProcessor(processorProxy.getId()));
	}

	void resumeProcessor(ProcessorProxy processorProxy) {
		this.processors.parseInstance(this.management.resumeProcessor(processorProxy.getId()));
	}

	void abortProcess(ProcessProxy processProxy) {
		this.processes.parseInstance(this.management.abortProcess(processProxy.getId()));
	}

	void terminateProcess(ProcessProxy processProxy) {
		this.processes.parseInstance(this.management.terminateProcess(processProxy.getId()));
	}

	Process newProcessor(ProgramTypeProxy programTypeProxy, Object context) {
		StringWriter writer = new StringWriter();
		JAXB.marshal(context, writer);
		return this.processes.parseInstance(this.management.newProcess(programTypeProxy.getId(), writer.toString()));
	}

	Processor getProcessor(String s) {
		if (s == null) {
			return null;
		}
		return this.processors.parseInstance(this.management.getProcessor(s));
	}

	Process getProcess(String s) {
		if (s == null) {
			return null;
		}
		return this.processes.parseInstance(this.management.getProcess(s));
	}

}
