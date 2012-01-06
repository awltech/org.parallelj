package org.parallelj.launching.transport.tcp.command.option;

import org.parallelj.launching.transport.tcp.program.TcpIpProgram;

public interface IIdOption extends IOption {
	public TcpIpProgram getProgram() throws OptionException;
}
