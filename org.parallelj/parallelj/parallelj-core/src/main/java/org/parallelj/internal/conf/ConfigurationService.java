package org.parallelj.internal.conf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

public class ConfigurationService {

	private static ConfigurationService configurationService;
	
	public static synchronized ConfigurationService getConfigurationService() {
		if (configurationService == null) {
			configurationService = new ConfigurationService();
		}
		return configurationService;
	}
	
	public ConfigurationManager getConfigurationManager() {
		List<ConfigurationManager> lstConf = new ArrayList<ConfigurationManager>();
		ServiceLoader<ConfigurationManager> loader = ServiceLoader.load(ConfigurationManager.class);
		for (ConfigurationManager conf:loader) {
			lstConf.add(conf);
		}
		return Collections.max(lstConf);
	}
	
}
