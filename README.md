Spring Cache for Google App Engine [![Build Status](https://travis-ci.org/patrickvankann/spring-cache-gae.png)](https://travis-ci.org/patrickvankann/spring-cache-gae)
================

[Spring Cache abstractions](http://static.springsource.org/spring/docs/3.2.x/spring-framework-reference/html/cache.html) for the [Google App Engine memcached service](https://developers.google.com/appengine/docs/java/memcache/).


##Features
- Includes implementations of [Cache](http://static.springsource.org/spring/docs/3.2.x/javadoc-api/org/springframework/cache/Cache.html) and [CacheManager](http://static.springsource.org/spring/docs/3.2.x/javadoc-api/org/springframework/cache/CacheManager.html) that wrap the Google [MemcacheService](https://developers.google.com/appengine/docs/java/javadoc/com/google/appengine/api/memcache/MemcacheService)
- Support for namespaces including clear() operations scoped to the namespace of the cache using [this approach](http://code.google.com/p/memcached/wiki/NewProgrammingTricks#Deleting_By_Namespace)
- Customisable expiration times per cache (with a default value of 30 minutes)

##Configuration
A Spring Java configuration is below.

    @Configuration
    public class CacheConfig {
    
        /**
         * Set up the {@link CacheManager}
         * with an memcached based cache
         * @return the configured {@link CacheManager} instance
         */
        @Bean(name="cacheManager")
        public CacheManager cacheManager() {
            GaeCacheManager cacheManager = new GaeCacheManager();
            cacheManager.addCache(new GaeCache("default", MemcacheServiceFactory.getMemcacheService(), Expiration.byDeltaSeconds(60)));
            return cacheManager;
        }

    }
    
##Logging
In order to be compatible with the Google App Engine's Java Logging infrastructure, GaeCache uses Java Logging for debug/tracing. This might be useful if you want to verify the behaviour of the cache or carry out debugging.

To enable logging you could use a configuration like this:

    handlers = java.util.logging.ConsoleHandler
    .level = WARNING
    net.eusashead.spring.gaecache.level = FINE
    java.util.logging.ConsoleHandler.level = FINE
    
This will enable you to see what the cache is doing in some detail.
   
##Notes
 - Cacheable values must implement java.io.Serializable in order for the MemcacheService to serialize them in the cache.
 - As a result, I recommend implementing hashCode() and equals() in cacheable values if you want values returned from the cache to be "equal" to those put in the cache - the serialization means that the object instance returned from the cache is different from that originally cached. 
 - Cache key objects should implement a meaningful toString() method. The cache key algorithm creates a string key prefixed with the namespace. To avoid accidental key collisions due to not overriding Object.toString(), implement a toString() that will consistently generate a unique string for your key object that would be consistent with equals() and hashCode() (e.g. if objects are equal, their toString() would be equal as well). This is not needed if you use primitive or wrapper types as the method parameters for a @Cacheable method - just if you use a custom Java type as a method parameter.
 - Support for namespaces cause an additional round-trip to the memcache service for each cache call because of the need to retrieve the namespace key first. This is because memcache does not support namespace clearing through its API in a single call (because this is a >O(1) operation).
 - Operations that set and clear the namespace are not synchronized so not threadsafe. The worst case scenario is that stale data could be returned to one thread while another is clearing the namespace by incrementing the key. Or, a cache miss could occur when a concurrent thread reads the cache for a namespace before the namespace key has been set. Hence, the overhead of synchronization is not justified and the class does not need to be threadsafe.

##Download
It is available in the Sonatype repository on these coordinates. Look in Github [Releases](https://github.com/patrickvankann/spring-cache-gae/releases) to see the latest version number.

    <dependency>
        <groupId>net.eusashead.spring</groupId>
        <artifactId>spring-cache-gae</artifactId>
        <version>${version}</version>
    </dependency>
    
    