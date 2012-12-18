package org.parallelj.launching.internal.ext;

import java.util.Map;

public class Ext1 implements Extension {

	@Override
	public String getType() {
		return "T1";
	}

	@Override
	public void init() throws ExtensionException {
	}

	@Override
	public Map<String, String> getProps() {
		return null;
	}

	@Override
	public boolean isInitialized() {
		return true;
	}

}
