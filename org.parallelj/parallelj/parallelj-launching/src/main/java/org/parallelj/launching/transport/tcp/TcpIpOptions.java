package org.parallelj.launching.transport.tcp;

import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

/**
 * Class to represent all options available for a launch command in a TcpIp
 * client according to args4j
 * 
 */
public class TcpIpOptions {
	/**
	 * The Id of an available Program
	 */
	@Option(name = "-id", usage = "program id", required=true)
	private int id;

	/**
	 * The restartId of an available Program
	 */
	@Option(name = "-rid", usage = "program restart id")
	private String rid;

	/**
	 * receives other command line parameters than options
	 */
	@Argument
	private List<String> arguments = new ArrayList<String>();

	/**
	 * Getter method for the Id
	 * 
	 * @return the id of the program
	 */
	public int getId() {
		return id;
	}

	/**
	 * Setter method for the Id
	 * 
	 * @param the
	 *            id of the program
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Getter method for the restartId
	 * 
	 * @return the rid of the program
	 */
	public String getRid() {
		return rid;
	}

	/**
	 * Setter method for the rid
	 * 
	 * @param the
	 *            rid of the program
	 */
	public void setRid(String rid) {
		this.rid = rid;
	}

	/**
	 * Getter method for the other command line parameters than
	 * options
	 * 
	 * @return a List of String
	 */
	public List<String> getArguments() {
		return arguments;
	}

	/**
	 * Setter method for the other command line parameters than
	 * options
	 * 
	 * @param arguments
	 */
	public void setArguments(List<String> arguments) {
		this.arguments = arguments;
	}
}