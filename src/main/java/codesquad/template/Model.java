package codesquad.template;

import codesquad.context.SessionContext;

import java.util.HashMap;
import java.util.Map;

public class Model {

    private final Map<String, Object> data = new HashMap<>();

    public void addAttribute(String key, Object value) {
        data.put(key, value);
    }

    public void setSession(SessionContext session) {
        if (session != null) data.put("session", session.getAttributes());
    }

    public Object getAttribute(String key) {
        // default value
        if (key.equals("null")) return null;
        if (isNumber(key)) return Integer.parseInt(key);

        // attribute
        return getAttribute(key, data);
    }

    private boolean isNumber(String key) {
        try {
            Integer.parseInt(key);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private Object getAttribute(String key, Map<String, Object> map) {
        if (key.contains(".")) {
            int i = key.indexOf(".");
            Object nextMap = map.get(key.substring(0, i));
            if (nextMap instanceof Map)
                return getAttribute(key.substring(i + 1), (Map<String, Object>) nextMap);
            else
                return null;
        }
        return map.get(key);
    }
}
