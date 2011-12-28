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
package main;

import java.lang.management.ManagementFactory;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.xml.bind.JAXB;

import org.parallelj.jmx.Management;
import org.parallelj.jmx.client.ClientReflection;
import org.parallelj.mirror.ExecutorServiceKind;
import org.parallelj.mirror.ProcessState;
import org.parallelj.mirror.Processor;
import org.parallelj.mirror.ProgramType;

public class RemoteMain {
	
	static void init() throws Exception {
		MyProgram m = new MyProgram();
		m.lower.add("a");
//		JAXB.marshal(m, System.out);
		
//		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
//
//		ObjectName name = new ObjectName("org.parallelj:type=Management");
//
//		Management management = new Management();
//
//		mbs.registerMBean(management, name);
		
	}

	public static void main(String[] args) throws Exception {
		init();
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

		ObjectName name = new ObjectName("org.parallelj:type=Management");

		// Make a connector server...
		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://");
		JMXConnectorServer cs = JMXConnectorServerFactory
				.newJMXConnectorServer(url, null, mbs);
		cs.start();
		JMXConnector cc = null;
		try {
			JMXServiceURL addr = cs.getAddress();

			// Now make a connector client using the server's address
			cc = JMXConnectorFactory.connect(addr);
			MBeanServerConnection mbsc = cc.getMBeanServerConnection();

			ClientReflection client = new ClientReflection(mbsc, name);
			
			List<ProgramType> programs = client.getPrograms();
			
			org.parallelj.mirror.Process process = programs.get(0).newProcess(new MyProgram());
			Processor processor = client.newProcessor(ExecutorServiceKind.FIXED_THREAD_POOL, 1);
			processor.execute(process);
			
			System.out.println(process.getContext());
			while (process.getState() != ProcessState.COMPLETED) {
				System.out.println("Waiting for completion");
				Thread.sleep(1000);
			}
			System.out.println("completed: " + process.getContext());
			//System.exit(0);
		} finally {
			if (cc != null)
				cc.close();
			cs.stop();
		}
	}
}
