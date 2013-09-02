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


import java.math.BigDecimal;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static net.eusashead.spring.gaecache.GaeCacheAssert.keyToString;

@RunWith(JUnit4.class)
public class GaeCacheKeyGeneratorTest {

	private final FooService service = new FooService();

	private GaeCacheKeyGenerator generator;

	@Before
	public void before() {
		generator = new GaeCacheKeyGenerator();
	}

	@Test
	public void testSingleArgumentPrimitive() {

		// Integer
		Assert.assertEquals(keyToString(Integer.valueOf(1)), generator.generate(service, null, Integer.valueOf(1)));

		// int
		Assert.assertEquals(keyToString(1), generator.generate(service, null, 1));

		// Long
		Assert.assertEquals(keyToString(Long.valueOf(1)), generator.generate(service, null, Long.valueOf(1)));

		// long
		Assert.assertEquals(keyToString(1l), generator.generate(service, null, 1l));

		// String
		Assert.assertEquals(keyToString("string"), generator.generate(service, null, "string"));

		// Decimal
		Assert.assertEquals(keyToString(BigDecimal.valueOf(1.32)), generator.generate(service, null, BigDecimal.valueOf(1.32)));

		// Float
		Assert.assertEquals(keyToString(Float.valueOf(1.32f)), generator.generate(service, null, Float.valueOf(1.32f)));

		// float
		Assert.assertEquals(keyToString(1.32f), generator.generate(service, null, 1.32f));

		// double
		Assert.assertEquals(keyToString(1.32d), generator.generate(service, null, 1.32d));

		// Double
		Assert.assertEquals(keyToString(Double.valueOf(1.32)), generator.generate(service, null, Double.valueOf(1.32)));

		// Boolean
		Assert.assertEquals(keyToString(Boolean.TRUE), generator.generate(service, null, Boolean.TRUE));

		// boolean
		Assert.assertEquals(keyToString(true), generator.generate(service, null, true));

		// Date
		Assert.assertEquals(keyToString(new Date(123456789l)), generator.generate(service, null, new Date(123456789l)));
	}

	@Test
	public void testSingleArgumentNull() {
		Object key = generator.generate(service, null, new Object[0]);
		Assert.assertEquals(keyToString(new Object[0]), key.toString());
	}

	@Test
	public void testSingleArgumentObject() {
		Object key = generator.generate(service, null, new FooKey(1l));
		Assert.assertEquals(keyToString(key), key.toString());
	}

	@Test
	public void testMultiArgument() {
		Object[] args = new Object[]{1, true, 2.32d};
		Object key = generator.generate(service, null, args);
		Assert.assertEquals(keyToString(args), key.toString());
	}
	
	@Test
	public void testMultiArgumentNull() {
		Object[] args1 = new Object[]{null, true, 2.32d};
		Assert.assertEquals(keyToString(args1), generator.generate(service, null, args1));
		Object[] args2 = new Object[]{1, null, 2.32d};
		Assert.assertEquals(keyToString(args2), generator.generate(service, null, args2));
		Object[] args3 = new Object[]{1, true, null};
		Assert.assertEquals(keyToString(args3), generator.generate(service, null, args3));
	}

	@Test
	public void testRegisterStrategy() {
		FooKeyGeneratorStrategy strategy = new FooKeyGeneratorStrategy();
		generator.registerStrategy(Foo.class, strategy);
		Foo foo = new Foo();
		Assert.assertEquals(strategy.getKey(foo), generator.generate(service, null, foo));
		
	}

	@Test(expected=IllegalArgumentException.class)
	public void testRegisterStrategyNullType() {
		generator.registerStrategy(null, new DefaultKeyGeneratorStrategy());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testRegisterNullStrategy() {
		generator.registerStrategy(Date.class, null);
	}
}

class FooService {
	public Foo getFoo() {
		return new Foo(new FooKey(1l), "foo");
	}
}

class FooKeyGeneratorStrategy implements KeyGeneratorStrategy<Foo> {

	@Override
	public String getKey(Object keySource) {
		return "foo";
	}
	
}
