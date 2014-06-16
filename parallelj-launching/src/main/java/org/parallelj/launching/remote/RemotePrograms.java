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
package org.parallelj.launching.remote;

import java.util.ArrayList;
import java.util.List;

import org.parallelj.Program;
import org.parallelj.internal.conf.ConfigurationService;
import org.parallelj.internal.conf.ParalleljConfigurationManager;
import org.parallelj.internal.conf.pojos.CBean;
import org.parallelj.internal.conf.pojos.ParalleljConfiguration;
import org.parallelj.internal.reflect.Adapter;


public final class RemotePrograms {
	private List<RemoteProgram> remotePrograms = new ArrayList<RemoteProgram>();
	
	/**
	 * The instance of RemoteProgram
	 */
	private static RemotePrograms instance = new RemotePrograms();

	/**
	 * Default constructor
	 */
	private RemotePrograms() {
		// Search for all available Program and print it's name and parameters
		// Available Programs are defined in parallej.xml as MBeans
		ParalleljConfiguration configuration = (ParalleljConfiguration) ConfigurationService
				.getConfigurationService().getConfigurationManager()
				.get(ParalleljConfigurationManager.class).getConfiguration();
		if (configuration.getServers().getBeans() != null
				&& configuration.getServers().getBeans().getBean() != null) {
			for (CBean bean : configuration.getServers().getBeans().getBean()) {
				/*
				 * List of types annotated with @In and its Parser class:
				 * adapterArgs[0] : the attribute name adapterArgs[1] : the
				 * canonical name of the corresponding parser class
				 */
				try {
					@SuppressWarnings("unchecked")
					Class<?> clazz = (Class<?>) Class
							.forName(bean.getClazz());
					//
					if(clazz.isAnnotationPresent(Program.class)) {
						this.remotePrograms.add(new RemoteProgram((Class<? extends Adapter>)
								clazz));
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static synchronized List<RemoteProgram> getRemotePrograms() {
		return instance.remotePrograms;
	}
	
	public static synchronized RemoteProgram getRemoteProgram(String type) {
		for (RemoteProgram remoteProgam: instance.remotePrograms) {
			if (remoteProgam.getAdapterClass().getCanonicalName().equals(type)) {
				return remoteProgam;
			}
		}
		return null;
	}

}
