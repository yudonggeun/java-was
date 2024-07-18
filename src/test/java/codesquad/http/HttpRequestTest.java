package codesquad.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class HttpRequestTest {

    @Test
    @DisplayName("HttpRequest 객체 생성 테스트")
    public void readInputStream() {
        // given
        String httpRequestMessage = """
                GET / HTTP/1.1\r
                Host: localhost:8080\r
                Connection: keep-alive\r
                Content-Type: text/plain\r
                Content-Length: 12\r
                Accept: text/html\r
                \r
                Hello World!""";

        InputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());

        // when
        HttpRequest httpRequest = new HttpRequest(inputStream);

        // then
        assertAll(
                () -> assertThat(httpRequest.method).isEqualTo(Method.GET),
                () -> assertThat(httpRequest.path).isEqualTo("/"),
                () -> assertThat(httpRequest.version).isEqualTo("HTTP/1.1"),
                () -> assertThat(httpRequest.getHeader("Host")).isEqualTo("localhost:8080"),
                () -> assertThat(httpRequest.getHeader("Connection")).isEqualTo("keep-alive"),
                () -> assertThat(httpRequest.getHeader("Content-Length")).isEqualTo("12"),
                () -> assertThat(httpRequest.getHeader("Accept")).isEqualTo("text/html"),
                () -> assertThat(httpRequest.getBody()).isEqualTo("Hello World!")
        );
    }
}