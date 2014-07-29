package org.parallelj.servers;

import java.util.List;

import org.parallelj.internal.conf.pojos.CBean;
import org.parallelj.internal.conf.pojos.CServer;

public abstract class Server {
	
	protected CServer server;
	protected List<CBean> beans;
	
	public Server(CServer server, List<CBean> beans) {
		super();
		this.server = server;
		this.beans = beans;
	}
	
	protected abstract boolean parseProperties();
	
	public abstract void start();
	
	public abstract void stop();
}
