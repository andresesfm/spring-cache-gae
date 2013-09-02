package net.eusashead.spring.gaecache;

import org.springframework.util.Assert;

public class ArgumentHash {
	
	public static final String NULL_HASH = "<null>";
	
	private final String raw;
	
	private final String hash;
	
	public ArgumentHash() {
		this.hash = NULL_HASH;
		this.raw = null;
	}
	
	public ArgumentHash(String raw) {
		this(raw, new Murmur3HashAlgorithm());
	}
	
	public ArgumentHash(String raw, HashAlgorithm strategy) {
		
		// Check parameters
		Assert.notNull(raw);
		Assert.notNull(strategy);
		
		// Create hash
		String hash = strategy.hash(raw);
		
		// Set variables
		this.raw = raw;
		this.hash = hash;
	}
	
	/**
	 * The hash value
	 * of this Hash
	 * @return
	 */
	public String rawValue() {
		return this.raw;
	}
	
	/**
	 * The original 
	 * pre-hashed value
	 * @return
	 */
	public String hashValue() {
		return this.hash;
	}
	
	@Override
	public String toString() {
		return this.hash;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hash == null) ? 0 : hash.hashCode());
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
		ArgumentHash other = (ArgumentHash) obj;
		if (hash == null) {
			if (other.hash != null)
				return false;
		} else if (!hash.equals(other.hash))
			return false;
		return true;
	}

}
