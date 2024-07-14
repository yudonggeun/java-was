package codesquad.filter;

import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.util.scan.Solo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTTP 요청과 응답에 대한 로깅을 수행하는 필터입니다.
 */
@Solo
public class HttpLoggingFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(HttpLoggingFilter.class);

    @Override
    public void preHandle(HttpRequest request) {
        logger.info("Request[version={} method={}, host={}, path={}, headers={}, body={}], params={}",
                request.version,
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
        logger.info("Response[status={}, headers={}]", response.getStatus(), response.getHeaderString());
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
