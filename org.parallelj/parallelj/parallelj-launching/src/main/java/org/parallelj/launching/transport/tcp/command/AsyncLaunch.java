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
package org.parallelj.launching.transport.tcp.command;

import org.apache.commons.cli.ParseException;
import org.apache.mina.core.session.IoSession;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.parser.ParserException;
import org.parallelj.launching.quartz.Launch;
import org.parallelj.launching.quartz.LaunchException;
import org.parallelj.launching.quartz.Launcher;
import org.parallelj.launching.transport.jmx.JmxCommand;
import org.parallelj.launching.transport.tcp.command.option.IAsyncLaunchOption;
import org.parallelj.launching.transport.tcp.command.option.IOption;
import org.parallelj.launching.transport.tcp.command.option.OptionException;
import org.parallelj.launching.transport.tcp.program.TcpIpProgram;
import org.quartz.Job;
import org.quartz.JobDataMap;

/**
 * AsyncLaunch TcpCommand
 * 
 */
public class AsyncLaunch extends AbstractLaunchTcpCommand implements JmxCommand {

	// private final static String DONE = "Done.";

	private static final int PRIORITY = 70;
	//private static final String USAGE = " asynclaunch -id x -rid y params : Launches a new Program instance with ID x, and returns (asynchronous launch).";
	private static final String USAGE = "asynclaunch : Launches a new Program instance, and returns (asynchronous launch).";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.parallelj.launching.transport.tcp.command.AbstractTcpCommand#process
	 * (org.apache.mina.core.session.IoSession, java.lang.String[])
	 */
	@Override
	public final String process(IoSession session, String... args) {
		JobDataMap jobDataMap = new JobDataMap();
		TcpIpProgram tcpIpProgram=null;
		// Get the corresponding TcpIpProgram
		try {
			tcpIpProgram = parseCommandLine(args);
			
			for (IOption ioption:this.getOptions()) {
				ioption.process(jobDataMap, tcpIpProgram);
			}
			
			@SuppressWarnings("unchecked")
			Class<? extends Job> jobClass = (Class<? extends Job>) tcpIpProgram.getAdapterClass();
			Launcher launcher = Launcher.getLauncher();

			Launch launch = launcher.newLaunch(jobClass)
					.addDatas(jobDataMap).aSynchLaunch();
			
			return LaunchingMessageKind.IQUARTZ0002.getFormatedMessage(
					jobClass.getCanonicalName(),
					launch.getLaunchId());
		} catch (ParseException e) {
			return e.getMessage();
		} catch (ParserException e) {
			return e.getFormatedMessage();
		} catch (OptionException e) {
			return e.getFormatedMessage();
		} catch (LaunchException e) {
			return  LaunchingMessageKind.EQUARTZ0003.format(tcpIpProgram!=null?tcpIpProgram.getAdapterClass():"unknown");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.parallelj.launching.transport.tcp.command.AbstractTcpCommand#getType
	 * ()
	 */
	public String getType() {
		return RemoteCommand.asynclaunch.name();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.parallelj.launching.transport.tcp.command.AbstractTcpCommand#getUsage
	 * ()
	 */
	@Override
	public String getUsage() {
		return USAGE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.parallelj.launching.transport.tcp.command.AbstractTcpCommand#
	 * getPriorityUsage()
	 */
	@Override
	public int getPriorityUsage() {
		return PRIORITY;
	}

	@Override
	public Class<? extends IOption> getOptionClass() {
		return IAsyncLaunchOption.class;
	}

}
