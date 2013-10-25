package org.parallelj.launching.internal.ext;

/**
 * 
 * Aspect that allow to load all defined Exts
 *
 */
public privileged aspect Extensions pertypewithin(org.parallelj.launching.quartz.web.ServersInitializerListener) {
	
	before(Object context): 
		execution(public final void org.parallelj.launching.quartz.web.ServersInitializerListener.contextInitialized(..)) 
		&& this(context) {
		
		// Load all availlable Exts...
		ExtensionService.getExtensionService();
	}
	
}

