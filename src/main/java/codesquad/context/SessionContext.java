package codesquad.context;

import java.util.HashMap;
import java.util.Map;

public class SessionContext {

    private final Map<String, Object> attributes = new HashMap<>();

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public void setAttributes(String key, Object value) {
        attributes.put(key, value);
    }
}
