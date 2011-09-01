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

import java.util.ArrayList;
import java.util.List;

import org.parallelj.mirror.ExceptionHandlingPolicy;
import org.parallelj.mirror.Procedure;
import org.parallelj.mirror.Process;
import org.parallelj.mirror.ProgramType;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

class ProgramTypeProxy extends ElementProxy implements ProgramType {

	class ProcedureProxy extends ElementProxy implements Procedure {

		@Override
		public String getName() {
			return this.getContent().get("Name").getAsString();
		}

		@Override
		public String getType() {
			return this.getContent().get("Type").getAsString();
		}

		@Override
		public ProgramType getProgram() {
			return ProgramTypeProxy.this;
		}

	}

	public ProgramTypeProxy() {
	}

	@Override
	public String getName() {
		return this.getContent().get("Name").getAsString();
	}

	@Override
	public List<Procedure> getProcedures() {
		List<Procedure> list = new ArrayList<Procedure>();
		for (JsonElement element : this.getContent().get("Procedures")
				.getAsJsonArray()) {
			ProcedureProxy procedure = new ProcedureProxy();
			procedure.setContent((JsonObject) element);
			list.add(procedure);
		}
		return list;
	}

	@Override
	public ExceptionHandlingPolicy getExceptionHandlingPolicy() {
		return Enum.valueOf(ExceptionHandlingPolicy.class, this.getContent()
				.get("ExceptionHandlingPolicy").getAsString());
	}

	@Override
	public Process newProcess(Object context) {
		return this.reflection.newProcessor(this, context);
	}

}
