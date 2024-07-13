package codesquad.router;

import codesquad.config.RouterConfig;
import codesquad.filter.FilterChain;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import codesquad.util.scan.Solo;

import java.util.Optional;

/**
 * 요청에 대당하는 핸들러를 호출하여 실행하는 진입점입니다.
 * @see FilterChain 를 통해 요청에 대한 필터 체인을 실행합니다.
 */
@Solo
public class Router {

    private final RouterConfig config;

    public Router(RouterConfig config) {
        this.config = config;
    }

    public void handle(HttpRequest request, HttpResponse response, FilterChain chain) {

        Optional<HttpHandler> handler = config.findHandler(request);
        if (handler.isPresent()) {
            response.update(handler.get().doRun(request));
        } else {
            // 핸들러를 찾지 못하는 경우 처리
            response.update(HttpResponse.of(HttpStatus.NOT_FOUND));
            chain.doFilter(request, response);
        }
    }
}
