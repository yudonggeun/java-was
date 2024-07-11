package codesquad.filter;

import codesquad.config.RouterConfig;
import codesquad.handler.HttpHandler;
import codesquad.handler.URLMatcher;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;

/**
 * 반드시 마지막에 실행이 되어야하는 필터입니다.
 * 실제 서비스의 로직을 실행하기 위한 진입점으로 사용하는 필터입니다.
 */
public class Router implements Filter {

    private final RouterConfig config;

    public Router(RouterConfig config) {
        this.config = config;
    }

    @Override
    public void doFilter(HttpRequest request, HttpResponse response, FilterChain chain) {

        for (var entry : config.getHandlerMap().entrySet()) {
            URLMatcher urlMatcher = entry.getKey();
            if (urlMatcher.isMatch(request)) {
                HttpHandler httpHandler = entry.getValue();
                response.update(httpHandler.doRun(request));
                return;
            }
        }
        // 핸들러를 찾지 못하는 경우 처리
        response.update(HttpResponse.of(HttpStatus.NOT_FOUND));
        chain.doFilter(request, response);
    }
}
