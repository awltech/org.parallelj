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
package org.parallelj.ssh.publickey;

import org.parallelj.internal.util.Formatter;
import org.parallelj.internal.util.Formatter.Format;


public enum ExtensionSshMessageKind {
	
	/**
	 * @Info Unable to read key %s
	 */
	@Format("Info Unable to read key %s..")
	ISH0001,
	
	/**
	 * @Info Access denied.
	 */
	@Format("Access denied.")
	ISH0002,
	
	/**
	 * @Warning No private key file found for the SSH server. The embedded one will be use..
	 */
	@Format("Warning No private key file found for the SSH server. The embedded one will be use..")
	WSH0001,
	
	/**
	 * @Error launching EasyFlow Plugin [%s]. Check the easyflow configuration: [%s].
	 */
	@Format("Error initializing EasyFlow Plugin [%s]. Check the easyflow configuration: [%s] ")
	ESH0001,
	
	/**
	 * @Error No private key file found for the SSH server.
	 */
	@Format("No private key file found for the SSH server. ")
	ESH0002,
	
	/**
	 * @Error Invalid authorized keys file.
	 */
	@Format("Invalid authorized keys file. [%s]")
	ESH0003;
	
	/**
	 * Method used to format a message
	 * 
	 * @param args
	 *            the arguments used to format the message
	 * @return the formatted message
	 */
	public String format(Object... args) {
		// delegates to formatter
		return formatter.print(this, args);
	}
	
	private static Formatter<ExtensionSshMessageKind> formatter = new Formatter<ExtensionSshMessageKind>(ExtensionSshMessageKind.class);


}
