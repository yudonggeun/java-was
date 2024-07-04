package codesquad.filter;

import codesquad.handler.HttpHandler;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;

import java.util.Optional;
import java.util.Set;

/**
 * 반드시 마지막에 실행이 되어야하는 필터입니다.
 * 실제 서비스의 로직을 실행하기 위한 진입점으로 사용하는 필터입니다.
 */
public class LogicFilter implements Filter {

    private final Set<HttpHandler> httpHandlers;

    public LogicFilter(Set<HttpHandler> httpHandlers) {
        this.httpHandlers = httpHandlers;
    }

    /**
     * LogicFilter는 가장 마지막에 수행되어야하기 때문에 가장 낮은 우선순위를 가집니다.
     *
     * @return 가장 낮은 우선순위 Integer.MAX_VALUE
     */
    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void doFilter(HttpRequest request, HttpResponse response, FilterChain chain) {

        Optional<HttpResponse> result = httpHandlers.stream()
                .filter(httpHandler -> httpHandler.match(request))
                .findFirst()
                .map(httpHandler -> httpHandler.doRun(request));

        if (result.isPresent()) {
            response.update(result.get());
        } else {
            response.update(HttpResponse.of(HttpStatus.NOT_FOUND));
        }
        chain.doFilter(request, response);
    }
}
