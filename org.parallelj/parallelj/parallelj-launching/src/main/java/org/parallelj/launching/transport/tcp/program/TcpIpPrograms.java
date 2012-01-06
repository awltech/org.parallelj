package org.parallelj.launching.transport.tcp.program;

import java.util.ArrayList;
import java.util.List;

import org.parallelj.internal.conf.CBeans.Bean;
import org.parallelj.internal.conf.ConfigurationService;
import org.parallelj.internal.conf.ParalleljConfiguration;
import org.parallelj.internal.reflect.ProgramAdapter.Adapter;


public class TcpIpPrograms {
	public List<TcpIpProgram> tcpIpPrograms = new ArrayList<TcpIpProgram>();
	
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
				.getConfiguration();
		if (configuration.getServers().getBeans() != null
				&& configuration.getServers().getBeans().getBean() != null) {
			for (Bean bean : configuration.getServers().getBeans().getBean()) {
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
