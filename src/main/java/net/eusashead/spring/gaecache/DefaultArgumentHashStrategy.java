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

import org.springframework.util.Assert;

/**
 * Default implementation
 * simply calls the supplied
 * object's toString() method
 * 
 * @author patrickvk
 *
 */
public class DefaultArgumentHashStrategy implements ArgumentHashStrategy<Object> {
	
	/* (non-Javadoc)
	 * @see net.eusashead.spring.gaecache.KeyGeneratorStrategy#getKey(java.lang.Object)
	 */
	@Override
	public ArgumentHash hash(Object keySource) {
		Assert.notNull(keySource);
		return new ArgumentHash(keySource.toString());
	}
	
}