package codesquad.application.repository;

import codesquad.application.domain.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyRepository {

    public static final MyRepository source = new MyRepository();

    public MyRepository() {
        this.map.put("admin", new User("admin", "admin", "admin"));
    }

    private final Map<String, Object> map = new ConcurrentHashMap<>();

    public void save(String key, Object value) {
        map.put(key, value);
    }

    public User findUser(String key) {
        if (key == null) return null;
        return (User) map.get(key);
    }

    public List<User> findAllUser() {
        return map.values().stream()
                .filter(value -> value instanceof User)
                .map(value -> (User) value).toList();
    }

    public void delete(String key) {
        map.remove(key);
    }

}
