/**
 * 
 */
package org.parallelj.launching.mbeans;

/**
 * @author a169210
 * 
 */
public interface ConfigurationHandlerRemoteMBean {

	/**
	 * reload the configuration
	 * 
	 * @return String
	 */
	public String reloadConfiguration();

	/**
	 * Display the security configuration
	 * 
	 * @return String
	 */
	public String displayConfiguration();

}
