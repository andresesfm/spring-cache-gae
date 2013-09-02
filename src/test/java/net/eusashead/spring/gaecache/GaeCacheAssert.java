package net.eusashead.spring.gaecache;

import org.junit.Assert;

import com.google.appengine.api.memcache.MemcacheService;

public class GaeCacheAssert {

	public static void assertCached(MemcacheService ms, Object value, String namespace, Object... args) {
		Integer nsKey = getNsKey(ms, namespace);
		GaeCacheKey key = keyToString(args);
		Assert.assertTrue(ms.contains("__NAMESPACE__" + namespace + "_" + nsKey + "_" + key));
		Assert.assertEquals(value, ms.get("__NAMESPACE__" + namespace + "_" + nsKey + "_" + key));
	}
	
	public static void assertNotCached(MemcacheService ms, String namespace, Object... args) {
		Integer nsKey = getNsKey(ms, namespace);
		GaeCacheKey key = keyToString(args);
		Assert.assertFalse(ms.contains("__NAMESPACE__" + namespace + "_" + nsKey + "_" + key));
	}

	public static Integer getNsKey(MemcacheService ms, String namespace) {
		return (Integer)ms.get("__NAMESPACE__" + namespace);
	}
	
	public static GaeCacheKey keyToString(Object...args) {
		ArgumentHash[] hash = args.length == 0 ? new ArgumentHash[]{new ArgumentHash()} : new ArgumentHash[args.length];
		for (int i=0;i<args.length;i++) {
			Object obj = args[i];
			if (obj == null) {
				hash[i] = new ArgumentHash();
			} else {
				hash[i] = new ArgumentHash(obj.toString());
			}
		}
		return new GaeCacheKey(hash);
	}
	
}
