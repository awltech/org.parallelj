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

public class SyncLaunchArgsOption implements ISyncLaunchOption {

	private Option option;

	public SyncLaunchArgsOption() {
		this.option = OptionBuilder.create("a");
		this.option.setLongOpt("args");
		this.option.setArgs(100);
		this.option.setArgName("arg1=value1 arg2=value2 ... argx=valueX");
		this.option.setDescription("Arguments of the Program");
		this.option.setRequired(false);
	}

	public Option getOption() {
		return this.option;
	}

	public int getPriority() {
		return 0;
	}

	@Override
	public void process(final JobDataMap jobDataMap, final Object... args)
			throws OptionException, ParserException {
		OptionsUtils.processArgs(this, jobDataMap, args);
	}

	@Override
	public void setOption(final Option option) {
		this.option = option;
	}

	@Override
	public void ckeckOption(final RemoteProgram remoteProgram) throws OptionException,
			ParserException {
		OptionsUtils.checkArgs(this, remoteProgram);
	}

}
