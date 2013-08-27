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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={CacheConfig.class})
public class CacheConfigITCase {
	
	private final LocalServiceTestHelper helper =
	        new LocalServiceTestHelper(new LocalMemcacheServiceTestConfig());
	
	@Autowired
	private CacheManager cacheManager;
	
	@Autowired
	private CacheService cacheService;
	
	@Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

	
	@Test
	public void testMemcacheService() throws Exception {
		MemcacheService ms = MemcacheServiceFactory.getMemcacheService();
        Assert.assertFalse(ms.contains("yar"));
        ms.put("yar", "foo");
        Assert.assertTrue(ms.contains("yar"));
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
		MemcacheService ms = MemcacheServiceFactory.getMemcacheService("default");
		Assert.assertTrue(ms.contains("key"));
	}
	
	@Test
	public void testCacheableAnnotation() throws Exception {
		Foo result1 = cacheService.getFoo("foo");
		Foo result2 = cacheService.getFoo("foo");
		
		// Check same
		Assert.assertNotNull(result1);
		Assert.assertEquals(result1, result2);
		
		// Check consistency
		MemcacheService ms = MemcacheServiceFactory.getMemcacheService("default");
		Assert.assertEquals(1, ms.getStatistics().getMissCount());
		Assert.assertEquals(1, ms.getStatistics().getHitCount());
		Assert.assertEquals(1, ms.getStatistics().getItemCount());
		Assert.assertTrue(ms.contains("foo"));
		
		// Make sure the method was invoked just once
		Assert.assertEquals(Long.valueOf(1), cacheService.getLastId());		
	}
	
	@Test
	public void testLazyCreatedCache() throws Exception {
		Foo result1 = cacheService.getOtherFoo("bar");
		Assert.assertNotNull(result1);
	}

}
