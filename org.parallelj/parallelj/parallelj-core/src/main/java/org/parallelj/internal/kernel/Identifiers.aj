/*
 *     ParallelJ, framework for parallel computing
 *
 *     Copyright (C) 2010 Atos Worldline or third-party contributors as
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

package org.parallelj.internal.kernel;

import java.util.concurrent.atomic.AtomicLong;

/**
 * This class manages the identifiers of main types of //J.
 * 
 * An id is composed of 4 numbers separated by dots.
 * 
 * @author Atos Worldline
 * 
 */
public privileged aspect Identifiers {

	/**
	 * The sequence for program id chunk allocation.
	 */
	static AtomicLong sequence = new AtomicLong(1);

	/**
	 * The id chunk for a program
	 */
	long KProgram.chunk;

	/**
	 * The sequence for procedure id chunk allocation.
	 */
	AtomicLong KProgram.procedureSequence = new AtomicLong(1);

	/**
	 * The sequence for process id chunk allocation.
	 */
	AtomicLong KProgram.processSequence = new AtomicLong(1);

	/**
	 * The id chunk for a procedure
	 */
	long KProcedure.chunk;

	/**
	 * The sequence for procedure call id chunk allocation.
	 */
	AtomicLong KProcedure.callSequence = new AtomicLong(1);

	/**
	 * The id chunk for a process
	 */
	long KProcess.chunk;

	/**
	 * The id chunk for a call
	 */
	long KCall.chunk;

	after(KProgram self): execution(KProgram.new(..)) && this(self) {
		generateProgramId(self);
	}

	after(KProcedure self): execution(KProcedure.new(..)) && this(self) {
		generateProcedureId(self);
	}

	after(KProcess self): execution(KProcess.new(..)) && this(self) {
		generateProcessId(self);
	}

	after(KCall self): execution(KCall.new(..)) && this(self) {
		generateCallId(self);
	}

	static void generateProgramId(KProgram self) {
		self.chunk = sequence.getAndIncrement();
		self.id = String.format("%d.0.0.0", self.chunk);
	}

	static void generateProcedureId(KProcedure self) {
		self.chunk = self.program.procedureSequence.getAndIncrement();
		self.id = String.format("%d.%d.0.0", self.program.chunk, self.chunk);
	}

	static void generateProcessId(KProcess self) {
		self.chunk = self.program.processSequence.getAndIncrement();
		self.id = String.format("%d.0.%d.0", self.program.chunk, self.chunk);
	}

	static void generateCallId(KCall self) {
		self.chunk = self.procedure.callSequence.getAndIncrement();
		self.id = String.format("%d.%d.%d.%d", self.procedure.program.chunk,
				self.procedure.chunk, self.process.chunk, self.chunk);
	}

}
