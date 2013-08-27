package net.eusashead.spring.gaecache;

import java.io.Serializable;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class GaeCache implements Cache {
	
	private static final Object NULL_HOLDER = new NullHolder();

	private final MemcacheService syncCache;
	
	private final boolean allowNullValues;

	public GaeCache(String name) {
		syncCache = MemcacheServiceFactory.getMemcacheService(name);
		allowNullValues = false;
	}

	@Override
	public void clear() {
		// Note - this clears ALL of the caches
		// not just for the namespace...
		syncCache.clearAll();
	}

	@Override
	public void evict(Object key) {
		syncCache.delete(key);

	}

	@Override
	public ValueWrapper get(Object key) {
		Object value = syncCache.get(key);
		return (value != null ? new SimpleValueWrapper(fromStoreValue(value)) : null);
	}

	@Override
	public String getName() {
		return syncCache.getNamespace();
	}

	@Override
	public Object getNativeCache() {
		return this.syncCache;
	}

	@Override
	public void put(Object key, Object value) {
		this.syncCache.put(key, toStoreValue(value));
	}

	/**
	 * Convert the given value from the internal store to a user value
	 * returned from the get method (adapting {@code null}).
	 * @param storeValue the store value
	 * @return the value to return to the user
	 */
	protected Object fromStoreValue(Object storeValue) {
		if (this.allowNullValues && storeValue == NULL_HOLDER) {
			return null;
		}
		return storeValue;
	}

	/**
	 * Convert the given user value, as passed into the put method,
	 * to a value in the internal store (adapting {@code null}).
	 * @param userValue the given user value
	 * @return the value to store
	 */
	protected Object toStoreValue(Object userValue) {
		if (this.allowNullValues && userValue == null) {
			return NULL_HOLDER;
		}
		return userValue;
	}


	@SuppressWarnings("serial")
	private static class NullHolder implements Serializable {
	}

}
