package net.eusashead.spring.gaecache;


import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Random;

/**
 * Tests the integration with Spring Expression Language (SpEL)
 * http://docs.spring.io/spring/docs/current/spring-framework-reference/html/expressions.html
 * @author andresesfm
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CacheConfig.class})
public class GaeCacheSpELIntegrationTest {
    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalMemcacheServiceTestConfig());

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }


    @Autowired
    TestService testService;

    @Test
    public void testCache() {
        String response1 = testService.cachedMethod("param1", "param2");
        String response2 = testService.cachedMethod("param1", "param2");
        Assert.assertEquals(response2, response1);
    }

    @Test
    public void testNumberCache() {
        String response1 = testService.cachedNumberParameterMethod(234029384L, "param2");

    }

    @Test
    public void testEvict() {
        String response1 = testService.cachedMethod("param1", "param2");
        testService.evictMethod();
    }

}

interface TestService {
    String cachedMethod(String param1, String param2);
    String cachedNumberParameterMethod(Long param1, String param2);
    void evictMethod();
}

@Component
class TestServiceImpl implements TestService {

    @Cacheable(value = "default", key = "#p0.concat('-').concat(#p1)")
    public String cachedMethod(String param1, String param2) {
        return "response " + new Random().nextInt();
    }

    @Override
    @Cacheable(value = "default", key = "#p0")
    public String cachedNumberParameterMethod(Long param1, String param2) {
        return "response " + param1 + param2;
    }

    @CacheEvict("default")
    @Override
    public void evictMethod() {

    }
}
