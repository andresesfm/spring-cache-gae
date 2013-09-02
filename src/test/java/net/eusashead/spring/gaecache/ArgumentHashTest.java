package net.eusashead.spring.gaecache;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

@RunWith(JUnit4.class)
public class ArgumentHashTest {
	
	// TODO should we allow null constructor arguments? 
	// The hash value could then be NULL_HASH
	// we wouldn't need to handle null in the GaeCacheKey so carefully
	
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
