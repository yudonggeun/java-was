package codesquad.context;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class ApplicationContextTest {

    @Test
    public void test() {

        Map<Class<?>, Object> map = new HashMap<>();

        map.put(ApplicationContextTest.class, new ApplicationContextTest());
        Object o = map.get(ApplicationContextTest.class);
        System.out.println(o.getClass());
    }
}