package org.parallelj.internal;

import org.parallelj.internal.util.Formatter;
import org.parallelj.internal.util.Formatter.Format;

/**
 * This enumeration contains the types of messages logged by //J.
 * 
 * @author Atos Worldline
 * @since 0.4.0
 */
public enum MessageKind {
	
	/**Error: invalid argument when creating a new Processor.
	 * 
	 */
	@Format("Invalid argument [%2$s] for kind [%1$s]")
	E0001,
	
	/**
	 * Info: building a program
	 */
	@Format("building program [%s]")
	I0001,
	
	/**
	 * Warning: member not found in a type.
	 */
	@Format("member [%s] not found in type [%s]")	
	W0001,
	
	/**
	 * Warning: a type is not assignable to another one.
	 * 
	 */
	W0002,
	
	/**
	 * Warning: runtime exception caught by //J. 
	 * 
	 */
	W0003,
	
	/**
	 * Warning: operation interrupted
	 */
	@Format("Operation interrupted [%s]")
	W0004;
	
	
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

	
	static Formatter<MessageKind> formatter = new Formatter<MessageKind>(MessageKind.class);

}
