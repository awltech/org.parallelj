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

package org.parallelj.launching.transport.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Define resources to use for Welcome and Help text.
 * These text are printed to a client connecting to the TcpIpServer   
 *
 */
public enum Resources {
	welcome ("/org/parallelj/launching/welcome.txt"),
	help ("/org/parallelj/launching/help.txt");
	
	private String text;

	/**
	 * Default constructor
	 * 
	 * @param resource
	 */
	private Resources(String resource) {
		InputStream inputStream = TcpIpHandlerAdapter.class.getResourceAsStream(resource);
	    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	    StringBuilder sb = new StringBuilder();
	    String line = null;
	    try {
			while ((line = reader.readLine()) != null) {
			  sb.append(line).append("\n\r");
			}
		} catch (IOException e) {
		} finally {
		    try {
				inputStream.close();
			} catch (IOException e) {}
		}
	    this.text = sb.toString();
	}
	
	public String format() {
		return this.text;
	}

}
