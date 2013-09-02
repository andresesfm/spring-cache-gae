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

import java.util.ArrayList;
import java.util.Collection;


import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.AbstractCacheManager;
import org.springframework.util.Assert;

/**
 * Implementation of {@link CacheManager} that 
 * creates or returns an existing {@link GaeCache}
 *  
 * @author patrickvk
 *
 */
public class GaeCacheManager extends AbstractCacheManager {

	private final Collection<GaeCache> caches = new ArrayList<GaeCache>();
	
	/* (non-Javadoc)
	 * @see org.springframework.cache.support.AbstractCacheManager#loadCaches()
	 */
	@Override
	protected Collection<? extends Cache> loadCaches() {
		return this.caches;
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.cache.support.AbstractCacheManager#getCache(java.lang.String)
	 */
	@Override
	public Cache getCache(String name) {
		Cache cache = super.getCache(name);
		if (cache == null) {
			// Cache doesn't exist yet so create on the fly
			cache = new GaeCache(name);
			super.addCache(cache);
		}
		return cache;
	}
	
	/**
	 * Add a pre-configured cache
	 * to the cache collection.
	 * 
	 * @param cache
	 */
	public void addCache(GaeCache cache) {
		Assert.notNull(cache);
		super.addCache(cache);
	}

}
