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

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

@Configuration
@ComponentScan(basePackageClasses={CacheService.class})
@EnableCaching
@EnableAspectJAutoProxy
public class CacheConfig {
	
	/**
	 * Set up the {@link CacheManager}
	 * with an memcached based cache
	 * @return
	 */
	@Bean(name="cacheManager")
	public CacheManager cacheManager() {
		GaeCacheManager cacheManager = new GaeCacheManager();
		cacheManager.addCache(new GaeCache("default", MemcacheServiceFactory.getMemcacheService(), Expiration.byDeltaSeconds(60)));
		return cacheManager;
	}

}
