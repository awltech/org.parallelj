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
import org.parallelj.launching.ReturnCodes;
import org.parallelj.launching.parser.ParserException;
import org.parallelj.launching.quartz.Launch;
import org.parallelj.launching.quartz.LaunchException;
import org.parallelj.launching.quartz.Launcher;
import org.parallelj.launching.quartz.QuartzUtils;
import org.parallelj.launching.remote.RemoteProgram;
import org.parallelj.launching.transport.jmx.JmxCommand;
import org.parallelj.launching.transport.tcp.command.option.IOption;
import org.parallelj.launching.transport.tcp.command.option.ISyncLaunchOption;
import org.parallelj.launching.transport.tcp.command.option.OptionException;
import org.quartz.Job;
import org.quartz.JobDataMap;

/**
 * SuncLaunch TcpCommand
 * 
 */
public class SyncLaunch extends AbstractLaunchTcpCommand implements JmxCommand {

	private static final int PRIORITY = 80;
	// private static final String USAGE =
	// "  synclaunch -id x -rid y params : Launches a new Program instance with ID x, waits till return status (synchronous launch).";
	private static final String USAGE = "synclaunch : Launches a new Program instance and waits till return status (synchronous launch).";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.parallelj.launching.transport.tcp.command.AbstractTcpCommand#process
	 * (org.apache.mina.core.session.IoSession, java.lang.String[])
	 */
	@Override
	public final String process(final IoSession session, final String... args) {
		final JobDataMap jobDataMap = new JobDataMap();
		RemoteProgram remoteProgram = null;
		// Get the corresponding remoteProgram
		try {
			remoteProgram = parseCommandLine(args);

			for (IOption ioption : this.getOptions()) {
				ioption.process(jobDataMap, remoteProgram);
			}

			@SuppressWarnings("unchecked")
			final Class<? extends Job> jobClass = (Class<? extends Job>) remoteProgram
					.getAdapterClass();
			final Launcher launcher = Launcher.getLauncher();

			final Launch launch = launcher.newLaunch(jobClass)
					.addDatas(jobDataMap).synchLaunch();

			final JobDataMap jdm = launch.getLaunchResult();
			String status = null;
			String userErrorCode = null;
			if (jdm == null) {
				status = ReturnCodes.NOTSTARTED.name();
			} else {
				status = String.valueOf(jdm.get(QuartzUtils.RETURN_CODE));
				userErrorCode = String.valueOf(jdm.get(QuartzUtils.USER_RETURN_CODE));
			}

			return LaunchingMessageKind.IQUARTZ0003.getFormatedMessage(
					jobClass.getCanonicalName(), launch.getLaunchId(), status, userErrorCode);
		} catch (ParseException e) {
			return e.getMessage();
		} catch (ParserException e) {
			return e.getFormatedMessage();
		} catch (OptionException e) {
			return e.getFormatedMessage();
		} catch (LaunchException e) {
			return LaunchingMessageKind.EQUARTZ0003
					.format(remoteProgram != null ? remoteProgram
							.getAdapterClass() : "unknown");
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
		return RemoteCommand.synclaunch.name();
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
		return ISyncLaunchOption.class;
	}

}
