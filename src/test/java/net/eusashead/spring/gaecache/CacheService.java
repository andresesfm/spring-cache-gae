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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

	private final AtomicLong id = new AtomicLong(0);

	@Cacheable(value="default")
	public Foo getFoo(String name) {
		Foo foo = new Foo(new FooKey(this.id.incrementAndGet()), name);
		return foo;
	}

	@Cacheable(value="objectKey")
	public Foo getFooByKey(FooKey key) {
		Foo foo = new Foo(key, key.getId().toString());
		return foo;
	}

	@Cacheable(value="list")
	public List<Foo> listFoos() {
		List<Foo> foos = new ArrayList<>();
		foos.add(new Foo(new FooKey(1l), "foo"));
		foos.add(new Foo(new FooKey(2l), "bar"));
		foos.add(new Foo(new FooKey(3l), "baz"));
		return foos;
	}
	
	@Caching (
			evict={@CacheEvict(value="list", allEntries=true)},
			put={@CachePut(value="objectKey", condition="#foo.id != null", key="#foo.id")}
	)
	public Foo saveFoo(Foo foo) {
		return new Foo(foo.getId(), "updated"); 
	}

	public Long getLastId() {
		return this.id.get();
	}

}
