package codesquad.http;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MultipartContentTest {

    @Test
    void parseMultipart() {
        String input =
                "----\r\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"test.txt\"\r\n" +
                "\r\n" +
                "hello world\r\n" +
                "----\r\n" +
                "Content-Disposition: form-data; name=\"hello\"; filename=\"hello.txt\"\r\n" +
                "test: test\r\n" +
                "hello: hello world\r\n" +
                "Content-Type: application/json\r\n" +
                "\r\n" +
                "hello world\r\n" +
                "----\r\n--";

        List<MultipartContent> parse = MultipartContent.parse("--", input.getBytes());
        for (MultipartContent multipartRequest : parse) {
            System.out.println(multipartRequest);
        }

        assertThat(parse.get(0)).matches(multipartRequest -> {
            assertThat(multipartRequest.filename).isEqualTo("test.txt");
            assertThat(multipartRequest.name).isEqualTo("file");
            assertThat(multipartRequest.contentType).isEqualTo(ContentType.TEXT_PLAIN);
            assertThat(multipartRequest.content).isEqualTo("hello world".getBytes());
            assertThat(multipartRequest.headers).isEmpty();
            return true;
        });

        assertThat(parse.get(1)).matches(multipartRequest -> {
            assertThat(multipartRequest.filename).isEqualTo("hello.txt");
            assertThat(multipartRequest.name).isEqualTo("hello");
            assertThat(multipartRequest.contentType).isEqualTo(ContentType.APPLICATION_JSON);
            assertThat(multipartRequest.content).isEqualTo("hello world".getBytes());
            assertThat(multipartRequest.headers.get("hello")).isEqualTo("hello world");
            assertThat(multipartRequest.headers.get("test")).isEqualTo("test");
            return true;
        });
    }

}