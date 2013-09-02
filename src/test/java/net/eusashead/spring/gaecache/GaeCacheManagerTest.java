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

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.springframework.cache.Cache;

@RunWith(JUnit4.class)
public class GaeCacheManagerTest {

	@Test(expected=IllegalArgumentException.class)
	public void testAddNullCache() {
		GaeCacheManager manager = new GaeCacheManager();
		manager.addCache(null);
	}
	
	@Test
	public void testAddMockCache() {
		GaeCacheManager manager = new GaeCacheManager();
		GaeCache cache = Mockito.mock(GaeCache.class);
		Mockito.when(cache.getName()).thenReturn("cache");
		manager.addCache(cache);
		Collection<String> names = manager.getCacheNames();
		Assert.assertTrue(names.contains("cache"));
		Cache get = manager.getCache("cache");
		Assert.assertNotNull(get);
		Assert.assertEquals(cache, get);
	}
	
}
