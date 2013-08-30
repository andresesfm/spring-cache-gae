package net.eusashead.spring.gaecache;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

@RunWith(JUnit4.class)
public class DefaultKeyGeneratorStrategyTest {

	@Test
	public void testStrategy() {
		Object source = Mockito.mock(Object.class);
		Mockito.when(source.toString()).thenReturn("string");
		DefaultKeyGeneratorStrategy strat = new DefaultKeyGeneratorStrategy();
		String key = strat.getKey(source);
		Assert.assertEquals("string", key);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullSource() {
		Object source = null;
		DefaultKeyGeneratorStrategy strat = new DefaultKeyGeneratorStrategy();
		strat.getKey(source);
	}
}
