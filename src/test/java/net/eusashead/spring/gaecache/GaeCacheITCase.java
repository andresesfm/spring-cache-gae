package net.eusashead.spring.gaecache;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

//@RunWith(JUnit4.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={CacheConfig.class})
public class GaeCacheITCase {
	
	private final LocalServiceTestHelper helper =
	        new LocalServiceTestHelper(new LocalMemcacheServiceTestConfig());
	
	@Autowired
	private CacheManager cacheManager;
	
	@Autowired
	private CacheService cacheService;

	private MemcacheService ms;
	
	@Before
    public void setUp() {
        helper.setUp();
        ms = MemcacheServiceFactory.getMemcacheService();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

	
	@Test
	public void testCacheManager() throws Exception {
		Cache cache = cacheManager.getCache("default");
		
		// Check cache is empty
		Assert.assertNotNull(cache);
		Assert.assertNull(cache.get("key"));
		
		// Put something in the cache
		cache.put("key", "foo");
		Assert.assertEquals(new SimpleValueWrapper("foo").get(), cache.get("key").get());
		
		// Check consistency
		assertCached("default", "key");
	}
	
	@Test
	public void testCacheNullResult() throws Exception {
		
		// Create a cache
		Cache cache = new GaeCache("nullCache");
		
		// Cache a null value
		cache.put("null", null);
		
		// Check consistency
		assertCached("nullCache", "null");
		
		// Check cached value
		Assert.assertNull(cache.get("null"));
		
	}
	
	@Test
	public void testCacheNullKey() throws Exception {
		
		// Create a cache
		Cache cache = new GaeCache("nullCache");
		
		// Cache a null value
		Foo foo = new Foo("null");
		cache.put(null, foo);
		
		// Check consistency
		assertCached("nullCache", null);
		
		// Check cached value
		Assert.assertNotNull(cache.get(null));
		Assert.assertNotNull(cache.get(null).get());
		Assert.assertEquals(foo, cache.get(null).get());
		
	}
	
	@Test
	public void testObjectCacheKey() throws Exception {
		
		FooKey key = new FooKey(123);
		Foo foo = cacheService.getFooByKey(key);
		
		// Check cache consistency
		assertCached("objectKey", key);
		
		// Get it back from the cache
		Foo cached = (Foo)cacheManager.getCache("objectKey").get(key).get();
		Assert.assertEquals(foo, cached);
		
		
	}
	
	@Test
	public void testCacheableAnnotation() throws Exception {
		Foo result1 = cacheService.getFoo("foo");
		Foo result2 = cacheService.getFoo("foo");
		
		// Check same
		Assert.assertNotNull(result1);
		Assert.assertEquals(result1, result2);
		
		// Make sure the method was invoked just once
		Assert.assertEquals(Long.valueOf(1), cacheService.getLastId());		
		
		// Check cache consistency
		assertCached("default", "foo");
		
	}
	
	@Test
	public void testLazyCreatedCache() throws Exception {
		
		// This cache is not pre-configured
		Cache lazy = cacheManager.getCache("other");
		Assert.assertNotNull(lazy);
		
		// Cache something
		lazy.put("bar", new Foo("bar"));
		
		// Check consistency
		assertCached("other", "bar");
	}
	
	@Test
	public void testEvictAll() throws Exception {
		
		// Create 2 caches
		Cache cache1 = cacheManager.getCache("cache1");
		Cache cache2 = cacheManager.getCache("cache2");
		
		// 2 objects for 2 caches
		cache1.put("foo1", new Foo("foo1"));
		cache2.put("foo2", new Foo("foo2"));
		
		// Are they cached?
		assertCached("cache1", "foo1");
		assertCached("cache2", "foo2");
		
		// Clear a cache
		cache2.clear();
		
		// Make sure the other cache is OK
		assertCached("cache1", "foo1");
		
		// Make sure the cleared cache is clear
		assertNotCached("cache2", "foo2");
		
	}
	
	@Test
	public void testCacheName() throws Exception {
		String name = "test";
		Cache cache = new GaeCache(name);
		Assert.assertEquals(name, cache.getName());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullCacheName() throws Exception {
		new GaeCache(null);
	}
	
	private void assertCached(String namespace, Object key) {
		Integer nsKey = getNsKey(namespace);
		Assert.assertTrue(ms.contains(nsKey + "_" + key));
	}
	
	private void assertNotCached(String namespace, Object key) {
		Integer nsKey = getNsKey(namespace);
		Assert.assertFalse(ms.contains(nsKey + "_" + key));
	}

	private Integer getNsKey(String namespace) {
		Integer nsKey = (Integer)ms.get("__NAMESPACE__" + namespace);
		return nsKey;
	}

}
