/**
 * 
 */
package org.parallelj.launching.mbeans;

import java.io.StringWriter;

import javax.xml.bind.JAXB;

import org.parallelj.internal.conf.ConfigurationManager;
import org.parallelj.internal.conf.ConfigurationService;
import org.parallelj.internal.conf.pojos.ParalleljConfiguration;
import org.parallelj.launching.LaunchingMessageKind;

/**
 * 
 * Mbean to handle all the Configuration related task like reloading or
 * displaying the configuration.
 */
public class ConfigurationHandlerRemote implements
		ConfigurationHandlerRemoteMBean {

	ParalleljConfiguration paralleljConfiguration = null;

	/**
	 * This method is used to reload the configurations from all configuration
	 * files.
	 * 
	 * @return String
	 */
	public String reloadConfiguration() {
		ConfigurationService.getConfigurationService().reloadConfiguration();
		return LaunchingMessageKind.ICONF002.getFormatedMessage();
	}

	/**
	 * This method is used to display the configurations data loaded from all
	 * configuration files.
	 * 
	 * @return String
	 */
	public String displayConfiguration() {
		StringBuffer result = new StringBuffer();

		for (ConfigurationManager confManager : ConfigurationService
				.getConfigurationService().getConfigurationManager().values()) {
			Object configuration = confManager.getConfiguration();
			StringWriter writer = new StringWriter();
			JAXB.marshal(configuration, writer);
			result.append(writer.getBuffer());
			result.append("\n");
		}
		;

		return result.toString();
	}

}
