package org.parallelj.launching.inout;

import java.util.ArrayList;
import java.util.List;

public aspect InOutManagement {

	/*
	 * Add the interface IProgramArguments to the KProgram
	*/
	declare parents: org.parallelj.internal.kernel.KProgram implements IProgramInputOutputs;

	public List<Argument> IProgramInputOutputs.arguments = new ArrayList<Argument>();
	public List<Output> IProgramInputOutputs.outputs = new ArrayList<Output>();
	
	public List<Argument> IProgramInputOutputs.getArguments() {
		return this.arguments;
	}
	
	public void IProgramInputOutputs.addArgument(Argument argument) {
		this.arguments.add(argument);
	}
	
	public Argument IProgramInputOutputs.getArgument(String name) {
		for (Argument argument : this.arguments) {
			if (argument.getName().equals(name)) {
				return argument;
			}
		}
		return null;
	}
	
	public List<Output> IProgramInputOutputs.getOutputs() {
		return this.outputs;
	}
	
	public void IProgramInputOutputs.addOutput(Output output) {
		this.outputs.add(output);
	}
	
	public Output IProgramInputOutputs.getOutput(String name) {
		for (Output output : this.outputs) {
			if (output.getName().equals(name)) {
				return output;
			}
		}
		return null;
	}

}
