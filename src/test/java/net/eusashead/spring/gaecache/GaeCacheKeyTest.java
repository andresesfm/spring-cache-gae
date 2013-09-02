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

@RunWith(JUnit4.class)
public class GaeCacheKeyTest {
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullArgument() {
		new GaeCacheKey((ArgumentHash)null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testZeroLengthArgument() {
		new GaeCacheKey(new ArgumentHash[0]);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullElementArgument() {
		new GaeCacheKey(new ArgumentHash[]{null});
	}
	
	@Test
	public void testSingleNullArgumentHash() {
		GaeCacheKey key = new GaeCacheKey(new ArgumentHash());
		Assert.assertEquals(ArgumentHash.NULL_HASH, key.hashValue());
	}
	
	@Test
	public void testSingleArgumentHash() {
		GaeCacheKey key = new GaeCacheKey(new ArgumentHash("hash"));
		Assert.assertEquals(new ArgumentHash("hash").hashValue(), key.hashValue());
		Assert.assertEquals("hash", key.rawValue());
	}
	
	@Test
	public void testMultiNullArgumentHash() {
		GaeCacheKey key = new GaeCacheKey(new ArgumentHash[]{new ArgumentHash(), new ArgumentHash()});
		Assert.assertEquals(ArgumentHash.NULL_HASH + "-" + ArgumentHash.NULL_HASH, key.hashValue());
	}
	
	@Test
	public void testMultiArgumentHash() {
		GaeCacheKey key = new GaeCacheKey(new ArgumentHash[]{new ArgumentHash("hash"), new ArgumentHash("hash")});
		Assert.assertEquals(new ArgumentHash("hash").hashValue() + "-" + new ArgumentHash("hash").hashValue(), key.hashValue());
		Assert.assertEquals("hash,hash", key.rawValue());
	}
	
	@Test
	public void testCreateNull() {
		GaeCacheKey key = GaeCacheKey.create(null);
		Assert.assertEquals(ArgumentHash.NULL_HASH, key.hashValue());
		Assert.assertEquals("null", key.rawValue());
	}
	
	@Test
	public void testCreateNotNull() {
		GaeCacheKey key = GaeCacheKey.create("hash");
		Assert.assertEquals(new ArgumentHash("hash").hashValue(), key.hashValue());
		Assert.assertEquals("hash", key.rawValue());
	}

}
