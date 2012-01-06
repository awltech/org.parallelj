package org.parallelj.launching.transport.tcp.command.option;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.parallelj.launching.parser.ParserException;
import org.parallelj.launching.transport.tcp.program.TcpIpProgram;
import org.quartz.JobDataMap;

public class AsyncLaunchIdOption implements IAsyncLaunchOption, IIdOption {

	private Option option;

	public AsyncLaunchIdOption() {
		this.option = OptionBuilder.create("i");
		this.option.setLongOpt("id");
		this.option.setArgs(1);
		this.option.setArgName("id");
		this.option
				.setDescription("Id of the Program from the list return by ll command");
		this.option.setRequired(true);
	}

	public Option getOption() {
		return this.option;
	}

	public int getPriority() {
		return 100;
	}

	@Override
	public void process(JobDataMap jobDataMap, Object... args)
			throws OptionException, ParserException {
		OptionsUtils.processId(this, jobDataMap);
	}

	@Override
	public void setOption(Option option) {
		this.option = option;
	}

	@Override
	public void ckeckOption(TcpIpProgram tcpIpProgram) throws OptionException,
			ParserException {
		// Do nothing
	}

	@Override
	public TcpIpProgram getProgram() throws OptionException {
		return OptionsUtils.getProgram(this);
	}
}
