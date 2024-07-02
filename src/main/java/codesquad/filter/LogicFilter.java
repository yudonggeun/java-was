package codesquad.filter;

import codesquad.handler.HttpHandler;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;

import java.util.Set;

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
        for (HttpHandler httpHandler : httpHandlers) {
            if (httpHandler.match(request)) {
                response.update(httpHandler.doRun(request));
                break;
            }
        }
        chain.doFilter(request, response);
    }
}
