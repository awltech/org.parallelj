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
package org.parallelj.internal.conf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.bind.JAXB;

import org.parallelj.internal.MessageKind;

public abstract class AbstractConfigurationManager implements ConfigurationManager {

	private Object configuration;
	
	@Override
	public int compareTo(ConfigurationManager o) {
		return this.getPriority()-o.getPriority();
	}

	public abstract Class<?> getConfigurationObjectClass();

	public Object getConfiguration() {
		if (this.configuration == null) {
			initialize();
		}
		return this.configuration;
	}

	public abstract String getConfigurationFile();

	public abstract int getPriority();

	@Override
	public void reloadConfiguration() {
		initialize();
	}

	private void initialize() {
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		Reader reader = null;
		inputStream = this.getConfigurationObjectClass().getResourceAsStream(this.getConfigurationFile());
		if (inputStream == null) {
			inputStream = this.getConfigurationObjectClass().getClassLoader().getResourceAsStream(this.getConfigurationFile());
		}
		if (inputStream == null) {
			MessageKind.E0002.format(this.getConfigurationFile());
		}
		try {
			inputStreamReader = new InputStreamReader(inputStream);
			reader = new BufferedReader(inputStreamReader);
			this.configuration = JAXB.unmarshal(reader, getConfigurationObjectClass());
		} finally {
			try {
				reader.close();
				inputStreamReader.close();
				inputStream.close();
			} catch (IOException e) {}
		}
	}
}
