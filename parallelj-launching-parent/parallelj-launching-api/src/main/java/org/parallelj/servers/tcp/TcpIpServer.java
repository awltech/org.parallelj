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
package org.parallelj.servers.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineDecoder;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.parallelj.internal.conf.pojos.CBean;
import org.parallelj.internal.conf.pojos.CServer;
import org.parallelj.internal.conf.pojos.CProperty;
import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.transport.tcp.TcpIpHandlerAdapter;
import org.parallelj.launching.transport.tcp.TcpIpTextLineEncoder;
import org.parallelj.servers.Server;

/**
 * Class representing a Parallelj TcpIpServer for remote launching 
 */
public class TcpIpServer extends Server {

	private final static String ENCODING = "UTF-8";
	private static final int BUFFER_READER_SIZE = 2048;
	private static final int IDLE_TIME = 10;
	
	private final IoAcceptor acceptor = new NioSocketAcceptor();
	private String host;
	private int port;
	
	public TcpIpServer(CServer cServer, List<CBean> beans) {
		super(cServer, beans);
	}
	
	/**
	 * Default constructor
	 * 
	 * @param host
	 * @param port
	 * @param handler
	 */
	public TcpIpServer(final String host, final int port, final IoHandler handler) {
		super(null,null);
		this.host=host;
		this.port=port;
		this.acceptor.setHandler(handler);
	}
	
	/**
	 * Default constructor
	 * 
	 * @param host
	 * @param port
	 */
	public TcpIpServer(final String host, final int port) {
		super(null,null);
		this.host = host;
		this.port = port;
	}
	
	public boolean parseProperties() {
		for (CProperty property : this.server.getProperty()) {
			switch (property.getName()) {
			case "host":
				this.host = property.getValue();
				if(this.host == null || this.host.trim().length()==0) {
					LaunchingMessageKind.ESERVER0005.format(this, "invalid host value",property.getValue());
					return false;
				}
				break;
			case "port":
				try {
					this.port = Integer.parseInt(property.getValue());
				} catch (NumberFormatException e) {
					LaunchingMessageKind.ESERVER0005.format(this, "invalid port value");
					return false;
				}
				break;
			default:
				break;
			}
		}
		return true;
	}

	@Override
	public void start() {
		if (parseProperties()) {
		
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

			if (this.acceptor != null) {
				if (this.acceptor.getHandler() == null) {
					this.acceptor.setHandler(new TcpIpHandlerAdapter());
				}
				
				LaunchingMessageKind.ISERVER0002.format(this, this.host, this.port);
				try {
					this.acceptor.bind(new InetSocketAddress(this.host, this.port));
				} catch (IOException e) {
					LaunchingMessageKind.ESERVER0002.format(this,e);
				}
			}
		} else {
			LaunchingMessageKind.ESERVER0005.format(this,"");
		}
		LaunchingMessageKind.ISERVER0004.format(this,this.host+":"+this.port);
	}

	@Override
	public void stop() {
		if (this.acceptor != null) {
			LaunchingMessageKind.ISERVER0005.format(this);
			this.acceptor.dispose(true);
		}
	}
}
