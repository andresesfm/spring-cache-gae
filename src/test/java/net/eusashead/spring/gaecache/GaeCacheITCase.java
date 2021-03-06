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

import static net.eusashead.spring.gaecache.GaeCacheAssert.assertCached;
import static net.eusashead.spring.gaecache.GaeCacheAssert.assertNotCached;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

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
	public void testCacheNullResult() throws Exception {

		// Create a cache
		Cache cache = new GaeCache("nullCache");

		// Cache a null value
		cache.put(GaeCacheKey.create(null), null);

		// Check consistency
		assertCached(ms, null, "nullCache", new Object[0]);

	}

	@Test
	public void testCacheNullKey() throws Exception {

		// Create a cache
		Cache cache = new GaeCache("nullCache");

		// Cache a null value
		Foo foo = new Foo(new FooKey(1l), "null");
		GaeCacheKey cacheKey = GaeCacheKey.create(null);
		cache.put(cacheKey, foo);

		// Check cached value
		Assert.assertNotNull(cache.get(cacheKey));
		Assert.assertNotNull(cache.get(cacheKey).get());
		Assert.assertEquals(foo, cache.get(cacheKey).get());

	}

	@Test
	public void testObjectCacheKey() throws Exception {

		FooKey key = new FooKey(123l);
		Foo foo = cacheService.getFooByKey(key);

		// Check cache consistency
		assertCached(ms, foo, "objectKey", key.toString());

		// Get it back from the cache
		Foo cached = (Foo)cacheManager.getCache("objectKey").get(GaeCacheKey.create(key.toString())).get();
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
		assertCached(ms, result1, "default", "foo");

	}

	@Test
	public void testEvictAll() throws Exception {

		// Create 2 caches
		Cache cache1 = cacheManager.getCache("cache1");
		Cache cache2 = cacheManager.getCache("cache2");

		// 2 objects for 2 caches
		Foo foo1 = new Foo(new FooKey(1l), "foo1");
		cache1.put(GaeCacheKey.create("foo1"), foo1);
		Foo foo2 = new Foo(new FooKey(2l), "foo2");
		cache2.put(GaeCacheKey.create("foo2"), foo2);

		// Are they cached?
		assertCached(ms, foo1, "cache1", "foo1");
		assertCached(ms, foo2, "cache2", "foo2");

		// Clear cache 2
		cache2.clear();

		// Make sure cache 1 is OK
		assertCached(ms, foo1, "cache1", "foo1");

		// Make sure the cleared cache is clear
		assertNotCached(ms, "cache2", "foo2");

	}

	@Test
	public void testEvictAndPut() throws Exception {

		// Prime list cache
		Object expected = cacheService.listFoos();

		// Assert cached
		assertCached(ms, expected, "list", new Object[0]); 

		// Create a key
		FooKey id = new FooKey(3l);

		// Assert object not cached
		assertNotCached(ms, "objectKey", id);

		// Cause eviction and putting
		Foo foo = new Foo(id, "blah");
		Foo updated = cacheService.saveFoo(foo);

		// Assert list not cached anymore
		assertNotCached(ms, "list", new Object[0]);

		// Assert object cached
		assertCached(ms, updated, "objectKey", id); 
	}


}
