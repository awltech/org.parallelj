package org.parallelj.launching.transport.tcp.command.option;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.parallelj.launching.parser.ParserException;
import org.parallelj.launching.transport.tcp.program.TcpIpProgram;
import org.quartz.JobDataMap;

public class SyncLaunchArgsOption implements ISyncLaunchOption {

	private Option option;

	public SyncLaunchArgsOption() {
		this.option = OptionBuilder.create("a");
		this.option.setLongOpt("args");
		this.option.setArgs(100);
		this.option.setArgName("args...");
		this.option.setDescription("Arguments of the Program");
		this.option.setRequired(false);
	}

	public Option getOption() {
		return this.option;
	}

	public int getPriority() {
		return 0;
	}

	@Override
	public void process(JobDataMap jobDataMap, Object... args)
			throws OptionException, ParserException {
		OptionsUtils.processArgs(this, jobDataMap, args);
	}

	@Override
	public void setOption(Option option) {
		this.option = option;
	}

	@Override
	public void ckeckOption(TcpIpProgram tcpIpProgram) throws OptionException,
			ParserException {
		OptionsUtils.checkArgs(this, tcpIpProgram);
	}

}
