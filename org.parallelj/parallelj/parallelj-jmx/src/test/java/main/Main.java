/*
 *     ParallelJ, framework for parallel computing
 *
 *     Copyright (C) 2010, 2011 Atos Worldline or third-party contributors as
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

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.xml.bind.JAXB;

import org.parallelj.jmx.Management;

public class Main {

	public static void main(String[] args) throws Exception {
		MyProgram m = new MyProgram();
		m.lower.add("a");
		JAXB.marshal(m, System.out);
		
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

		ObjectName name = new ObjectName("org.parallelj:type=Management");

		Management management = new Management();

		mbs.registerMBean(management, name);

		System.out.println("Waiting forever...");
		Thread.sleep(Long.MAX_VALUE);

	}

}
