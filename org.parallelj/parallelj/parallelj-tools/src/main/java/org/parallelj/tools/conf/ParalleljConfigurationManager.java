package org.parallelj.tools.conf;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.bind.JAXB;

public class ParalleljConfigurationManager {

	/*
	 * The configuration file for Parallelj.
	 * This file must be in the classpath
	 * 
	 */
	public static final String CONFIGURATION_FILE = "/parallelj.xml";
	
	private static ParalleljConfiguration configuration;
	
	public static synchronized ParalleljConfiguration getConfiguration() {
		if (configuration == null) {
			initialize();
		}
		return configuration;
	}
	
	public static synchronized void reload() {
		initialize();
	}

	private static void initialize() {
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		Reader reader = null;
		try {
			inputStream = InputStream.class.getResourceAsStream(CONFIGURATION_FILE);
			inputStreamReader = new InputStreamReader(inputStream);
			reader = new BufferedReader(inputStreamReader);
			configuration = JAXB.unmarshal(reader, ParalleljConfiguration.class);
		} finally {
			try {
				reader.close();
				inputStreamReader.close();
				inputStream.close();
			} catch (IOException e) {}
		}
	}
}
