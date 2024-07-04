package codesquad.http;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private final String version = "HTTP/1.1";
    private HttpStatus status;
    private Map<String, String> headers = new HashMap<>();
    private byte[] body;

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

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getHeaderString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }
        return sb.toString();
    }

    public byte[] getBody() {
        return this.body;
    }

    /*----------getter end-----------*/
    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setBody(byte[] body) {
        int size = body.length;
        headers.put("Content-Length", String.valueOf(size));
        this.body = body;
    }

    public void setContentType(ContentType type) {
        headers.put("Content-Type", type.fullType);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void clear() {
        headers.clear();
        body = null;
        status = null;
    }

    public void update(HttpResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("response is null");
        }
        this.clear();
        this.status = response.getStatus();
        this.headers = response.headers;
        this.body = response.getBody();
    }
}
