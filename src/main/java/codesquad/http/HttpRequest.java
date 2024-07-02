package codesquad.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * http request message를 파싱하여 HttpRequest 객체를 생성합니다.
 *
 * @author yudonggeun
 */
public class HttpRequest {

    public final String method;
    public final String path;
    public final String version;

    private final Map<String, String> headers = new HashMap<>();
    private String body;

    public HttpRequest(InputStream inputStream) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line = br.readLine();

            String[] tokens = line.split(" ");
            method = tokens[0];
            path = tokens[1];
            version = tokens[2];

            while (!(line = br.readLine()).isEmpty()) {
                tokens = line.split(": ");
                headers.put(tokens[0], tokens[1]);
            }

            if (existBody()) {
                int contentLength = Integer.parseInt(headers.get("Content-Length"));
                char[] body = new char[contentLength];
                br.read(body, 0, contentLength);
                this.body = new String(body);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean existBody() {
        return headers.containsKey("Content-Length");
    }

    /*----------- getter --------------*/

    /**
     * http request message의 header 값을 반환합니다.
     *
     * @param key header name
     * @return header value. if header key is not exist, return null
     */
    public String getHeader(String key) {
        return headers.get(key);
    }

    /**
     * http request message의 body 값을 반환합니다.
     *
     * @return body value. if body is not exist, return null
     */
    public String getBody() {
        return body;
    }

    public String getHeaders() {
        return headers.toString();
    }
}
