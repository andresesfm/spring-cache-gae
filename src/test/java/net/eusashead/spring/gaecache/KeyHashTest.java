package net.eusashead.spring.gaecache;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class KeyHashTest {

	@Test
	public void testEmpty() {
		Assert.assertEquals("00000000", KeyHash.hash(""));
	}
	
	@Test(expected=NullPointerException.class)
	public void testNull() {
		KeyHash.hash(null);
	}
}
