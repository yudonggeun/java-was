package codesquad.context;

import codesquad.http.HttpRequest;
import codesquad.util.scan.Solo;

import java.util.List;
import java.util.Optional;
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
        List<String> cookies = request.getHeaders("Cookie");
        if (cookies == null) return null;
        Optional<String> optionalSessionCookie = cookies.stream().filter(cookie -> cookie.startsWith("SID")).findAny();

        if (optionalSessionCookie.isEmpty()) return null;
        String sessionCookie = optionalSessionCookie.get();
        String[] entry = sessionCookie.split("=");
        String value = entry[1];
        return value;
    }

}
