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
package org.parallelj.launching.transport.tcp.program;

import java.util.ArrayList;
import java.util.List;

import org.parallelj.internal.conf.ConfigurationService;
import org.parallelj.internal.conf.ParalleljConfigurationManager;
import org.parallelj.internal.conf.pojos.CBean;
import org.parallelj.internal.conf.pojos.ParalleljConfiguration;
import org.parallelj.internal.reflect.ProgramAdapter.Adapter;


public final class TcpIpPrograms {
	private List<TcpIpProgram> tcpIpPrograms = new ArrayList<TcpIpProgram>();
	
	/**
	 * The instance of TcpIpCommands
	 */
	private static TcpIpPrograms instance = new TcpIpPrograms();

	/**
	 * Default constructor
	 */
	private TcpIpPrograms() {
		// Search for all available Program and print it's name and parameters
		// Available Programs are defined in parallej.xml as MBeans
		ParalleljConfiguration configuration = (ParalleljConfiguration) ConfigurationService
				.getConfigurationService().getConfigurationManager()
				.get(ParalleljConfigurationManager.class).getConfiguration();
		if (configuration.getServers().getBeans() != null
				&& configuration.getServers().getBeans().getBean() != null) {
			for (CBean bean : configuration.getServers().getBeans().getBean()) {
				//
				/*
				 * List of types annotated with @In and its Parser class:
				 * adapterArgs[0] : the attribute name adapterArgs[1] : the
				 * canonical name of the corresponding parser class
				 */
				//List<ArgEntry> adapterArgs = new ArrayList<ArgEntry>();
				try {
					@SuppressWarnings("unchecked")
					Class<? extends Adapter> clazz = (Class<? extends Adapter>) Class
							.forName(bean.getClazz());
					//
					this.tcpIpPrograms.add(new TcpIpProgram(
							clazz));
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static synchronized List<TcpIpProgram> getTcpIpPrograms() {
		return instance.tcpIpPrograms;
	}
	
	public static synchronized TcpIpProgram getTcpIpProgram(String type) {
		for (TcpIpProgram tcpIpProgam: instance.tcpIpPrograms) {
			if (tcpIpProgam.getAdapterClass().getCanonicalName().equals(type)) {
				return tcpIpProgam;
			}
		}
		return null;
	}

}
