package codesquad.filter;

import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpLoggingFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(HttpLoggingFilter.class);

    @Override
    public void preHandle(HttpRequest request) {
        logger.info("Request[method={}, host={}, path={}, headers={}, body={}]",
                request.method,
                request.getHeader("Host"),
                request.path,
                request.getHeaders(),
                request.getBody()
        );
    }

    @Override
    public void postHandle(HttpRequest request, HttpResponse response) {
        logger.info("Response[status={}, headers={}, body={}]", response.getStatus(), response.getHeaderString(), response.getBody());
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
