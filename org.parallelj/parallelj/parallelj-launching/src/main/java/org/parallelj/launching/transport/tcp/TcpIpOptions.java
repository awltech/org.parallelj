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
	public final int getId() {
		return id;
	}

	/**
	 * Setter method for the Id
	 * 
	 * @param the
	 *            id of the program
	 */
	public final void setId(int id) {
		this.id = id;
	}

	/**
	 * Getter method for the restartId
	 * 
	 * @return the rid of the program
	 */
	public final String getRid() {
		return rid;
	}

	/**
	 * Setter method for the rid
	 * 
	 * @param the
	 *            rid of the program
	 */
	public final void setRid(String rid) {
		this.rid = rid;
	}

	/**
	 * Getter method for the other command line parameters than
	 * options
	 * 
	 * @return a List of String
	 */
	public final List<String> getArguments() {
		return arguments;
	}

	/**
	 * Setter method for the other command line parameters than
	 * options
	 * 
	 * @param arguments
	 */
	public final void setArguments(List<String> arguments) {
		this.arguments = arguments;
	}
}