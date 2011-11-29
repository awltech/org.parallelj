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

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

public class TcpIpHandlerAdapter
extends IoHandlerAdapter {
	
	public TcpIpHandlerAdapter() {
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		cause.printStackTrace();
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		
		String str = message.toString();
		switch (RemoteCommand.valueOf(str.toLowerCase())) {
			case h:
			case help:
				session.write(Resources.help.format());
				//session.
				//session.write(LineDelimiter.AUTO);
				break;
			case ll:
				//TODO: Scan for all programs and write the welcome message 
				
				break;
			case s:
			case signature:
				//TODO
				break;
			case stop:
				//TODO
				break;
			case dp:
				//TODO: Call the IsmpDp...
				break;
			case q:
			case quit:
				session.close(true);
				break;
			default:
				break;
		}
		session.write("\n\r>");
	}
	
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		session.write(Resources.welcome.format());
		session.write("\n\r>");
		super.sessionOpened(session);
	}

}