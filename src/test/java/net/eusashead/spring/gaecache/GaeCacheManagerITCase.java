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
public class GaeCacheManagerITCase {
	
	private final LocalServiceTestHelper helper =
			new LocalServiceTestHelper(new LocalMemcacheServiceTestConfig());

	@Autowired
	private CacheManager cacheManager;

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
		GaeCacheKey cacheKey = GaeCacheKey.create("key");
		Assert.assertNull(cache.get(cacheKey));

		// Put something in the cache
		cache.put(cacheKey, "foo");
		Assert.assertEquals(new SimpleValueWrapper("foo").get(), cache.get(cacheKey).get());

		// Check consistency
		Assert.assertNotNull(cache.get(cacheKey));
	}
	
	@Test
	public void testLazyCreatedCache() throws Exception {

		// This cache is not pre-configured
		Cache lazy = cacheManager.getCache("other");
		Assert.assertNotNull(lazy);

		// Cache something
		Foo foo = new Foo(new FooKey(1l), "bar");
		lazy.put(GaeCacheKey.create("bar"), foo);

		// Check consistency
		assertCached(ms, foo, "other", "bar");
	}

}
