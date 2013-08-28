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

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.google.appengine.api.datastore.KeyFactory;

@Service
public class CacheService {
	
	private final AtomicLong id = new AtomicLong(0);
	
	@Cacheable(value="default")
	public Foo getFoo(String name) {
		Foo foo = new Foo(name);
		foo.setId(KeyFactory.createKey("Foo", this.id.incrementAndGet()));
		return foo;
	}
	
	@Cacheable(value="objectKey")
	public Foo getFooByKey(FooKey key) {
		Foo foo = new Foo(key.getId().toString());
		foo.setId(KeyFactory.createKey("Foo", this.id.incrementAndGet()));
		return foo;
	}
	
	public Long getLastId() {
		return this.id.get();
	}

}
