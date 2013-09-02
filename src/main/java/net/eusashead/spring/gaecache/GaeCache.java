package net.eusashead.spring.gaecache;

/*
 * #[license]
 * spring-cache-gae
 * %%
 * Copyright (C) 2013 Eusa's Head
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * %[license]
 */

import java.util.Random;
import java.util.logging.Logger;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.util.Assert;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

/**
 * A {@link Cache} implementation
 * built on top of the Google App Engine
 * {@link MemcacheService}. 
 * 
 * This implementation does not make
 * use of the namespace support offered
 * in the GAE MemcacheService. That is because
 * this implementation does not allow for
 * namespace-specific evictions needed to 
 * implement clear() from {@link Cache}. The
 * {@link MemcacheService} clearAll() method
 * clears ALL namespaces.
 * 
 * Instead, we use the namespace trick here:
 * http://code.google.com/p/memcached/wiki/NewProgrammingTricks#Deleting_By_Namespace
 * 
 * NB: The cache statistics are inaccurate because of the extra calls
 * needed to setup namespaces. The more namespaces and namespace
 * operations that are carried out in comparison to cache retrievals,
 * the more inaccurate the statistics will be.
 * 
 * NB: This class is not threadsafe by design. Synchronizing
 * the namespace operations would cause more harm than good.
 * 
 * 
 * @author patrickvk
 *
 */
public class GaeCache implements Cache {
	
	/**
	 * Logger
	 */
	private static final Logger log = Logger.getLogger(GaeCache.class.getName());

	/**
	 * Namespace prefix to avoid accidental collisions
	 */
	private static final String NS_PREFIX = "__NAMESPACE__";
	
	/**
	 * Duration to cache values for
	 */
	private final Expiration expiration;

	/**
	 * The "friendly" name as seen by the GaeCacheManager
	 */
	private final String name;

	/**
	 * The fully qualified name with namespace prefix
	 */
	private final String fqName;

	/**
	 * MemcacheService to use for cache operations
	 */
	private final MemcacheService syncCache;

	/**
	 * Constructor uses supplied name
	 * and creates a {@link MemcacheService}
	 * using {@link MemcacheServiceFactory}.getMemcacheService()
	 * and a default expiration time of 30 minutes
	 * 
	 * @param name {@link String} namespace for cache
	 */
	public GaeCache(String name) {
		this(name, MemcacheServiceFactory.getMemcacheService(), Expiration.byDeltaSeconds(3600));
	}
	
	/**
	 * Constructor uses supplied name
	 * and {@link MemcacheService}
	 * 
	 * @param name {@link String} namespace for cache
	 * @param memcacheService {@link MemcacheService} to use for cache
	 * @param expiration {@link Expiration} to use for cached values
	 */
	public GaeCache(String name, MemcacheService memcacheService, Expiration expiration) {

		// Validate parameters
		Assert.notNull(name, "Name cannot be null.");
		Assert.notNull(memcacheService, "MemcacheService cannot be null.");
		Assert.notNull(expiration, "Expiration cannot be null.");

		// Set the name and fully qualified name
		this.name = name;
		this.fqName = NS_PREFIX + name;

		// Get a reference to the MemcacheService
		this.syncCache = memcacheService;
		
		// Set the cache expiration
		this.expiration = expiration;

	}

	/**
	 * This clears the cache by
	 * incrementing the namespace key.
	 * That renders all previous cache entries
	 * inaccessible, and subsequent entries are 
	 * stored under the new key.
	 * 
	 * NB: This could be synchronized with
	 * getNamespaceKey() below to avoid
	 * a concurrent thread storing new cache
	 * values under the old key while the increment
	 * is happening. Since this lost data should
	 * not be critical, the overhead of synchronization
	 * is not needed.
	 */
	@Override
	public void clear() {
		if (this.syncCache.contains(fqName)) {
			log.fine(String.format("Clearing cache with name %s", name));
			this.syncCache.increment(fqName, 1);
		}
	}

	@Override
	public void evict(Object key) {
		Assert.notNull(key);
		Assert.isAssignable(GaeCacheKey.class, key.getClass());
		GaeCacheKey cacheKey = GaeCacheKey.class.cast(key);
		Integer namespaceKey = getNamespaceKey();
		String nsKey = getKey(namespaceKey, cacheKey);
		log.fine(String.format("Deleting key %s (%s) from namespace %s (namespace key: %s)", cacheKey.hashValue(), cacheKey.rawValue(), this.name, namespaceKey));
		syncCache.delete(nsKey);
	}

	@Override
	public ValueWrapper get(Object key) {
		Assert.notNull(key);
		Assert.isAssignable(GaeCacheKey.class, key.getClass());
		GaeCacheKey cacheKey = GaeCacheKey.class.cast(key);
		Integer namespaceKey = getNamespaceKey();
		String nsKey = getKey(namespaceKey, cacheKey);
		Object value = syncCache.get(nsKey);
		log.fine(String.format("Retrieving key %s (%s) from namespace %s (namespace key: %s), got %s", cacheKey.hashValue(), cacheKey.rawValue(), this.name, namespaceKey, value));
		return (value != null ? new SimpleValueWrapper(value) : null);
	}

	@Override
	public void put(Object key, Object value) {
		Assert.notNull(key);
		Assert.isAssignable(GaeCacheKey.class, key.getClass());
		GaeCacheKey cacheKey = GaeCacheKey.class.cast(key);
		Integer namespaceKey = getNamespaceKey();
		String nsKey = getKey(namespaceKey, cacheKey);
		log.fine(String.format("Caching key %s (%s) from namespace %s (namespace key: %s), with %s", cacheKey.hashValue(), cacheKey.rawValue(), this.name, namespaceKey, value));
		this.syncCache.put(nsKey, value, expiration);
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Object getNativeCache() {
		return this.syncCache;
	}


	/**
	 * Get the current namespace key
	 * from the cache using the fully
	 * qualified name.
	 * 
	 * NB this method would need to be 
	 * synchronized to prevent an unsafe
	 * "check then act" (concurrent threads
	 * could simultaneously put the key
	 * in the cache). However, since this would
	 * add an overhead for every cache call
	 * this seems an unnecessary burden
	 * since no bad consequences would occur as
	 * a result of this. 
	 * @return {@link Long} the current namespace key
	 */
	private Integer getNamespaceKey() {
		Integer nsKey = (Integer)this.syncCache.get(fqName);
		if (nsKey == null) {
			nsKey = new Random().nextInt(Integer.MAX_VALUE);
			log.fine(String.format("Creating namespace key %s in cache %s", nsKey, name));
			this.syncCache.put(fqName, nsKey);
		}
		return nsKey;
	}

	/**
	 * Get the key qualified 
	 * with the namespace key
	 * @param key {@link Object} the original key, without the namespace prefix
	 * @return {@link String} value of key prefixed with namespace key
	 */
	private String getKey(Integer namespaceKey, GaeCacheKey key) {
		return fqName + "_" + namespaceKey.toString() + "_" + key.hashValue();
	}
	
}
