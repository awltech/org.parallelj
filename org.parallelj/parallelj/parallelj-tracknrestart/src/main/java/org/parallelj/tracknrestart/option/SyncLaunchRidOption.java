package org.parallelj.tracknrestart.option;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.parallelj.launching.parser.ParserException;
import org.parallelj.launching.transport.tcp.command.option.ISyncLaunchOption;
import org.parallelj.launching.transport.tcp.command.option.OptionException;
import org.parallelj.launching.transport.tcp.program.TcpIpProgram;
import org.parallelj.tracknrestart.plugins.TrackNRestartPluginAll;
import org.quartz.JobDataMap;

/**
 * Define the rid Option for the SynchLaunch command.
 *
 */
public class SyncLaunchRidOption implements ISyncLaunchOption {

	private Option option;
	
	public SyncLaunchRidOption() {
		this.option = OptionBuilder.create("r");
		this.option.setLongOpt("rid");
		this.option.setArgs(1);
		this.option.setArgName("rid");
		this.option.setDescription("Restart Id of an already launched Program");
		this.option.setRequired(false);
	}

	public Option getOption() {
		return this.option;
	}

	public int getPriority() {
		return 90;
	}

	@Override
	public void setOption(Option option) {
		this.option = option;
	}

	@Override
	public void ckeckOption(TcpIpProgram tcpIpProgram) throws OptionException,
			ParserException {
		if (this.getOption().getValue()!= null && this.getOption().getValue().length()==0) {
			throw new OptionException("The restart Id must be specified!");
		}
	}

	@Override
	public void process(JobDataMap jobDataMap, Object... args)
			throws OptionException, ParserException {
		String restartId = this.getOption().getValue();
		jobDataMap.put(TrackNRestartPluginAll.RESTARTED_FIRE_INSTANCE_ID, restartId);
	}
}
