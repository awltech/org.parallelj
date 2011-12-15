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

import static org.quartz.JobBuilder.newJob;

import java.util.List;

import org.apache.mina.core.session.IoSession;
import org.kohsuke.args4j.CmdLineException;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.parser.ParserException;
import org.parallelj.launching.quartz.AdapterJobsRunner;
import org.parallelj.launching.quartz.ParalleljScheduler;
import org.parallelj.launching.quartz.ParalleljSchedulerFactory;
import org.parallelj.launching.transport.AdaptersArguments;
import org.parallelj.launching.transport.AdaptersArguments.AdapterArguments;
import org.parallelj.launching.transport.tcp.TcpIpOptions;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;

/**
 * SuncLaunch TcpCommand
 *
 */
public class SyncLaunch extends AbstractLaunchTcpCommand {
	
	private static final int PRIORITY=80;

	/* (non-Javadoc)
	 * @see org.parallelj.launching.transport.tcp.command.AbstractTcpCommand#process(org.apache.mina.core.session.IoSession, java.lang.String[])
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
			String rid = options.getRid();
			List<String> arguments = options.getArguments();
			
			if (id >= AdaptersArguments.size()) {
				return LaunchingMessageKind.EREMOTE0004.format(id);
			}
			
			// Get the arguments of the Program
			AdapterArguments adapterArguments = AdaptersArguments.getAdapterArgument(id);
			
			// Verify number of arguments
			if (adapterArguments.getAdapterArguments().size()>arguments.size()) {
				return LaunchingMessageKind.EREMOTE0005.format(adapterArguments.getAdapterClassName(), adapterArguments.getAdapterArguments().size());
			}

			// Check arguments format
			try {
				checkArgsFormat(adapterArguments, arguments);
			} catch (NumberFormatException e) {
				return LaunchingMessageKind.EREMOTE0006.format();				
			} catch (ParserException e) {
				return LaunchingMessageKind.EREMOTE0007.format(e.getParser(), e);				
			}
			
			String adapterClassName = adapterArguments.getAdapterClassName();
			
			try {
				Class<?> adapterClass = Class.forName(adapterClassName);
				
				// First we must get a reference to a scheduler
				ParalleljScheduler scheduler = (new ParalleljSchedulerFactory())
						.getScheduler();

				// define the job and tie it to our HelloJob class
				@SuppressWarnings("unchecked")
				JobBuilder jobBuilder = newJob((Class<Job>) adapterClass);
				JobDetail job = jobBuilder.withIdentity(String.valueOf(jobBuilder),
						String.valueOf(jobBuilder)).build();

				// Is there a restartId?
				if (rid != null && rid.length()>0) {
					job.getJobDataMap().put("restartId", rid);
				}
				// initialize others arguments for Quartz
				addAdapterArgumentsToJobDataMap(job, adapterArguments, arguments.toArray());

				return String.valueOf(AdapterJobsRunner.syncLaunch(scheduler, job));
			} catch (SchedulerException e) {
				return LaunchingMessageKind.EQUARTZ0003.format(adapterClassName);
			} catch (ClassNotFoundException e) {
				return LaunchingMessageKind.EREMOTE0001.format(adapterClassName);
			}
		} else {
			return LaunchingMessageKind.EREMOTE0002.format((Object[])args);
		}
	}

	/* (non-Javadoc)
	 * @see org.parallelj.launching.transport.tcp.command.AbstractTcpCommand#getType()
	 */
	public String getType() {
		return RemoteCommand.synclaunch.name();
	}

	/* (non-Javadoc)
	 * @see org.parallelj.launching.transport.tcp.command.AbstractTcpCommand#getUsage()
	 */
	@Override
	public String getUsage() {
		return "  synclaunch -id x -rid y params : Launches a new Program instance with ID x, waits till return status (synchronous launch).";
	}

	/* (non-Javadoc)
	 * @see org.parallelj.launching.transport.tcp.command.AbstractTcpCommand#getPriorityUsage()
	 */
	@Override
	public int getPriorityUsage() {
		return PRIORITY;
	}

}
