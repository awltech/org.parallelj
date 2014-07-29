package org.parallelj.servers;

import java.util.List;

import org.parallelj.internal.conf.pojos.CProperty;
import org.parallelj.launching.internal.ext.ExtensionException;


public interface ServerExtension {

	public boolean parseProperties(List<CProperty> properties);
	
	public void process(Object... parameters) throws ExtensionException;

}
