package net.eusashead.spring.gaecache;

import java.util.Random;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

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
	 * Namespace prefix to avoid accidental collisions
	 */
	private static final String NS_PREFIX = "__NAMESPACE__";

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

	public GaeCache(String name) {

		// Name must be supplied
		if (name == null) {
			throw new IllegalArgumentException("Name cannot be null.");
		}

		// Set the name and fully qualified name
		this.name = name;
		this.fqName = NS_PREFIX + name;

		// Get a reference to the MemcacheService
		this.syncCache = MemcacheServiceFactory.getMemcacheService();

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
			this.syncCache.increment(fqName, 1);
		}
	}

	@Override
	public void evict(Object key) {
		syncCache.delete(getKey(key));
	}

	@Override
	public ValueWrapper get(Object key) {
		Object value = syncCache.get(getKey(key));
		return (value != null ? new SimpleValueWrapper(value) : null);
	}

	@Override
	public void put(Object key, Object value) {
		this.syncCache.put(getKey(key), value);
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
	private String getKey(Object key) {
		return getNamespaceKey().toString() + "_" + key;
	}

}
