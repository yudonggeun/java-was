package codesquad.http;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private final String version = "HTTP/1.1";
    private HttpStatus status;
    private final Map<String, String> headers = new HashMap<>();
    private String body;

    private HttpResponse(HttpStatus status) {
        this.status = status;
    }

    public static HttpResponse of(HttpStatus status) {
        return new HttpResponse(status);
    }

    /*------------getter-------------*/
    public String getVersion() {
        return version;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
    /*----------getter end-----------*/

    public String getHeaderString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }
        return sb.toString();
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void clear() {
        headers.clear();
        body = null;
        status = null;
    }
}
