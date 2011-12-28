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
package org.parallelj.launching.quartz.web;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.parallelj.internal.conf.CBeans.Bean;
import org.parallelj.internal.conf.ConfigurationService;
import org.parallelj.internal.conf.ParalleljConfiguration;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.quartz.LaunchException;
import org.parallelj.launching.quartz.Launcher;
import org.parallelj.launching.transport.AdaptersArguments;
import org.parallelj.launching.transport.jmx.JmxServer;
import org.parallelj.launching.transport.tcp.TcpIpHandlerAdapter;
import org.parallelj.launching.transport.tcp.TcpIpServer;

/**
 * <p>
 * A ServletContextListner that can be used to start:
 * - a Parallelj scheduler
 * - a TcpIpServer 
 * - a JmxServer
 * </p>
 */
public class ServersInitializerListener implements ServletContextListener {

	/**
	 * The TcpIpServer for remote launching via telnet.
	 */
	private TcpIpServer tcpIpServer;
	
    /**
	 * The JmxServer for remote launching via JMX.
     */
    private JmxServer jmxServer;

    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    public final void contextInitialized(ServletContextEvent sce) {
        try {
            // Get the configuration
            ParalleljConfiguration configuration = (ParalleljConfiguration) ConfigurationService
    				.getConfigurationService().getConfigurationManager()
    				.getConfiguration();
            
            // Initialize the scheduler
            Launcher.getLauncher();
			LaunchingMessageKind.IQUARTZ0001.format();
			
			// Initialize a TcpIpServer
			if (configuration.getServers() != null
					&& configuration.getServers().getTelnet() != null) {
				this.tcpIpServer = new TcpIpServer(configuration.getServers()
						.getTelnet().getHost(), configuration.getServers()
						.getTelnet().getPort(), new TcpIpHandlerAdapter());
				// Try to start the TciIpServer
				if (this.tcpIpServer != null) {
					try {
						this.tcpIpServer.start();
					} catch (IOException e) {
						LaunchingMessageKind.ETCPIP0001.format(e);
					}
				}
			} else {
				LaunchingMessageKind.ETCPIP0001.format();
			}

			// Initialize a JmxServer
			// and register all defined Program as MBeans
			if (configuration.getServers() != null
					&& configuration.getServers().getJmx() != null) {
					this.jmxServer = new JmxServer(configuration.getServers()
						.getJmx().getHost(), configuration.getServers()
						.getJmx().getPort());
				// Try to start the JmxServer
				if (this.jmxServer != null) {
					try {
						this.jmxServer.start();
					} catch (IOException e) {
						LaunchingMessageKind.EJMX0001.format(e);
					}
				}
			} else {
				LaunchingMessageKind.EJMX0001.format();
			}
			
			// Scan all defined Program in parallej.xml 
			if (configuration.getServers().getBeans() != null
					&& configuration.getServers().getBeans().getBean() != null) {
				for (Bean bean : configuration.getServers().getBeans().getBean()) {
					// Initialize the Arguments of the Programs in AdaptersArguments
					AdaptersArguments.addAdapter(bean.getClazz());
					
					// Register the Program as MBean
					if (this.jmxServer != null) {
						this.jmxServer.registerProgramAsMBean(bean.getClazz());
					}
				}
			}
        } catch (LaunchException e) {
        	LaunchingMessageKind.EQUARTZ0001.format(e);
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    public final void contextDestroyed(ServletContextEvent sce) {
		// Stop the TciIpServer
		if (this.tcpIpServer != null) {
			this.tcpIpServer.stop();
		}

		// Stop the JmxServer
		if (this.jmxServer != null) {
			this.jmxServer.stop();
		}

		// Shutdown the scheduler
        try {
            Launcher.getLauncher().complete();
        } catch (LaunchException e) {
        	LaunchingMessageKind.EQUARTZ0002.format(e);
        }
    }
}
