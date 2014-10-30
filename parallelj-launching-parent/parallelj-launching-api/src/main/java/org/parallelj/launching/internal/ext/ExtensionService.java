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
package org.parallelj.launching.internal.ext;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.parallelj.launching.LaunchingMessageKind;
import org.parallelj.launching.internal.spi.CacheableServiceLoader;

public class ExtensionService {

	private static ExtensionService extService = new ExtensionService();

	private ArrayList<Extension> exts = new ArrayList<Extension>();

	private ExtensionService() {
		initialize();
	}

	private synchronized void initialize() {
		ServiceLoader<Extension> loader = CacheableServiceLoader.INSTANCE.load(Extension.class,
				ExtensionService.class.getClassLoader());
		if (loader == null || loader.iterator() == null || !loader.iterator().hasNext()) {
			loader = CacheableServiceLoader.INSTANCE.load(Extension.class, Thread.currentThread().getContextClassLoader());
		}
		for (Extension ext : loader) {
			// Try to load all available extensions
			try {
				ext.init();
				exts.add(ext);
			} catch (ExtensionException e) {
				LaunchingMessageKind.EEXT002.format(ext.getClass().getCanonicalName(), "", e);
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
