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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.parallelj.launching.LaunchingMessageKind;

public class TcpIpServer {
	private final IoAcceptor acceptor = new NioSocketAcceptor();
	private String host;
	private int port;

	public TcpIpServer(String host, int port) {
		this.host = host;
		this.port = port;
		
		// Initialize the acceptor
		this.acceptor.getFilterChain().addLast("logger", new LoggingFilter());
		this.acceptor.getFilterChain().addLast(
				"codec",
				new ProtocolCodecFilter(new TextLineCodecFactory(Charset
						.forName("UTF-8"))));
		this.acceptor.setHandler(new TcpIpHandlerAdapter());
		this.acceptor.getSessionConfig().setReadBufferSize(2048);
		//this.acceptor.getSessionConfig().set
		this.acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
	}
	
	public synchronized void start() throws IOException {
		if (acceptor != null) {
			LaunchingMessageKind.I0001.format(this.host, this.port);
			this.acceptor.bind(new InetSocketAddress(this.host, this.port));
		}
	}

	public synchronized void stop() {
		if (acceptor != null) {
			LaunchingMessageKind.I0002.format();
			acceptor.dispose(true);
		}
	}

}
