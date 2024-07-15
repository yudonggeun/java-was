package codesquad.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultipartContent {

    public final String name;
    public final String filename;
    public final ContentType contentType;
    public final byte[] type;
    public final Map<String, String> headers;

    public MultipartContent(String filename, String name, ContentType contentType, byte[] type, Map<String, String> headers) {
        this.filename = filename;
        this.name = name;
        this.contentType = contentType;
        this.type = type;
        this.headers = headers;
    }

    public static List<MultipartContent> parse(String boundary, byte[] body) {

        List<MultipartContent> multipartRequests = new ArrayList<>();
        boundary = "--" + boundary + "\r\n";

        byte[][] multipartDatas = splitByte(body, boundary.getBytes());

        for (byte[] multipartData : multipartDatas) {
            Map<String, String> multipartHeaders = new HashMap<>();

            // read header
            String filename = null;
            String name = null;
            ContentType contentType = ContentType.TEXT_PLAIN;

            byte[][] splitMultipart = splitByte(multipartData, "\r\n\r\n".getBytes());

            byte[][] headers = splitByte(splitMultipart[0], "\r\n".getBytes());
            byte[] contentBody = splitMultipart[1];

            int index = 0;
            // header
            for (; index < headers.length; index++) {
                byte[] header = headers[index];
                try (BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(header)))) {
                    String line = null;
                    while ((line = br.readLine()) != null) {

                        if (line.startsWith("Content-Disposition:")) {
                            String[] tokens = line.substring(20).split("; ");
                            for (String token : tokens) {
                                if (token.startsWith("name=")) {
                                    name = token.substring(5).replace("\"", "");
                                } else if (token.startsWith("filename=")) {
                                    filename = token.substring(9).replace("\"", "");
                                }
                            }
                        } else if (line.startsWith("Content-Type:")) {
                            contentType = ContentType.of(line.substring(13).replace(" ", ""));
                        } else {
                            String[] headerInfo = line.split(": ");
                            String key = headerInfo[0];
                            String value = headerInfo[1];
                            multipartHeaders.put(key, value);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            MultipartContent multipartRequest = new MultipartContent(filename, name, contentType, contentBody, Map.copyOf(multipartHeaders));
            multipartRequests.add(multipartRequest);
        }
        return multipartRequests;
    }

    /**
     * split multipart data and remove boundary data
     *
     * @param body
     * @param boundary
     * @return
     */
    public static byte[][] splitByte(byte[] body, byte[] boundary) {

        List<byte[]> result = new ArrayList<>();
        List<Byte> content = new ArrayList<>();

        for (int s = 0; s < body.length; ) {
            boolean isMatch = true;
            for (int e = 0; s < body.length && e < boundary.length; e++) {
                content.add(body[s]);
                if (!(boundary[e] == body[s])) {
                    isMatch = false;
                    s++;
                    break;
                }
                s++;
            }
            if (isMatch) {
                if (content.size() == boundary.length) {
                    content.clear();
                } else {
                    List<Byte> listContent = content.subList(0, content.size() - boundary.length);
                    byte[] bytes = toByteArray(listContent);
                    result.add(bytes);
                    content.clear();
                }
            }
        }
        if (!content.isEmpty()) {
            byte[] bytes = toByteArray(content);
            result.add(bytes);
        }
        return result.toArray(new byte[0][]);
    }

    private static byte[] toByteArray(List<Byte> bytes) {
        byte[] result = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            result[i] = bytes.get(i);
        }
        return result;
    }

    public String toString() {
        return "name: " + name + ", filename: " + filename + ", contentType: " + contentType + ", type: " + new String(type) + ", headers: " + headers;
    }
}
