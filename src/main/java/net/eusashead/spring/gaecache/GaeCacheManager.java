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
import org.springframework.cache.support.AbstractCacheManager;

// TODO expiry time? should be different for nskey than for cache keys

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
