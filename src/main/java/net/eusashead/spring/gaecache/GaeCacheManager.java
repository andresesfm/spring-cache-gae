package net.eusashead.spring.gaecache;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;

public class GaeCacheManager extends AbstractCacheManager {

	private final Collection<GaeCache> caches = new ArrayList<GaeCache>();
	
	@Override
	protected Collection<? extends Cache> loadCaches() {
		return this.caches;
	}
	
	@Override
	public Cache getCache(String name) {
		Cache cache = super.getCache(name);
		if (cache == null) {
			// check the EhCache cache again
			// (in case the cache was added at runtime)
			cache = new GaeCache(name);
			super.addCache(cache);
		}
		return cache;
	}
	
	public void addCache(GaeCache cache) {
		this.caches.add(cache);
	}

}
