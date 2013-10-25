package org.parallelj.launching.internal.ext;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.parallelj.launching.LaunchingMessageKind;

public class ExtensionService {

	private static ExtensionService extService = new ExtensionService();

	private ArrayList<Extension> exts = new ArrayList<Extension>();

	private ExtensionService() {
		initialize();
	}

	private synchronized void initialize() {
		ServiceLoader<Extension> loader = ServiceLoader.load(Extension.class, ExtensionService.class.getClassLoader());
		if (loader==null || loader.iterator()==null || !loader.iterator().hasNext()) {
			loader = ServiceLoader.load(Extension.class, Thread.currentThread().getContextClassLoader());
		}
		for (Extension ext : loader) {
			// Try to load all available extensions
			try {
				ext.init();
				exts.add(ext);
			} catch (ExtensionException e) {
				LaunchingMessageKind.EEXT002.format(ext.getClass()
						.getCanonicalName(), "", e);
			}
		}
	}

	public static synchronized ExtensionService getExtensionService() {
		return extService;
	}

	public List<Extension> getExtensions() {
		return this.exts;
	}

	public List<Extension> getExtentionsByType(String type) {
		List<Extension> lstExtensions = new ArrayList<Extension>();
		for (Extension ext : this.exts) {
			if (ext.getType().equalsIgnoreCase(type)) {
				lstExtensions.add(ext);
			}
		}
		return lstExtensions;
	}

}
