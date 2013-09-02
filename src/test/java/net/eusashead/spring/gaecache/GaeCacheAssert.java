package net.eusashead.spring.gaecache;

import org.junit.Assert;

import com.google.appengine.api.memcache.MemcacheService;

public class GaeCacheAssert {

	public static void assertCached(MemcacheService ms, Object value, String namespace, Object... args) {
		Integer nsKey = getNsKey(ms, namespace);
		String key = keyToString(args);
		Assert.assertTrue(ms.contains("__NAMESPACE__" + namespace + "_" + nsKey + "_" + key));
		Assert.assertEquals(value, ms.get("__NAMESPACE__" + namespace + "_" + nsKey + "_" + key));
	}
	
	public static void assertNotCached(MemcacheService ms, String namespace, Object... args) {
		Integer nsKey = getNsKey(ms, namespace);
		String key = keyToString(args);
		Assert.assertFalse(ms.contains("__NAMESPACE__" + namespace + "_" + nsKey + "_" + key));
	}

	public static Integer getNsKey(MemcacheService ms, String namespace) {
		return (Integer)ms.get("__NAMESPACE__" + namespace);
	}
	
	public static String keyToString(Object...args) {
		StringBuilder compoundKey = new StringBuilder();
		//compoundKey.append("<key<params<");
		for (int i=0;i<args.length;i++) {
			//compoundKey.append("<p");
			//compoundKey.append(i);
			//compoundKey.append("=");
			Object obj = args[i];
			if (obj == null) {
				compoundKey.append("");
			} else {
				compoundKey.append(KeyHash.hash(obj.toString()));
			}
			//compoundKey.append(">");
			if (i < args.length - 1) {
				compoundKey.append(",");
			}
		}
		//compoundKey.append(">>>");
		return compoundKey.toString();
	}
	
}
