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
package org.parallelj.tracknrestart.option;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.parallelj.launching.parser.ParserException;
import org.parallelj.launching.remote.RemoteProgram;
import org.parallelj.launching.transport.tcp.command.option.IAsyncLaunchOption;
import org.parallelj.launching.transport.tcp.command.option.OptionException;
import org.parallelj.tracknrestart.plugins.TrackNRestartPluginAll;
import org.quartz.JobDataMap;

/**
 * Define the rid Option for the AsynchLaunch command.
 *
 */
public class AsyncLaunchRidOption implements IAsyncLaunchOption {

	private Option option;

	public AsyncLaunchRidOption() {
		this.option = OptionBuilder.create("r");
		this.option.setLongOpt("rid");
		this.option.setArgs(1);
		this.option.setArgName("rid");
		this.option.setDescription("Restart Id of an already launched Program");
		this.option.setRequired(false);
	}

	public Option getOption() {
		return this.option;
	}

	public int getPriority() {
		return 90;
	}

	@Override
	public void setOption(Option option) {
		this.option = option;
	}

	@Override
	public void ckeckOption(RemoteProgram tcpIpProgram) throws OptionException,
			ParserException {
		if (this.getOption().getValue() != null && this.getOption().getValue().length()==0) {
			throw new OptionException("The restart Id must be specified!");
		}
	}

	@Override
	public void process(JobDataMap jobDataMap, Object... args)
			throws OptionException, ParserException {
		String restartId = this.getOption().getValue();
		if (restartId != null) {
			jobDataMap.put(TrackNRestartPluginAll.RESTARTED_FIRE_INSTANCE_ID, restartId);
		}
	}
}
