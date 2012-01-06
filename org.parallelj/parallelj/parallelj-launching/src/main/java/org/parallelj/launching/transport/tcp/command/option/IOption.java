package org.parallelj.launching.transport.tcp.command.option;

import org.apache.commons.cli.Option;
import org.parallelj.launching.parser.ParserException;
import org.parallelj.launching.transport.tcp.program.TcpIpProgram;
import org.quartz.JobDataMap;

/**
 * Define an Option for a remote Command.
 *
 */
public interface IOption {
	public Option getOption();

	public void setOption(Option option);

	public int getPriority();

	/**
	 * Check the current Option. Example of what to check:
	 * <p>
	 * The value passed for the Option
	 * </p>
	 * <p>
	 * The arguments passed to this Option (if defined)
	 * </p>
	 * 
	 * @param tcpIpProgram
	 * @throws OptionException
	 * @throws ParserException
	 */
	public void ckeckOption(TcpIpProgram tcpIpProgram) throws OptionException,
			ParserException;

	/**
	 * Process the current Option. Example of process:
	 * <p>
	 * Initialize some JobDataMap values with the Option
	 * </p>
	 * <p>
	 * Initialize some JobDataMap values with the arguments passed to this
	 * Option (if defined)
	 * </p>
	 * 
	 * @param jobDataMap
	 * @param args
	 * @throws OptionException
	 * @throws ParserException
	 */
	public void process(JobDataMap jobDataMap, Object... args)
			throws OptionException, ParserException;
}
