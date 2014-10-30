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
