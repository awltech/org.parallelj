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
import org.apache.commons.cli.OptionBuilder;
import org.parallelj.launching.parser.ParserException;
import org.parallelj.launching.remote.RemoteProgram;
import org.quartz.JobDataMap;

public class AsyncLaunchIdOption implements IAsyncLaunchOption, IIdOption {

	private Option option;

	public AsyncLaunchIdOption() {
		this.option = OptionBuilder.create("i");
		this.option.setLongOpt("id");
		this.option.setArgs(1);
		this.option.setArgName("id");
		this.option
				.setDescription("Id of the Program from the list return by ll command");
		this.option.setRequired(true);
	}

	public Option getOption() {
		return this.option;
	}

	public int getPriority() {
		return 100;
	}

	@Override
	public void process(final JobDataMap jobDataMap, final Object... args)
			throws OptionException, ParserException {
		OptionsUtils.processId(this, jobDataMap);
	}

	@Override
	public void setOption(final Option option) {
		this.option = option;
	}

	@Override
	public void ckeckOption(final RemoteProgram remoteProgram) throws OptionException,
			ParserException {
		// Do nothing
	}

	@Override
	public RemoteProgram getProgram() throws OptionException {
		return OptionsUtils.getProgram(this);
	}
}
