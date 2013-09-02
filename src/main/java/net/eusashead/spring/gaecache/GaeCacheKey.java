package net.eusashead.spring.gaecache;

import java.util.Arrays;

import org.springframework.util.Assert;

/**
 * A cache key for {@link GaeCache}
 * that comprises of an array of 
 * {@link ArgumentHash} values of the 
 * method arguments for the cached
 * method call.
 * 
 * @author patrickvk
 *
 */
public class GaeCacheKey {
	
	/**
	 * Cache key is composed of an array of Hash values
	 */
	private final ArgumentHash[] hashes;
	
	public GaeCacheKey(ArgumentHash arg) {
		this(new ArgumentHash[]{arg});
	}
	
	public GaeCacheKey(ArgumentHash[] args) {
		Assert.notNull(args);
		Assert.notEmpty(args);
		Assert.noNullElements(args);
		this.hashes = args;
	}
	
	/**
	 * Return dash-separated
	 * hash values based on the 
	 * internal {@link ArgumentHash} array
	 * @return
	 */
	public String hashValue() {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < hashes.length; i++) {
			ArgumentHash hash = hashes[i];
			if (hash == null) {
				b.append(ArgumentHash.NULL_HASH);
			} else {
				b.append(hash.hashValue());
			}
			if (i < hashes.length - 1) {
				b.append("-");
			}
		}
		return b.toString();
	}
	
	/**
	 * Human-readable representation
	 * of un-hashed raw values of the
	 * underlying {@link ArgumentHash} array
	 * 
	 * @return
	 */
	public String rawValue() {
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < hashes.length; i++) {
			ArgumentHash hash = hashes[i];
			if (hash == null) {
				b.append("null");
			} else {
				b.append(hash.rawValue());
			}
			if (i < hashes.length - 1) {
				b.append(",");
			}
		}
		return b.toString();
	}
	
	@Override
	public String toString() {
		return hashValue();
	}
	
	public static GaeCacheKey create(String raw) {
		return (raw != null ? new GaeCacheKey(new ArgumentHash(raw)) : new GaeCacheKey(new ArgumentHash()));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(hashes);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GaeCacheKey other = (GaeCacheKey) obj;
		if (!Arrays.equals(hashes, other.hashes))
			return false;
		return true;
	}
	
}
