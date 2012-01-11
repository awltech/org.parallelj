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
package org.parallelj.launching.transport.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.transport.tcp.command.TcpCommand;
import org.parallelj.launching.transport.tcp.command.TcpIpCommands;

/**
 * Handler associated with the TcpIpServer
 * 
 *
 */
public class TcpIpHandlerAdapter
extends IoHandlerAdapter {
	
	public static final String ENDLINE = "\n\r";
	
	private static final String WELCOMEFILE = "/org/parallelj/launching/welcome.txt";
	
	private String welcome;
	
	/**
	 * Default constructor
	 */
	public TcpIpHandlerAdapter() {
		super();
		final InputStream inputStream = TcpIpHandlerAdapter.class.getResourceAsStream(WELCOMEFILE);
		final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		final BufferedReader reader = new BufferedReader(inputStreamReader);
		final StringBuilder stringBuiler = new StringBuilder();
	    String line = null;
	    try {
			while ((line = reader.readLine()) != null) {
				stringBuiler.append(line).append(ENDLINE);
			}
		} catch (IOException e) {
			// Do nothing
		} finally {
		    try {
				reader.close();
			} catch (IOException e) {
				// Do nothing
			}
		    try {
		    	inputStream.close();
			} catch (IOException e) {
				// Do nothing
			}
		    try {
		    	inputStreamReader.close();
			} catch (IOException e) {
				// Do nothing
			}
		}
	    this.welcome = stringBuiler.toString();
	}

	/* (non-Javadoc)
	 * @see org.apache.mina.core.service.IoHandlerAdapter#exceptionCaught(org.apache.mina.core.session.IoSession, java.lang.Throwable)
	 */
	@Override
	public final void exceptionCaught(final IoSession session, final Throwable cause)
			throws Exception {
		LaunchingMessageKind.EREMOTE0009.format(cause);
	}

	/* (non-Javadoc)
	 * @see org.apache.mina.core.service.IoHandlerAdapter#messageReceived(org.apache.mina.core.session.IoSession, java.lang.Object)
	 */
	@Override
	public final void messageReceived(final IoSession session, final Object message)
			throws Exception {
		final String str = message.toString();
		
		// Parse the command
		String cmd = null;
		final String[] args = str.split("[\t ]");
		if (args.length>0) {
			cmd = args[0];
		}
		
		// Try to launch the command
		final TcpCommand command = TcpIpCommands.getCommands().get(cmd);
		String result = null;
		
		// launch the command and get the result
		if (command != null) {
			if (args.length>1) {
				result = command.process(session, Arrays.copyOfRange(args, 1, args.length));
			} else {
				result = command.process(session, new String[]{});
			}
		} else {
			session.write("command unknown :"+cmd);
		}
		// If the command returned a result, write it for the user
		if (result != null) {
			session.write(result);
		}
		session.write(ENDLINE);
		session.write(ENDLINE);
		session.write("> ");
	}
	
	/* (non-Javadoc)
	 * @see org.apache.mina.core.service.IoHandlerAdapter#sessionOpened(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public final void sessionOpened(final IoSession session) throws Exception {
		session.write(this.welcome);
		session.write(ENDLINE);
		session.write("> ");
		super.sessionOpened(session);
	}

}