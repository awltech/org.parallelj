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
package org.parallelj.launching.transport.tcp.command;

import java.util.List;

import org.apache.mina.core.session.IoSession;
import org.kohsuke.args4j.CmdLineException;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.parser.ParserException;
import org.parallelj.launching.quartz.Launch;
import org.parallelj.launching.quartz.LaunchException;
import org.parallelj.launching.quartz.Launcher;
import org.parallelj.launching.quartz.QuartzUtils;
import org.parallelj.launching.transport.AdaptersArguments;
import org.parallelj.launching.transport.AdaptersArguments.AdapterArguments;
import org.parallelj.launching.transport.tcp.TcpIpOptions;
import org.quartz.Job;
import org.quartz.JobDataMap;

/**
 * AsyncLaunch TcpCommand
 * 
 */
public class AsyncLaunch extends AbstractLaunchTcpCommand {

	// private final static String DONE = "Done.";

	private static final int PRIORITY = 70;
	private final String usage = " asynclaunch -id x -rid y params : Launches a new Program instance with ID x, and returns (asynchronous launch).";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.parallelj.launching.transport.tcp.command.AbstractTcpCommand#process
	 * (org.apache.mina.core.session.IoSession, java.lang.String[])
	 */
	@Override
	public final String process(IoSession session, String... args) {
		TcpIpOptions options = null;
		try {
			options = parseCommandLine(args);
		} catch (CmdLineException e) {
			return e.getMessage();
		}

		if (options != null) {
			int id = options.getId();
			List<String> arguments = options.getArguments();

			if (id >= AdaptersArguments.size()) {
				return LaunchingMessageKind.EREMOTE0004.format(id);
			}

			// Get the arguments of the Program
			AdapterArguments adapterArguments = AdaptersArguments
					.getAdapterArgument(id);

			// Verify number of arguments
			if (adapterArguments.getAdapterArguments().size() > arguments
					.size()) {
				return LaunchingMessageKind.EREMOTE0005.format(
						adapterArguments.getAdapterClassName(),
						adapterArguments.getAdapterArguments().size());
			}

			// Check arguments format
			try {
				checkArgsFormat(adapterArguments, arguments);
			} catch (NumberFormatException e) {
				return LaunchingMessageKind.EREMOTE0006.format();
			} catch (ParserException e) {
				return LaunchingMessageKind.EREMOTE0007
						.format(e.getParser(), e);
			}

			String adapterClassName = adapterArguments.getAdapterClassName();

			try {
				Class<?> adapterClass = Class.forName(adapterClassName);

				@SuppressWarnings("unchecked")
				Class<? extends Job> jobClass = (Class<? extends Job>) adapterClass;
				Launcher launcher = Launcher.getLauncher();

				JobDataMap jobDataMap = buildJobDataMap(adapterArguments,
						arguments.toArray());
				String jobId = options.getRid();
				// Is there a restartId?
				if (jobId == null || jobId.trim().length() == 0) {
					if (adapterArguments.getAdapterArguments().size() > arguments
							.size()) {
						return LaunchingMessageKind.EREMOTE0005
								.getFormatedMessage(adapterArguments
										.getAdapterClassName(),
										adapterArguments.getAdapterArguments()
												.size());
					}
				}
				jobDataMap.put(QuartzUtils.getRestartedFireInstanceIdKey(), jobId);

				Launch launch = launcher.newLaunch(jobClass)
						.addDatas(jobDataMap).aSynchLaunch();
				return LaunchingMessageKind.IQUARTZ0002.getFormatedMessage(
						jobClass.getCanonicalName(), launch.getLaunchId());
			} catch (LaunchException e) {
				return LaunchingMessageKind.EQUARTZ0003
						.format(adapterClassName);
			} catch (ClassNotFoundException e) {
				return LaunchingMessageKind.EREMOTE0001
						.format(adapterClassName);
			}
		} else {
			return LaunchingMessageKind.EREMOTE0002.format((Object[]) args);
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
		return this.usage;
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

}
