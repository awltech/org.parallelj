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
package org.parallelj.jmx;

import java.io.StringWriter;

import javax.xml.bind.JAXB;

import org.parallelj.mirror.Event;
import org.parallelj.mirror.Machine;
import org.parallelj.mirror.Procedure;
import org.parallelj.mirror.Process;
import org.parallelj.mirror.Processor;
import org.parallelj.mirror.ProgramType;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Marshaller {

	JsonArray programs(Iterable<ProgramType> programs) {
		JsonArray array = new JsonArray();
		for (ProgramType program : programs) {
			array.add(this.program(program));
		}
		return array;
	}

	JsonObject program(ProgramType program) {
		JsonObject object = new JsonObject();
		object.addProperty("Id", program.getId());
		object.addProperty("Name", program.getName());
		object.addProperty("ExceptionHandlingPolicy",
				String.valueOf(program.getExceptionHandlingPolicy()));

		object.add("Procedures", this.procedures(program.getProcedures()));

		return object;
	}

	JsonArray procedures(Iterable<Procedure> procedures) {
		JsonArray array = new JsonArray();
		for (Procedure procedure : procedures) {
			array.add(this.procedure(procedure));
		}
		return array;
	}

	JsonObject procedure(Procedure procedure) {
		JsonObject object = new JsonObject();
		object.addProperty("Id", procedure.getId());
		object.addProperty("Name", procedure.getName());
		object.addProperty("Type", procedure.getType());
		object.addProperty("Program", procedure.getProgram().getId());
		return object;
	}

	JsonObject process(Process process) {
		JsonObject object = this.machine(process);
		object.addProperty("Program", process.getProgram().getId());
		object.addProperty("Processor",
				(process.getProcessor() != null) ? process.getProcessor()
						.getId() : null);
		try {
			StringWriter writer = new StringWriter();
			JAXB.marshal(process.getContext(), writer);
			object.addProperty("Context", writer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return object;
	}

	JsonObject processor(Processor processor) {
		JsonObject object = this.machine(processor);
		return object;
	}

	<E extends Enum<E>> JsonObject machine(Machine<E> machine) {
		JsonObject object = new JsonObject();
		object.addProperty("Id", machine.getId());
		object.addProperty("Kind", String.valueOf(machine.getKind()));
		object.addProperty("State", String.valueOf(machine.getState()));
		object.add("Events", this.events(machine.getEvents()));
		return object;
	}

	<E extends Enum<E>> JsonArray events(Iterable<Event<E>> events) {
		JsonArray array = new JsonArray();
		for (Event<?> event : events) {
			array.add(this.event(event));
		}
		return array;
	}

	JsonObject event(Event<?> event) {
		JsonObject object = new JsonObject();
		object.addProperty("Source", event.getSource().getId());
		object.addProperty("MachineKind",
				String.valueOf(event.getMachineKind()));
		object.addProperty("State", String.valueOf(event.getState()));
		object.addProperty("Worker", event.getWorker());
		object.addProperty("Timestamp", event.getTimestamp());
		return object;
	}

}
