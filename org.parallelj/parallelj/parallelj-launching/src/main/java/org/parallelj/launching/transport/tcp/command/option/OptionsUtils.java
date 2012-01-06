package org.parallelj.launching.transport.tcp.command.option;

import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.parser.NopParser;
import org.parallelj.launching.parser.Parser;
import org.parallelj.launching.parser.ParserException;
//import org.parallelj.launching.quartz.QuartzUtils;
import org.parallelj.launching.transport.AdaptersArguments;
import org.parallelj.launching.transport.tcp.program.ArgEntry;
import org.parallelj.launching.transport.tcp.program.TcpIpProgram;
import org.parallelj.launching.transport.tcp.program.TcpIpPrograms;
import org.quartz.JobDataMap;

public class OptionsUtils {

	public static void processId(IOption ioption, JobDataMap jobDataMap)
			throws OptionException {
		String strId = ioption.getOption().getValue("id");
		// Check the Id
		try {
			int id = Integer.parseInt(strId);
			if (id >= AdaptersArguments.size()) {
				throw new OptionException(
						LaunchingMessageKind.EREMOTE0004.format(strId));
			}
			//jobDataMap.put(QuartzUtils.getRestartedFireInstanceIdKey(), id);
		} catch (NumberFormatException e) {
			throw new OptionException(LaunchingMessageKind.EREMOTE0008.format(
					"id", strId), e);
		}
	}

	public static void processArgs(IOption ioption, JobDataMap jobDataMap,
			Object... args) throws OptionException, ParserException {
		// Check arguments number
		String[] arguments = ioption.getOption().getValues();
		TcpIpProgram tcpIpProgram = (TcpIpProgram) args[0];
		if (arguments != null) {
			if (tcpIpProgram.getArgEntries().size() > arguments.length) {
				throw new OptionException(
						LaunchingMessageKind.EREMOTE0005.format(
								tcpIpProgram.getAdapterClass(),
								arguments.length));
			}

			// Check arguments format
			checkArgsFormat(tcpIpProgram, arguments);

			int ind = 0;
			for (ArgEntry arg : tcpIpProgram.getArgEntries()) {
				// Do we have to use a Parser?
				Object obj = null;
				if (!arg.getParser().equals(NopParser.class)) {
					try {
						obj = arg.getParser().newInstance()
								.parse(String.valueOf(arguments[ind++]));
					} catch (InstantiationException e) {
						throw new ParserException(
								LaunchingMessageKind.EREMOTE0002.format(e));
					} catch (IllegalAccessException e) {
						throw new ParserException(
								LaunchingMessageKind.EREMOTE0002.format(e));
					}
				} else {
					obj = arguments[ind++];
				}
				jobDataMap.put(arg.getName(), obj);
			}
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
	public static void checkArgsFormat(TcpIpProgram tcpIpProgram, String[] arguments)
			throws OptionException, ParserException {

		int index = 0;
		for (ArgEntry entry : tcpIpProgram.getArgEntries()) {
			Class<?> clazz = entry.getType();
			// If a Parser is defined...
			if (!entry.getParser().equals(NopParser.class)) {
				try {
					Parser parser = entry.getParser().newInstance();
					parser.parse(arguments[index]);
				} catch (InstantiationException e) {
					throw new ParserException(
							LaunchingMessageKind.EREMOTE0007.format(
									entry.getParser().getCanonicalName(), e));
				} catch (IllegalAccessException e) {
					throw new ParserException(
							LaunchingMessageKind.EREMOTE0007.format(
									entry.getParser().getCanonicalName(), e));
				} catch (Exception e) {
					// Other Exceptions...
					throw new OptionException(
							LaunchingMessageKind.EREMOTE0007.format(
									entry.getParser().getCanonicalName(), e));
				}
			} else {
				try {
					// No Parser is defined
					if (clazz.equals(int.class)) {
						if (arguments[index] instanceof String) {
							Integer.valueOf((String) arguments[index]);
						}
					} else if (clazz.equals(long.class)) {
						if (arguments[index] instanceof String) {
							Long.valueOf((String) arguments[index]);
						}
					} else if (clazz.equals(float.class)) {
						if (arguments[index] instanceof String) {
							Float.valueOf((String) arguments[index]);
						}
					} else if (clazz.equals(double.class)) {
						if (arguments[index] instanceof String) {
							Double.valueOf((String) arguments[index]);
						}
					} else if (clazz.equals(boolean.class)) {
						if (arguments[index] instanceof String) {
							Boolean.valueOf((String) arguments[index]);
						}
					} else if (clazz.equals(byte.class)) {
						if (arguments[index] instanceof String) {
							Byte.valueOf((String) arguments[index]);
						}
					} else if (clazz.equals(short.class)) {
						if (arguments[index] instanceof String) {
							Short.valueOf((String) arguments[index]);
						}
					} else if (clazz.equals(char.class)) {
						if (arguments[index] instanceof String) {
							String str = (String) arguments[index];
							if (str.length() == 1) {
								Character.valueOf(str.charAt(0));
							}
						}
					}
				} catch (NumberFormatException e) {
					throw new OptionException(
							LaunchingMessageKind.EREMOTE0006.format(
									arguments[index], e));
				}
			}
			index++;
		}
	}

	public static void checkArgs(IOption ioption,
			TcpIpProgram tcpIpProgram) throws OptionException {
		if (ioption.getOption().getLongOpt().equals("args") && tcpIpProgram.getArgEntries().size()>0) {
			if ( ioption.getOption().getValues() == null
					|| ioption.getOption().getValues().length < tcpIpProgram.getArgEntries().size()) {
				throw new OptionException(
						LaunchingMessageKind.EREMOTE0005.format(tcpIpProgram
								.getAdapterClass().getCanonicalName(),
								tcpIpProgram.getArgEntries().size()));
			}
		}
	}

	public static int checkId(String str) throws OptionException {
		int index = 0;
		try {
			index = Integer.parseInt(str);
		} catch (NumberFormatException e) {
			throw new OptionException(
					LaunchingMessageKind.EREMOTE0006.format(str));
		}
		return index;
	}

	public static TcpIpProgram getProgram(IIdOption iIdOption) throws OptionException {
		int index = checkId(iIdOption.getOption().getValue());
		try {
			TcpIpProgram tcpIpProgram = (TcpIpProgram) (TcpIpPrograms
					.getTcpIpPrograms().get(index));
			return tcpIpProgram;
		} catch (IndexOutOfBoundsException e) {
			throw new OptionException(
					LaunchingMessageKind.EREMOTE0004.format(index));
		}
	}

}
