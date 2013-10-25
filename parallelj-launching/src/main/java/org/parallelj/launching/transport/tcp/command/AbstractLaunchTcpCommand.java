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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.mina.core.session.IoSession;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.parser.ParserException;
import org.parallelj.launching.remote.RemoteProgram;
import org.parallelj.launching.transport.tcp.TcpIpHandlerAdapter;
import org.parallelj.launching.transport.tcp.command.option.IIdOption;
import org.parallelj.launching.transport.tcp.command.option.IOption;
import org.parallelj.launching.transport.tcp.command.option.OptionException;

/**
 * Define a Program launch Command available in a TcpIpServer
 */
abstract class AbstractLaunchTcpCommand extends AbstractTcpCommand {

	/** default number of characters per line */
	public static final int DEFAULT_WIDTH = 74;

	/** default padding to the left of each line */
	public static final int DEFAULT_LEFT_PAD = 4;

	/**
	 * the number of characters of padding to be prefixed to each description
	 * line
	 */
	public static final int DEFAULT_DESC_PAD = 3;

	private List<IOption> options;

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

	public RemoteProgram parseCommandLine(final String... args) throws ParseException,
			OptionException {
		// Get sorted options
		final List<IOption> ioptions = getOptions();
		final Options options = new Options();
		for (IOption ioption : ioptions) {
			options.addOption(ioption.getOption());
		}

		// create the command line parser
		final CommandLineParser parser = new PosixParser();

		// parse the command line arguments
		// CommandLine cmdLine = parser.parse(options, args);
		final CommandLine cmdLine = parser.parse(options, args);
		// As Options are reinitialized after parsing,
		// we need to re-affect IOption.option
		for (Option option : cmdLine.getOptions()) {
			for (IOption ioption : getOptions()) {
				if (ioption.getOption().getOpt().equals(option.getOpt())) {
					ioption.setOption(option);
					break;
				}
			}
		}
		
		// First, we need to get the IIdOption to get the Program
		// We are sure this option is present as it it mandatory!
		IIdOption idOption = null;
		for (IOption ioption : ioptions) {
			if (ioption instanceof IIdOption) {
				idOption = (IIdOption)ioption;
				break;
			}
		}
		final RemoteProgram remoteProgram = idOption.getProgram();
		
		// Check all defined Options with the selected Program
		try {
			for (IOption ioption : ioptions) {
				ioption.ckeckOption(remoteProgram);
			}
			return remoteProgram;
		} catch (ParserException e) {
			throw new OptionException(
					e.getFormatedMessage());
		}
	}

	public List<IOption> getOptions() {
		final Class<? extends IOption> iOptionClass = this.getOptionClass();
		if (iOptionClass != null) {
			if (this.options == null) {
				this.options = new ArrayList<IOption>();
				ServiceLoader<? extends IOption> loader = ServiceLoader.load(iOptionClass, AbstractLaunchTcpCommand.class.getClassLoader());
				if (loader==null || loader.iterator()==null || !loader.iterator().hasNext()) {
					loader = ServiceLoader.load(iOptionClass, Thread.currentThread().getContextClassLoader());
				}
				for (IOption option : loader) {
					this.options.add(option);
				}
			}
		}

		// Sort the list of IOption.
		// The first IOption should be "id" as it is the only once mandatory
		final Comparator<IOption> comparator = new Comparator<IOption>() {
			public int compare(final IOption option1, final IOption option2) {
				if (option1.getOption() != null && option1.getOption().isRequired()) {
					return -1;
				}
				if (option2.getOption() != null && option2.getOption().isRequired()) {
					return 1;
				}
				return 0;
			}
		};
		Collections.sort(this.options, comparator);

		return this.options;
	}

	public abstract Class<? extends IOption> getOptionClass();

	@Override
	public String getHelp() {
		final Writer writer = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(writer);
		final Options options = new Options();
		for (IOption ioption : this.getOptions()) {
			options.addOption(ioption.getOption());
		}
		final HelpFormatter formatter = new HelpFormatter();
		formatter.setSyntaxPrefix("  ");
		final Comparator<Option> comparator = new Comparator<Option>() {
			public int compare(final Option option1, final Option option2) {
				if (option1.isRequired()) {
					return -1;
				}
				if (option2.isRequired()) {
					return 1;
				}
				return 0;
			}
		};
		formatter.setOptionComparator(comparator);
		printWriter.print(getUsage() + TcpIpHandlerAdapter.ENDLINE);
		formatter.printHelp(printWriter, DEFAULT_WIDTH, getType(), null,
				options, DEFAULT_LEFT_PAD, DEFAULT_DESC_PAD, null, true);
		printWriter.flush();
		try {
			writer.flush();
		} catch (IOException e) {
			LaunchingMessageKind.EREMOTE0009.format(e);
		}
		return writer.toString();
	}
}
