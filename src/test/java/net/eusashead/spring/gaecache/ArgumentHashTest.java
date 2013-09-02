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

@RunWith(JUnit4.class)
public class ArgumentHashTest {
	
	@Test
	public void testEmptyConstructor() {
		ArgumentHash hash = new ArgumentHash();
		Assert.assertEquals(ArgumentHash.NULL_HASH, hash.hashValue());
		Assert.assertNull(hash.rawValue());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullConstructor() {
		new ArgumentHash(null);
	}
	
	@Test
	public void testValidConstructor() {
		ArgumentHash hash = new ArgumentHash("");
		Assert.assertEquals("", hash.rawValue());
		Assert.assertEquals(new Murmur3HashAlgorithm().hash(""), hash.hashValue());
	}
	
	@Test
	public void testMockStrategy() {
		HashAlgorithm algo = Mockito.mock(HashAlgorithm.class);
		Mockito.when(algo.hash("raw")).thenReturn("hash");
		ArgumentHash hash = new ArgumentHash("raw", algo);
		Assert.assertEquals("raw", hash.rawValue());
		Assert.assertEquals("hash", hash.hashValue());
		Mockito.verify(algo, Mockito.atLeastOnce()).hash("raw");
	}
}
