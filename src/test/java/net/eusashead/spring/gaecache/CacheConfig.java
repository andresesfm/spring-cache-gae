package net.eusashead.spring.gaecache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

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
		cacheManager.addCache(new GaeCache("default"));
		return cacheManager;
	}

}
