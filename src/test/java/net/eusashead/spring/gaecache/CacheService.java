package net.eusashead.spring.gaecache;

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
