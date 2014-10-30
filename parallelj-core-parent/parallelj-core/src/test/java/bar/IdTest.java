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
package bar;

import java.rmi.server.UID;
import java.util.UUID;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdTest {
	
	static Logger logger = LoggerFactory.getLogger("org.parallelj.internal");
	
	int count = 5000;
	
	@Test
	public void uuid() {
		Runtime.getRuntime().gc();
		long t0 = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			UUID.randomUUID().toString();
		}
		logger.info(String.format("uuid: %d\n", System.currentTimeMillis() - t0));
		Runtime.getRuntime().gc();
		logger.info(UUID.randomUUID().toString());
	}
	@Test
	public void uid() {
		Runtime.getRuntime().gc();
		long t0 = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			new UID().toString();
		}
		logger.info(String.format("uid: %d\n", System.currentTimeMillis() - t0));
		Runtime.getRuntime().gc();
		logger.info(new UID().toString());
	}
}
