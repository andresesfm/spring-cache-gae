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
		Assert.assertEquals("1", generator.generate(service, null, Integer.valueOf(1)));

		// int
		Assert.assertEquals("1", generator.generate(service, null, 1));

		// Long
		Assert.assertEquals("1", generator.generate(service, null, Long.valueOf(1)));

		// long
		Assert.assertEquals("1", generator.generate(service, null, 1l));

		// String
		Assert.assertEquals("string", generator.generate(service, null, "string"));

		// Decimal
		Assert.assertEquals("1.32", generator.generate(service, null, BigDecimal.valueOf(1.32)));

		// Float
		Assert.assertEquals("1.32", generator.generate(service, null, Float.valueOf(1.32f)));

		// float
		Assert.assertEquals("1.32", generator.generate(service, null, 1.32f));

		// double
		Assert.assertEquals("1.32", generator.generate(service, null, 1.32d));

		// Double
		Assert.assertEquals("1.32", generator.generate(service, null, Double.valueOf(1.32)));

		// Boolean
		Assert.assertEquals("true", generator.generate(service, null, Boolean.TRUE));

		// boolean
		Assert.assertEquals("true", generator.generate(service, null, true));

		// Date
		Assert.assertEquals("Fri Jan 02 11:17:36 GMT 1970", generator.generate(service, null, new Date(123456789l)));
	}

	@Test
	public void testSingleArgumentNull() {
		Object key = generator.generate(service, null, new Object[0]);
		Assert.assertEquals("", key.toString());
	}

	@Test
	public void testSingleArgumentObject() {
		Object key = generator.generate(service, null, new FooKey(1l));
		Assert.assertEquals("FooKey [id=1]", key.toString());
	}

	@Test
	public void testMultiArgument() {
		Object key = generator.generate(service, null, 1, true, 2.32d);
		Assert.assertEquals("1,true,2.32", key.toString());
	}
	
	@Test
	public void testMultiArgumentNull() {
		Assert.assertEquals(",true,2.32", generator.generate(service, null, null, true, 2.32d));
		Assert.assertEquals("1,,2.32", generator.generate(service, null, 1, null, 2.32d));
		Assert.assertEquals("1,true,", generator.generate(service, null, 1, true, null));
	}

	@Test
	public void testRegisterStrategy() {
		generator.registerStrategy(Foo.class, new FooKeyGeneratorStrategy());
		Assert.assertEquals("foo", generator.generate(service, null, new Foo()));
		
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
