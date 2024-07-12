package codesquad.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * http request message를 파싱하여 HttpRequest 객체를 생성합니다.
 *
 * @author yudonggeun
 */
public class HttpRequest {

    public final Method method;
    public final String path;
    public final String version;

    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, List<String>> params = new HashMap<>();
    private final Map<String, Object> bodyParams = new HashMap<>();

    private String body;

    public HttpRequest(InputStream inputStream) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line = br.readLine();

            String[] tokens = line.split(" ");
            method = Method.of(tokens[0]);
            path = getAndGetPath(tokens[1]);
            version = tokens[2];

            line = br.readLine();
            while (line != null && !line.isEmpty()) {
                tokens = line.split(": ");
                headers.put(tokens[0], tokens[1]);
                line = br.readLine();
            }

            if (existBody()) {
                int contentLength = Integer.parseInt(headers.get("Content-Length"));
                char[] body = new char[contentLength];
                br.read(body, 0, contentLength);
                this.body = URLDecoder.decode(new String(body), StandardCharsets.UTF_8);
                if (headers.get("Content-Type").equals("application/x-www-form-urlencoded"))
                    initFormData(this.body);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * body에 포함된 데이터를 파싱하여 Map에 저장합니다.
     * 하나의 속성이 존재하는 값은 String to String으로 저장하고,
     * 여러개의 속성이 존재하는 값은 String to List<String>으로 저장합니다.
     *
     * @param body body에 포함된 데이터
     */
    private void initFormData(String body) {
        StringTokenizer st = new StringTokenizer(body, "&");
        while (st.hasMoreTokens()) {
            String[] tokens = st.nextToken().split("=");
            if (tokens.length <= 1) continue;
            if (bodyParams.containsKey(tokens[0])) {
                String type = (String) bodyParams.get(tokens[0]);
                bodyParams.put(tokens[0], new ArrayList<>());
                ArrayList<String> values = (ArrayList<String>) bodyParams.get(tokens[0]);
                values.add(type);
                values.add(tokens[1]);
            } else {
                bodyParams.put(tokens[0], tokens[1]);
            }
        }
    }

    /**
     * path 에 포함된 Query Parameter를 추출하여 Map에 저장하고, path에서 Query Parameter를 제외한 path를 반환합니다.
     *
     * @param path Query Parameter가 포함된 path
     * @return Query Parameter가 제거된 path
     */
    private String getAndGetPath(String path) {
        int index = path.indexOf("?");
        if (index == -1) return path;
        String result = path.substring(0, index);
        StringTokenizer st = new StringTokenizer(path.substring(index + 1), "&");
        while (st.hasMoreTokens()) {
            String[] tokens = st.nextToken().split("=");
            if (!params.containsKey(tokens[0])) {
                params.put(tokens[0], new ArrayList<>());
            }
            params.get(tokens[0]).add(tokens[1]);
        }
        return result;
    }

    private boolean existBody() {
        return headers.containsKey("Content-Length");
    }

    /*----------- getter --------------*/

    public String getParam(String key) {
        if (!params.containsKey(key)) return null;
        return params.get(key).get(0);
    }

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

    public String getParams() {
        return params.toString();
    }

    public Object getBodyParam(String attr) {
        return bodyParams.get(attr);
    }
}
