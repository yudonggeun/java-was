package codesquad.handler;

import codesquad.config.RouterConfig;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Map;

class TemplateResourceHandlerTest {

    @Test
    void splitTags() {
//        TemplateResourceHandler handler = new TemplateResourceHandler();

        String input = """
                <body>
                    <h1 class="test" name="h1" action="test">Hello, World!</h1>
                    <br>
                    <div>
                        <p>Hi</p>
                        <div>test</div>
                        sample
                    <br>
                    </div>
                </body>
                """;
    }

    @Test
    void readStaticResource() {
        var input = new ByteArrayInputStream("""
                GET /login/index.html HTTP/1.1
                Host: localhost:8080
                """.getBytes());
        HttpRequest request = new HttpRequest(input);

        RouterConfig routerConfig = new RouterConfig();

        Map<URLMatcher, HttpHandler> map = routerConfig.staticResourceHandlerMap();
        for (URLMatcher urlMatcher : map.keySet()) {
            if (urlMatcher.isMatch(request)) {
                HttpHandler httpHandler = map.get(urlMatcher);
                HttpResponse response = httpHandler.doRun(request);
                System.out.println(response.getStatus());
                System.out.println(new String(response.getBody()));
            }
        }
    }
}