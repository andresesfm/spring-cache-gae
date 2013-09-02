package net.eusashead.spring.gaecache;

import java.nio.charset.Charset;

import com.google.common.hash.Hashing;

public class Murmur3HashAlgorithm implements HashAlgorithm {
	
	/**
	 * Returns a string containing each byte 
	 * a Murmurhash3 32 bit hash of this string
	 * in order, as a two-digit unsigned hexadecimal 
	 * number in lower case
	 * @param key
	 * @return
	 */
	public String hash(String key) {
		return Hashing.murmur3_32().hashBytes(key.getBytes(Charset.forName("UTF-8"))).toString();
	}

}
