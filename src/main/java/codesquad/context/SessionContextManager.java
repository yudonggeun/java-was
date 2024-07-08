package codesquad.context;

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

    public static void clearContext(String sid) {
        instance.sessionContexts.remove(sid);
    }

}
