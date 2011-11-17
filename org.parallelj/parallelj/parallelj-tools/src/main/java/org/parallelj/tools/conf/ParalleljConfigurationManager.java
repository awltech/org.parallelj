/*
 *     ParallelJ, framework for parallel computing
 *
 *     Copyright (C) 2010 Atos Worldline or third-party contributors as
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

package org.parallelj.tools.conf;

import java.io.BufferedReader;
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
