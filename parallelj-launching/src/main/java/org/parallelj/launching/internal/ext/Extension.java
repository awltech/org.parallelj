package org.parallelj.launching.internal.ext;

import java.util.Map;

public interface Extension {

	public String getType();

	public void init() throws ExtensionException;
	
	public Map<String,String> getProps();
	
	public boolean isInitialized();
	
}
