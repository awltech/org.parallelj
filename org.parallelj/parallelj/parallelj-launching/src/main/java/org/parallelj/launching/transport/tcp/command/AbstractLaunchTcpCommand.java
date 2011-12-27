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
import org.kohsuke.args4j.CmdLineParser;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.parser.NopParser;
import org.parallelj.launching.parser.Parser;
import org.parallelj.launching.parser.ParserException;
import org.parallelj.launching.transport.AdaptersArguments.AdapterArguments;
import org.parallelj.launching.transport.ArgEntry;
import org.parallelj.launching.transport.tcp.TcpIpOptions;
import org.quartz.JobDataMap;

/**
 * Define a Program launch Command available in a TcpIpServer
 */
abstract class AbstractLaunchTcpCommand extends AbstractTcpCommand {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.parallelj.launching.transport.tcp.command.TcpCommand#process(org.
	 * apache.mina.core.session.IoSession, java.lang.String[])
	 */
	public abstract String process(IoSession session, String... args);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.parallelj.launching.transport.tcp.command.TcpCommand#getType()
	 */
	public abstract String getType();

	public TcpIpOptions parseCommandLine(String... args)
			throws CmdLineException {
		TcpIpOptions options = new TcpIpOptions();
		CmdLineParser parser = new CmdLineParser(options);
		parser.parseArgument(args);
		return options;
	}

	protected JobDataMap buildJobDataMap(AdapterArguments adapterArguments,
			Object[] params) {
		JobDataMap jobDataMap = new JobDataMap();
		/*
		 * if no restartId: this.adapterArgs.size() == params[].length ==
		 * signature[].length if a restartId: this.adapterArgs.size()+1 ==
		 * params[].length == signature[].length In this case, params[0] is the
		 * restartId If it is not the case, there is an error in initializing
		 * JMX description methods for the Adpater MBean.
		 */
		try {
			int ind = 0;
			for (ArgEntry arg : adapterArguments.getAdapterArguments()) {
				// Do we have to use a Parser?
				Object obj = null;
				if (!arg.getParser().equals(NopParser.class)) {
					obj = arg.getParser().newInstance()
							.parse(String.valueOf(params[ind++]));
				} else {
					obj = params[ind++];
				}
				jobDataMap.put(arg.getName(), obj);
			}
		} catch (InstantiationException e) {
			LaunchingMessageKind.EREMOTE0002.format();
		} catch (IllegalAccessException e) {
			LaunchingMessageKind.EREMOTE0002.format();
		}
		return jobDataMap;
	}

	/**
	 * Check the validity of arguments values coming from remote launching with
	 * regard to types. If not done, the Quartz Exception thrown doesn't
	 * stop the launch and Program may be launched with invalid arguments
	 * values.
	 * 
	 * @param adapterArguments
	 * @param arguments
	 * @throws ParserException
	 */
	protected void checkArgsFormat(AdapterArguments adapterArguments,
			List<String> arguments) throws ParserException {

		int index = 0;
		for (ArgEntry entry : adapterArguments.getAdapterArguments()) {
			Class<?> clazz = entry.getType();
			// If a Parser is defined...
			if (!entry.getParser().equals(NopParser.class)) {
				try {
					Parser parser = entry.getParser().newInstance();
					parser.parse(arguments.get(index));
				} catch (InstantiationException e) {
					throw new ParserException(
							String.valueOf(entry.getParser()), e);
				} catch (IllegalAccessException e) {
					throw new ParserException(entry.getParser()
							.getCanonicalName(), e);
				} catch (Exception e) {
					// Other Exceptions...
					throw new ParserException(entry.getParser()
							.getCanonicalName(), e);
				}
			} else {
				// No Parser is defined
				if (clazz.equals(int.class)) {
					if (arguments.get(index) instanceof String) {
						Integer.valueOf((String) arguments.get(index));
					}
				} else if (clazz.equals(long.class)) {
					if (arguments.get(index) instanceof String) {
						Long.valueOf((String) arguments.get(index));
					}
				} else if (clazz.equals(float.class)) {
					if (arguments.get(index) instanceof String) {
						Float.valueOf((String) arguments.get(index));
					}
				} else if (clazz.equals(double.class)) {
					if (arguments.get(index) instanceof String) {
						Double.valueOf((String) arguments.get(index));
					}
				} else if (clazz.equals(boolean.class)) {
					if (arguments.get(index) instanceof String) {
						Boolean.valueOf((String) arguments.get(index));
					}
				} else if (clazz.equals(byte.class)) {
					if (arguments.get(index) instanceof String) {
						Byte.valueOf((String) arguments.get(index));
					}
				} else if (clazz.equals(short.class)) {
					if (arguments.get(index) instanceof String) {
						Short.valueOf((String) arguments.get(index));
					}
				} else if (clazz.equals(char.class)) {
					if (arguments.get(index) instanceof String) {
						String str = (String) arguments.get(index);
						if (str.length() == 1) {
							Character.valueOf(str.charAt(0));
						}
					}
				}
			}
			index++;
		}
	}
}
