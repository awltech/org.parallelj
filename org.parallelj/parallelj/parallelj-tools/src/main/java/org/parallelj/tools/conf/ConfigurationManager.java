package org.parallelj.tools.conf;

import java.io.File;

import javax.xml.bind.JAXB;

public class ConfigurationManager {

	public static final String CONFIGURATION_FILE = "parallelj.xml";
	
	private static ParalleljConfiguration configuration;
	
	public static ParalleljConfiguration getConfiguration() {
		if (configuration == null) {
			initialize();
		}
		return configuration;
	}
	
	public static void reload() {
		initialize();
	}

	private static synchronized void initialize() {
		File file = new File(CONFIGURATION_FILE);
		configuration = JAXB.unmarshal(file, ParalleljConfiguration.class);
	}
}
