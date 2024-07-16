package codesquad.http;

import java.io.IOException;
import java.io.InputStream;
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

    private final Map<String, List<String>> headers = new HashMap<>();
    private final Map<String, List<String>> params = new HashMap<>();
    private final Map<String, Object> bodyParams = new HashMap<>();

    private byte[] body;

    public HttpRequest(InputStream inputStream) {
        try {
            String line = new String(readLine(inputStream));

            String[] tokens = line.split(" ");
            method = Method.of(tokens[0]);
            path = getAndGetPath(tokens[1]);
            version = tokens[2];

            line = new String(readLine(inputStream));
            while (line != null && !line.isEmpty()) {
                tokens = line.split(": ");
                headers.putIfAbsent(tokens[0], new ArrayList<>());

                StringTokenizer st = new StringTokenizer(tokens[1], ",; ");
                while (st.hasMoreTokens()) {
                    String value = st.nextToken();
                    headers.get(tokens[0]).add(value);
                }
                line = new String(readLine(inputStream));
            }

            if (existBody()) {
                int contentLength = Integer.parseInt(headers.get("Content-Length").get(0));

                this.body = new byte[contentLength];
                inputStream.read(this.body, 0, contentLength);
                String contentType = headers.get("Content-Type").get(0);
                if (contentType != null && contentType.equals("application/x-www-form-urlencoded"))
                    initFormData(URLDecoder.decode(new String(body), StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] readLine(InputStream inputStream) {
        List<Byte> result = new ArrayList<>();
        int count = 0;
        try {
            int b = 0;
            while ((b = inputStream.read()) != -1) {
                byte bytes = (byte) b;
                result.add(bytes);
                if (bytes == '\r') {
                    count = 1;
                }
                if (count == 1 && bytes == '\n') {
                    return toByteArray(result.subList(0, result.size() - 2));
                }
            }
            return toByteArray(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] toByteArray(List<Byte> bytes) {
        byte[] result = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            result[i] = bytes.get(i);
        }
        return result;
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
        List<String> content = headers.get(key);
        if (content == null) return null;
        return content.get(0);
    }

    /**
     * http request 헤더의 모든 값을 조회합니다.
     *
     * @param key header name
     * @return header values. if header key is not exit return null
     */
    public List<String> getHeaders(String key) {
        return headers.get(key);
    }

    /**
     * http request message의 body 값을 반환합니다.
     *
     * @return body value. if body is not exist, return null
     */
    public String getBody() {
        if (body == null) return null;
        return new String(body);
    }

    public byte[] getByteBody() {
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
