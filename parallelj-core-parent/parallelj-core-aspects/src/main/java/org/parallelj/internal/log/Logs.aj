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
package org.parallelj.internal.log;

import java.net.URLEncoder;
import java.util.Map;

import org.parallelj.Executables;
import org.parallelj.internal.kernel.KCall;
import org.parallelj.internal.kernel.KProcess;
import org.parallelj.internal.kernel.KProgram;
import org.parallelj.mirror.Procedure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class manages the event logs of each major element of //J.
 * 
 * @author Atos Worldline
 * 
 */
public privileged aspect Logs {

	Logger logger = LoggerFactory.getLogger("org.parallelj.events");

	/**
	 * Flag indicating if a program has already been logged.
	 */
//	boolean KProgram.dumped = false;
	
	interface IDumped{};

	declare parents: KProgram implements IDumped;
	
	boolean IDumped.dumped = false;
	
	interface ILogEntry{};
	
	declare parents: KProcess implements ILogEntry;
	declare parents: KCall implements ILogEntry;

	LogEntry ILogEntry.logEntry = new LogEntry();
	
	/**
	 * The {@link LogEntry} of a process.
	 */
//	LogEntry KProcess.logEntry = new LogEntry();

	/**
	 * The {@link LogEntry} of a procedure {@link KCall call}.
	 */
//	LogEntry KCall.logEntry = new LogEntry();

	Logs() {
		// log the prolog
		logger.info(LogEntry.prolog.toString());
		// System.out.println(LogEntry.prolog);
	}

	/**
	 * This advice dumps the main element of a program the first time the method
	 * newProcess(...) is called.
	 * 
	 * @param self
	 */
	before(KProgram self): execution(* KProgram.newProcess(..)) && this(self) && if(!((IDumped)self).dumped) {
		LogEntry programEntry = new LogEntry();
		programEntry.start("0.0.0.0/" + self.getId());
		programEntry.end(self.getName());
		logger.info(programEntry.toString());

		for (Procedure procedure : self.getProcedures()) {
			LogEntry procedureEntry = new LogEntry();
			procedureEntry.start(self.getId() + "/" + procedure.getId());
			procedureEntry.end(procedure.getName() + ":" + procedure.getType());
			logger.info(procedureEntry.toString());
		}
		((IDumped)self).dumped = true;
	}

	/**
	 * This advice manages the LogEntry of a process when its state has changed.
	 * 
	 * @param self
	 *            the process
	 */
	after(KProcess self) : execution(* KProcess.doStart()) && this(self) {
		((ILogEntry)self).logEntry.start(self.getParentId() + "/" + self.getId());
	}

	after(KProcess self) : execution(* KProcess.done()) && this(self) {
		((ILogEntry)self).logEntry.end(self.getState().toString(),
				this.attributes(self.getContext()));
		logger.info(((ILogEntry)self).logEntry.toString());
	}

	/**
	 * This advice manages the LogEntry of a call when its state has changed.
	 * 
	 * @param self
	 *            the procedure call
	 */
	after(KCall self) : execution(* KCall.onRunning()) && this(self) {
		((ILogEntry)self).logEntry.start(self.process.getId() + "/" + self.getId());
	}
	
	after(KCall self) : execution( * KCall.onComplete()) && this(self) {
		((ILogEntry)self).logEntry.end(self.getState().toString(),
				this.attributes(self.getContext()));
		logger.info(((ILogEntry)self).logEntry.toString());
		
	}
		after(KCall self) : execution(* KCall.onCanceled()) && this(self) {
			((ILogEntry)self).logEntry.start(self.process.getId() + "/" + self.getId());
			((ILogEntry)self).logEntry.end(self.getState().toString(),
					this.attributes(self.getContext()));
			logger.info(((ILogEntry)self).logEntry.toString());
		}

	@SuppressWarnings("deprecation")
	String attributes(Object context) {
		StringBuilder builder = new StringBuilder();
		Map<String, String> map = Executables.attributes(context);
		for (Map.Entry<String, String> entry : map.entrySet()) {
			if (builder.length() > 0) {
				builder.append('&');
			}
			builder.append(URLEncoder.encode(entry.getKey())).append('=')
					.append(URLEncoder.encode(entry.getValue()));
		}
		return builder.toString();
	}
}
