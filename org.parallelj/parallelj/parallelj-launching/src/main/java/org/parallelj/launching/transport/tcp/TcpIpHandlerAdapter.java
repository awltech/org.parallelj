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

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.parallelj.launching.transport.tcp.command.TcpCommand;

/**
 * Handler associated with the TcpIpServer
 * 
 *
 */
public class TcpIpHandlerAdapter
extends IoHandlerAdapter {
	private Map<String, TcpCommand> commands = new HashMap<String, TcpCommand>();
	
	/**
	 * Default constructor
	 */
	public TcpIpHandlerAdapter() {
		// Search for available commands
		ServiceLoader<TcpCommand> loader = ServiceLoader.load(TcpCommand.class);
		for (TcpCommand command:loader) {
			this.commands.put(command.getType(), command);
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.mina.core.service.IoHandlerAdapter#exceptionCaught(org.apache.mina.core.session.IoSession, java.lang.Throwable)
	 */
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		cause.printStackTrace();
	}

	/* (non-Javadoc)
	 * @see org.apache.mina.core.service.IoHandlerAdapter#messageReceived(org.apache.mina.core.session.IoSession, java.lang.Object)
	 */
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		
		String str = message.toString();
		
		// Parse the command
		String cmd = null;
		String[] args = str.split("[\t ]");
		if (args.length>0) {
			cmd = args[0];
		}
		
		// Try to launch the command
		TcpCommand command = this.commands.get(cmd);
		String result = null;
		
		// launch the command and get the result
		if (command != null) { 
			result = command.process(session, args);
		} else {
			session.write("command unknown :"+cmd);
		}
		// If the command returned a result, write it for the user
		if (result != null) {
			session.write(result);
		}
		session.write("\n\r>");
	}
	
	/* (non-Javadoc)
	 * @see org.apache.mina.core.service.IoHandlerAdapter#sessionOpened(org.apache.mina.core.session.IoSession)
	 */
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		session.write(Resources.welcome.format());
		session.write("\n\r>");
		super.sessionOpened(session);
	}

}