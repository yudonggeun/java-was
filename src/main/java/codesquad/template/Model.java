package codesquad.template;

import codesquad.context.SessionContext;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Model {

    private final Map<String, Object> data = new HashMap<>();
    private static final Pattern numberPattern = Pattern.compile("\\d+");
    private static final Pattern stringPattern = Pattern.compile("String\\(.*\\)");

    public void addAttribute(String key, Object value) {
        data.put(key, value);
    }

    public void setSession(SessionContext session) {
        if (session != null) data.put("session", session.getAttributes());
    }

    public Object getAttribute(String key) {
        // default value
        if (key.equals("null")) return null;
        if (numberPattern.matcher(key).matches()) return Integer.parseInt(key);
        if (stringPattern.matcher(key).matches()) return key.substring(7, key.length() - 1);

        // attribute
        return getAttribute(key, data);
    }

    private Object getAttribute(String attr, Map<String, Object> map) {
        if (attr.contains(".")) {
            int i = attr.indexOf(".");
            String key = attr.substring(0, i);
            String value = attr.substring(i + 1);

            Object source = map.get(key);
            if (source instanceof Map) {
                return getAttribute(value, (Map<String, Object>) source);
            } else if (source != null) {
                Field[] fields = source.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if (field.getName().equals(value)) {
                        try {
                            field.setAccessible(true);
                            return field.get(source);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                return null;
            }
        }
        return map.get(attr);
    }
}
