package codesquad.filter;

import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTTP 요청과 응답에 대한 로깅을 수행하는 필터입니다.
 */
public class HttpLoggingFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(HttpLoggingFilter.class);

    @Override
    public void preHandle(HttpRequest request) {
        logger.info("Request[method={}, host={}, path={}, headers={}, body={}], params={}",
                request.method,
                request.getHeader("Host"),
                request.path,
                request.getHeaders(),
                request.getBody(),
                request.getParams()
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
