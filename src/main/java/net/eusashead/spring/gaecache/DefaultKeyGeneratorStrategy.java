package net.eusashead.spring.gaecache;

import org.springframework.util.Assert;

/**
 * Default implementation
 * simply calls the supplied
 * object's toString() method
 * 
 * @author patrickvk
 *
 */
public class DefaultKeyGeneratorStrategy implements KeyGeneratorStrategy<Object> {
	
	/* (non-Javadoc)
	 * @see net.eusashead.spring.gaecache.KeyGeneratorStrategy#getKey(java.lang.Object)
	 */
	@Override
	public String getKey(Object keySource) {
		Assert.notNull(keySource);
		return keySource.toString();
	}
	
}