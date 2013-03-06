package org.parallelj.launching.inout;

import java.util.List;

public interface IProgramInputOutputs {
	public List<Argument> getArguments();
	public void addArgument(Argument argument);
	public Argument getArgument(String name);
	public void addOutput(Output output);
	public List<Output> getOutputs();
	public Output getOutput(String name);
}
