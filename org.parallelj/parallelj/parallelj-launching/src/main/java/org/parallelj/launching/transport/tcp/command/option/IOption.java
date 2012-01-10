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
package org.parallelj.launching.transport.tcp.command.option;

import org.apache.commons.cli.Option;
import org.parallelj.launching.parser.ParserException;
import org.parallelj.launching.transport.tcp.program.TcpIpProgram;
import org.quartz.JobDataMap;

/**
 * Define an Option for a remote Command.
 *
 */
public interface IOption {
	public Option getOption();

	public void setOption(Option option);

	public int getPriority();

	/**
	 * Check the current Option. Example of what to check:
	 * <p>
	 * The value passed for the Option
	 * </p>
	 * <p>
	 * The arguments passed to this Option (if defined)
	 * </p>
	 * 
	 * @param tcpIpProgram
	 * @throws OptionException
	 * @throws ParserException
	 */
	public void ckeckOption(TcpIpProgram tcpIpProgram) throws OptionException,
			ParserException;

	/**
	 * Process the current Option. Example of process:
	 * <p>
	 * Initialize some JobDataMap values with the Option
	 * </p>
	 * <p>
	 * Initialize some JobDataMap values with the arguments passed to this
	 * Option (if defined)
	 * </p>
	 * 
	 * @param jobDataMap
	 * @param args
	 * @throws OptionException
	 * @throws ParserException
	 */
	public void process(JobDataMap jobDataMap, Object... args)
			throws OptionException, ParserException;
}
