package codesquad.application.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyRepository {

    public static final MyRepository source = new MyRepository();

    private final Map<String, Object> map = new ConcurrentHashMap<>();

    public void save(String key, Object value) {
        map.put(key, value);
    }

    public void find(String key) {
        map.get(key);
    }

    public void delete(String key) {
        map.remove(key);
    }
}
