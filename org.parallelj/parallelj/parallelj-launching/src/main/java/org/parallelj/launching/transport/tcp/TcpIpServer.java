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
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineDecoder;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.parallelj.launching.LaunchingMessageKind;

/**
 * Class representing a Parallelj RcpIpServer Server for remote launching 
 */
public class TcpIpServer {
	private final static String ENCODING = "UTF-8";
	private static final int BUFFER_READER_SIZE = 2048;
	private static final int IDLE_TIME = 10;
	
	private final IoAcceptor acceptor = new NioSocketAcceptor();
	private String host;
	private int port;

	/**
	 * Default constructor
	 * 
	 * @param host
	 * @param port
	 * @param handler
	 */
	public TcpIpServer(String host, int port, IoHandler handler) {
		this(host, port);
		setHandler(handler);
	}
	
	/**
	 * Default constructor
	 * 
	 * @param host
	 * @param port
	 */
	public TcpIpServer(String host, int port) {
		this.host = host;
		this.port = port;
		
		// Initialize the acceptor
		this.acceptor.getFilterChain().addLast("logger", new LoggingFilter());
		this.acceptor.getFilterChain().addLast(
				"codec",
				new ProtocolCodecFilter( 
						new TcpIpTextLineEncoder(Charset.forName(ENCODING)), 
						new TextLineDecoder(Charset.forName(ENCODING))
						));
		this.acceptor.getSessionConfig().setReadBufferSize(BUFFER_READER_SIZE);
		this.acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, IDLE_TIME);
	}
	
	/**
	 * Start the TcpIpServer
	 * 
	 * @throws IOException
	 */
	public final synchronized void start() throws IOException {
		if (this.acceptor != null) {
			if (this.acceptor.getHandler() == null) {
				this.acceptor.setHandler(new TcpIpHandlerAdapter());
			}
			
			LaunchingMessageKind.ITCPIP0001.format(this.host, this.port);
			this.acceptor.bind(new InetSocketAddress(this.host, this.port));
		}
	}

	/**
	 * Stop the TcpIpServer
	 */
	public final synchronized void stop() {
		if (this.acceptor != null) {
			LaunchingMessageKind.ITCPIP0002.format();
			this.acceptor.dispose(true);
		}
	}

	public void setHandler(IoHandler handler) {
		this.acceptor.setHandler(handler);
	}
}
