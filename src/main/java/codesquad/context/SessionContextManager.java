package codesquad.context;

import codesquad.http.HttpRequest;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionContextManager {

    private static final SessionContextManager instance = new SessionContextManager();
    public final ConcurrentHashMap<String, SessionContext> sessionContexts = new ConcurrentHashMap<>();

    public static String createContext() {
        String sid = UUID.randomUUID().toString();
        instance.sessionContexts.put(sid, new SessionContext());
        return sid;
    }

    public static SessionContext getContext(String sid) {
        return instance.sessionContexts.get(sid);
    }

    public static void clearContext(HttpRequest request) {
        String sid = getSessionId(request);
        instance.sessionContexts.remove(sid);
    }

    public static SessionContext getSession(HttpRequest request) {
        String sid = getSessionId(request);
        return getContext(sid);
    }

    private static String getSessionId(HttpRequest request) {
        String sid = null;
        String cookies = request.getHeader("Cookie");
        if (cookies == null) return null;
        cookies = cookies.replace(";", "");
        for (String cookie : cookies.split(" ")) {
            String[] entry = cookie.split("=");
            String key = entry[0];
            String value = entry[1];
            if (key.equals("SID")) {
                sid = value;
                break;
            }
        }
        return sid;
    }

}
