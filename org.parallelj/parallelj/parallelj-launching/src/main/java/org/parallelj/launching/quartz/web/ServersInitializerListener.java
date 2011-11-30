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


package org.parallelj.launching.quartz.web;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.parallelj.internal.conf.CBeans.Bean;
import org.parallelj.internal.conf.ParalleljConfiguration;
import org.parallelj.internal.conf.ParalleljConfigurationManager;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.quartz.ParalleljScheduler;
import org.parallelj.launching.quartz.ParalleljSchedulerFactory;
import org.parallelj.launching.transport.jmx.JmxServer;
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

	private TcpIpServer tcpIpServer;
	
    private ParalleljScheduler scheduler = null;

    private JmxServer jmxServer;

    public void contextInitialized(ServletContextEvent sce) {
        try {
            // Get the configuration
            ParalleljConfiguration configuration = ParalleljConfigurationManager.getConfiguration();
            
            // Initialize the scheduler
			this.scheduler = (new ParalleljSchedulerFactory()).getScheduler();
			this.scheduler.start();
			LaunchingMessageKind.I0009.format();
			
			// Initialize a TcpIpServer
			if (configuration.getServers() != null
					&& configuration.getServers().getTelnet() != null) {
				this.tcpIpServer = new TcpIpServer(configuration.getServers()
						.getTelnet().getHost(), configuration.getServers()
						.getTelnet().getPort());
				// Try to start the TciIpServer
				if (this.tcpIpServer != null) {
					try {
						this.tcpIpServer.start();
					} catch (IOException e) {
						LaunchingMessageKind.E0001.format(e);
					}
				}
			} else {
				LaunchingMessageKind.E0001.format();
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
						
						// Register all defined Program in parallej.xml as MBeans
						if (configuration.getServers().getBeans() != null
								&& configuration.getServers().getBeans().getBean() != null) {
							for (Bean bean : configuration.getServers().getBeans().getBean()) {
								// Register the Program as MBean
								this.jmxServer.registerProgramAsMBean(bean.getClazz());
							}
						}

					} catch (IOException e) {
						LaunchingMessageKind.E0002.format(e);
					}
				}
			} else {
				LaunchingMessageKind.E0002.format();
			}
        } catch (Exception e) {
        	LaunchingMessageKind.E0006.format();
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
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
            if (this.scheduler != null) {
                this.scheduler.shutdown();
            }
        } catch (Exception e) {
        	LaunchingMessageKind.E0007.format(e);
        }
    }
}
