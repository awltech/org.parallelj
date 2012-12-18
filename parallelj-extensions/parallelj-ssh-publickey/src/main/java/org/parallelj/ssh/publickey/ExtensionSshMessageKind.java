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
	
	static Formatter<ExtensionSshMessageKind> formatter = new Formatter<ExtensionSshMessageKind>(ExtensionSshMessageKind.class);


}
