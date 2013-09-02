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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;

@RunWith(JUnit4.class)
public class GaeCacheTest {
	private static final String FQ_NAMESPACE = "__NAMESPACE__name";
	private static final String NS_KEY = FQ_NAMESPACE + "_100_";
	
	private static final Expiration expiry = Expiration.byDeltaSeconds(10);

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
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullMemcacheService() throws Exception {
		new GaeCache("name", null, expiry);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullExpiration() throws Exception {
		new GaeCache("name", Mockito.mock(MemcacheService.class), null);
	}
	
	@Test
	public void testConstructWithService() throws Exception {
		MemcacheService service = Mockito.mock(MemcacheService.class);
		GaeCache cache = new GaeCache("name", service, expiry);
		Assert.assertEquals(service, cache.getNativeCache());
	}
	
	@Test
	public void testClear() throws Exception {
		MemcacheService service = Mockito.mock(MemcacheService.class);
		GaeCache cache = new GaeCache("name", service, expiry);
		Mockito.when(service.contains(FQ_NAMESPACE)).thenReturn(true);
		cache.clear();
		Mockito.verify(service, Mockito.atLeastOnce()).increment(FQ_NAMESPACE, 1);
	}
	
	@Test
	public void testGet() throws Exception {
		MemcacheService service = Mockito.mock(MemcacheService.class);
		GaeCache cache = new GaeCache("name", service, expiry);
		FooKey key = new FooKey(1l);
		Foo value = new Foo(key, "foo");
		GaeCacheKey cacheKey = GaeCacheKey.create(key.toString());
		Mockito.when(service.get(FQ_NAMESPACE)).thenReturn(100);
		Mockito.when(service.get(NS_KEY + cacheKey)).thenReturn(value);
		ValueWrapper retObj = cache.get(cacheKey);
		Mockito.verify(service, Mockito.atLeastOnce()).get(FQ_NAMESPACE);
		Mockito.verify(service, Mockito.atLeastOnce()).get(NS_KEY + cacheKey);
		Assert.assertNotNull(retObj);
		Assert.assertNotNull(retObj.get());
		Assert.assertTrue(Foo.class.isAssignableFrom(retObj.get().getClass()));
		Foo retFoo = Foo.class.cast(retObj.get());
		Assert.assertEquals(retFoo, value);
	}
	
	@Test
	public void testPut() throws Exception {
		MemcacheService service = Mockito.mock(MemcacheService.class);
		GaeCache cache = new GaeCache("name", service, expiry);
		FooKey key = new FooKey(1l);
		Foo value = new Foo(key, "foo");
		Mockito.when(service.get(FQ_NAMESPACE)).thenReturn(100);
		GaeCacheKey cacheKey = GaeCacheKey.create(key.toString());
		cache.put(cacheKey, value);
		Mockito.verify(service, Mockito.atLeastOnce()).put(NS_KEY + cacheKey, value, expiry);
	}
	
	@Test
	public void testEvict() throws Exception {
		MemcacheService service = Mockito.mock(MemcacheService.class);
		GaeCache cache = new GaeCache("name", service, expiry);
		FooKey key = new FooKey(1l);
		Mockito.when(service.get(FQ_NAMESPACE)).thenReturn(100);
		Mockito.when(service.delete(NS_KEY + key)).thenReturn(true);
		GaeCacheKey cacheKey = GaeCacheKey.create(key.toString());
		cache.evict(cacheKey);
		Mockito.verify(service, Mockito.atLeastOnce()).delete(NS_KEY + cacheKey);
	}

}
