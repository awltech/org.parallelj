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

import org.parallelj.internal.kernel.KProcessor;

/**
 * Represents a log entry
 * 
 * @author Atos Worldline
 * @since 0.3.0
 * 
 */
public class LogEntry {

	/**
	 * When the JVM starts
	 */
	public static final long epoch = System.currentTimeMillis();

	/**
	 * The first log entry
	 */
	public static LogEntry prolog = new LogEntry("0.0.0.0/0.0.0.0");

	/**
	 * The identity of the object that is the source of the event.
	 */
	private String id;

	/**
	 * the creation time.
	 */
	private long created = System.currentTimeMillis();// - epoch;

	/**
	 * The start time of the event.
	 */
	private long start;

	/**
	 * The ending time of the event.
	 */
	private long end;

	/**
	 * The status of the event.
	 */
	private String state;

	/**
	 * The attribute set
	 */
	private String attributes = "";

	/**
	 * Create a new log entry.
	 */
	public LogEntry() {
	}

	private LogEntry(String id) {
		this.id = id;
		this.created = this.start = this.end = epoch;
		this.state = "prolog";

	}

	/**
	 * Called when it is started
	 * 
	 * @param id
	 *            the id of the log entry
	 */
	public void start(String id) {
		this.start = System.currentTimeMillis();// - epoch;
		this.id = id;
	}

	/**
	 * Called when it is ended
	 * 
	 * @param state
	 *            the final state
	 */
	public void end(String state) {
		this.end(state, "");
	}

	/**
	 * Called when it is ended
	 * 
	 * @param state
	 *            the final state
	 * @param attributes the attributes
	 */
	public void end(String state, String attributes) {
		this.end = System.currentTimeMillis();// - epoch;
		this.state = state;
		this.attributes = attributes;
		// System.out.println(this);
	}

	@Override
	public String toString() {
		KProcessor processor = KProcessor.currentProcessor();
		return String.format("[%s] %s %d:%d:%d:%d %s %s %s",
				(processor == null) ? 0 : processor.getId(), this.id,
				this.created, this.start, this.end, this.end - this.start,
				this.state, Thread.currentThread().getName(), this.attributes);
	}
}
