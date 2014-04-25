package org.parallelj.launching.internal.spi;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import org.parallelj.launching.LaunchingMessageKind;

/**
 * Implementation of Layer over ServiceLoader from JDK, with synchronized cache.
 * This CacheableService loader is singleton, accessible from INSTANCE literal.
 * 
 * @author mvanbesien
 * @since 1.4.2
 * 
 */
public enum CacheableServiceLoader {

	/**
	 * Singleton Instance.
	 */
	INSTANCE;

	/*
	 * Internal monitor object, for synchronisation
	 */
	private final Object monitor = new Object();

	/*
	 * Boolean to tell whether the cache is enabled...
	 */
	private boolean cacheEnabled;

	/*
	 * Cache instance.
	 */
	private Map<ServiceLoaderIdentifier<?>, ServiceLoader<?>> cache = new ConcurrentHashMap<>();

	/**
	 * Disablement key constant.
	 * 
	 * By default, the cache is enabled, except if the
	 * -Dorg.parallelj.launching.spicache.disabled=true" is set when launching
	 * the process/server.
	 */
	public static final String DISABLEMENT_KEY = "org.parallelj.launching.spicache.disabled";

	/*
	 * Private constructor, overridden for knowing whether cache is enabled.
	 */
	private CacheableServiceLoader() {
		this.cacheEnabled = !Boolean.parseBoolean(System.getProperty(DISABLEMENT_KEY));
		LaunchingMessageKind.ICACHESPI001.format(this.cacheEnabled);
	}

	/*
	 * Effective loading cache implementation.
	 */
	@SuppressWarnings("unchecked")
	private <E> ServiceLoader<E> loadWithCache(Class<E> clazz, ClassLoader classLoader) {
		synchronized (this.monitor) {
			ServiceLoaderIdentifier<E> identifier = new ServiceLoaderIdentifier<E>(clazz, classLoader);
			if (!this.cache.containsKey(identifier)) {
				ServiceLoader<E> loader = ServiceLoader.load(clazz, classLoader);
				this.cache.put(identifier, loader);
			}
			return (ServiceLoader<E>) this.cache.get(identifier);
		}
	}

	/**
	 * Loads the ServiceLoader, uses cache if necessary.
	 * 
	 * @see java.util.ServiceLoader.load(Class<E>, ClassLoader)
	 * 
	 * @param clazz
	 *            Service Class
	 * @param classLoader
	 *            custom ClassLoader
	 * @return Loaded service loader instance
	 */
	public <E> ServiceLoader<E> load(Class<E> clazz, ClassLoader classLoader) {
		return this.cacheEnabled ? this.loadWithCache(clazz, classLoader) : ServiceLoader.load(clazz, classLoader);
	}

	/**
	 * Loads the ServiceLoader, uses cache if necessary.
	 * 
	 * @param clazz
	 * @param classLoader
	 * @return
	 */
	public <E> ServiceLoader<E> load(Class<E> clazz) {
		return this.load(clazz, Thread.currentThread().getContextClassLoader());
	}

	public void clear() {
		synchronized (this.monitor) {
			this.cache.clear();
		}
	}
}
