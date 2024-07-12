package codesquad.context;

import codesquad.http.HttpRequest;
import codesquad.util.scan.Solo;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Solo
public class SessionContextManager {

    public final ConcurrentHashMap<String, SessionContext> sessionContexts = new ConcurrentHashMap<>();

    public String createContext() {
        String sid = UUID.randomUUID().toString();
        sessionContexts.put(sid, new SessionContext());
        return sid;
    }

    public SessionContext getContext(String sid) {
        if (sid == null) return null;
        return sessionContexts.get(sid);
    }

    public void clearContext(HttpRequest request) {
        String sid = getSessionId(request);
        sessionContexts.remove(sid);
    }

    public SessionContext getSession(HttpRequest request) {
        String sid = getSessionId(request);
        return getContext(sid);
    }

    private String getSessionId(HttpRequest request) {
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
