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

import java.util.Iterator;

import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.inout.Argument;
import org.parallelj.launching.parser.ParserException;
import org.parallelj.launching.remote.RemoteProgram;
import org.parallelj.launching.remote.RemotePrograms;
import org.quartz.JobDataMap;

public final class OptionsUtils {
	
	private OptionsUtils() {
	}

	public static void processId(final IOption ioption, final JobDataMap jobDataMap)
			throws OptionException {
		String strId=null;
		try {
			strId = ioption.getOption().getValue("id");
			// Check the Id
			int id = Integer.parseInt(strId);
			if (id >= RemotePrograms.getRemotePrograms().size()) {
				throw new OptionException(
						LaunchingMessageKind.EREMOTE0004.format(strId));
			}
			//jobDataMap.put(QuartzUtils.getRestartedFireInstanceIdKey(), id);
		} catch (NumberFormatException e) {
			throw new OptionException(LaunchingMessageKind.EREMOTE0008.format(
					"id", strId), e);
		}
	}

	public static void processArgs(final IOption ioption, final JobDataMap jobDataMap,
			final Object... args) throws OptionException, ParserException {
		// Check arguments number
		final String[] arguments = ioption.getOption().getValues();
		final RemoteProgram remoteProgram = (RemoteProgram) args[0];
		if (arguments != null) {
			if (remoteProgram.getArguments().size() > arguments.length) {
				throw new OptionException(
						LaunchingMessageKind.EREMOTE0005.format(
								remoteProgram.getAdapterClass(),
								arguments.length));
			}

			// Initialize arguments format
			initializeArg(remoteProgram, arguments, jobDataMap);
		}
	}

	/**
	 * Check the validity of arguments values coming from remote launching with
	 * regard to types. If not done, the Quartz Exception thrown doesn't stop
	 * the launch and Program may be launched with invalid arguments values.
	 * 
	 * @param adapterArguments
	 * @param arguments
	 * @throws ParserException
	 */
	public static void initializeArg(final RemoteProgram remoteProgram, final String[] arguments, final JobDataMap jobDataMap)
			throws OptionException, ParserException {

		int numberOfEquals = 0;
		for (String argument : arguments) {
			if (argument.indexOf('=') > -1)
				numberOfEquals++;
		}
		
		if (numberOfEquals != arguments.length)
			throw new OptionException(LaunchingMessageKind.WREMOTE001.format(remoteProgram.getClass().getCanonicalName()));
		
		for (String argument : arguments) {
			if (argument.indexOf("=") > 0) {
				String[] arg = argument.split("=");
				String argName = arg[0];
				String argValue = null;
				if (arg.length>1)
					argValue = arg[1];
				
				if (argValue != null && argValue.length() == 0)
					argValue = null;
				
				if (argValue!=null && argValue.charAt(0) == '"' && argValue.charAt(argValue.length()-1) == '"') {
					argValue = argValue.substring(1, argValue.length()-1);
				}
				
				Iterator<Argument> argumentIterator = remoteProgram.getArguments().iterator();
				boolean found = false;
				while (argumentIterator.hasNext() && !found) {
				Argument a = argumentIterator.next(); {
					try {
						if (a.getName().equals(argName)) {
							a.setValueUsingParser(argValue);
							found = true;
						}
					} catch (Exception e) {
						throw new OptionException(LaunchingMessageKind.EREMOTE0010.format(a.getName(), remoteProgram.getClass().getCanonicalName()), e);
					}
				}
				}	
			} else {
				throw new OptionException(LaunchingMessageKind.EREMOTE0011.format(remoteProgram.getClass().getCanonicalName()));
			}
		}
	}

	public static void checkArgs(final IOption ioption,
			final RemoteProgram remoteProgram) throws OptionException {
		if (ioption.getOption().getLongOpt().equals("args") 
				&& remoteProgram.getArguments().size()>0
				&& ( ioption.getOption().getValues() == null
					|| ioption.getOption().getValues().length < remoteProgram.getArguments().size())) {
			throw new OptionException(
					LaunchingMessageKind.EREMOTE0005.format(remoteProgram
							.getAdapterClass().getCanonicalName(),
							remoteProgram.getArguments().size()));
		}
	}

	public static int checkId(final String str) throws OptionException {
		int index = 0;
		try {
			index = Integer.parseInt(str);
		} catch (NumberFormatException e) {
			throw new OptionException(
					LaunchingMessageKind.EREMOTE0006.format(str));
		}
		return index;
	}

	public static RemoteProgram getProgram(final IIdOption iIdOption) throws OptionException {
		final int index = checkId(iIdOption.getOption().getValue());
		try {
			final RemoteProgram remoteProgram = (RemoteProgram) (RemotePrograms
					.getRemotePrograms().get(index));
			return remoteProgram;
		} catch (IndexOutOfBoundsException e) {
			throw new OptionException(
					LaunchingMessageKind.EREMOTE0004.format(index));
		}
	}

}
