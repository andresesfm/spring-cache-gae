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
