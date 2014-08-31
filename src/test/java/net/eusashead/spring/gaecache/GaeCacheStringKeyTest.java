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

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;

/**
 * Tests basic operations with a String key, this is required to support
 * Spring Expression Language (SpEL).
 * @author andresesfm
 */
@RunWith(JUnit4.class)
public class GaeCacheStringKeyTest {
	private static final String FQ_NAMESPACE = "__NAMESPACE__name";
	private static final String NS_KEY = FQ_NAMESPACE + "_100_";
	
	private static final Expiration expiry = Expiration.byDeltaSeconds(10);


    @Test
    public void testGetWithStringKey() throws Exception {
        MemcacheService service = Mockito.mock(MemcacheService.class);
        GaeCache cache = new GaeCache("name", service, expiry);
        FooKey key = new FooKey(1l);
        String stringKey = "11-DS34";
        GaeCacheKey gaeCacheKey = GaeCacheKey.create(stringKey);
        Foo value = new Foo(key, "foo");
        Mockito.when(service.get(FQ_NAMESPACE)).thenReturn(100);
        Mockito.when(service.get(NS_KEY + gaeCacheKey)).thenReturn(value);
        ValueWrapper retObj = cache.get(stringKey);
        Mockito.verify(service, Mockito.atLeastOnce()).get(FQ_NAMESPACE);
        Mockito.verify(service, Mockito.atLeastOnce()).get(NS_KEY + gaeCacheKey);
        Assert.assertNotNull(retObj);
        Assert.assertNotNull(retObj.get());
        Assert.assertTrue(Foo.class.isAssignableFrom(retObj.get().getClass()));
        Foo retFoo = Foo.class.cast(retObj.get());
        Assert.assertEquals(retFoo, value);
    }
	
	@Test
	public void testPutWithStringKey() throws Exception {
		MemcacheService service = Mockito.mock(MemcacheService.class);
		GaeCache cache = new GaeCache("name", service, expiry);
		FooKey key = new FooKey(1l);
        String stringKey = "11-DS34";
        GaeCacheKey gaeCacheKey = GaeCacheKey.create(stringKey);
		Foo value = new Foo(key, "foo");
		Mockito.when(service.get(FQ_NAMESPACE)).thenReturn(100);
		cache.put(stringKey, value);
		Mockito.verify(service, Mockito.atLeastOnce()).put(NS_KEY + gaeCacheKey, value, expiry);
	}
	
	@Test
	public void testEvictWithStringKey() throws Exception {
		MemcacheService service = Mockito.mock(MemcacheService.class);
		GaeCache cache = new GaeCache("name", service, expiry);
		FooKey key = new FooKey(1l);
		Mockito.when(service.get(FQ_NAMESPACE)).thenReturn(100);
		Mockito.when(service.delete(NS_KEY + key)).thenReturn(true);
        String stringKey = "11-DS34";
        GaeCacheKey gaeCacheKey = GaeCacheKey.create(stringKey);
		cache.evict(stringKey);
		Mockito.verify(service, Mockito.atLeastOnce()).delete(NS_KEY + gaeCacheKey);
	}

}
