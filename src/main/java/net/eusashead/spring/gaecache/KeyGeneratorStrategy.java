package net.eusashead.spring.gaecache;

/**
 * Strategy used to generate
 * a {@link String} key from a
 * supplied object for use in 
 * a {@link GaeCache}
 * 
 * The key should be unique
 * and based on the state of the 
 * object. Equal objects should have
 * equal keys.
 * @author patrickvk
 *
 * @param <T>
 */
public interface KeyGeneratorStrategy<T> {
	
	/**
	 * Perform the key conversion
	 * for the supplied instance
	 * 
	 * @param keySource
	 * @return unique {@link String} based on object state
	 */
	String getKey(Object keySource);
	
}