package codesquad.container;

import codesquad.filter.*;
import codesquad.handler.HttpHandler;
import codesquad.handler.LoginHandler;
import codesquad.handler.StaticResourceHandler;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;

import java.util.Set;

public class MyContainer {

    private final Set<HttpHandler> httpHandler = Set.of(
            new StaticResourceHandler(),
            new LoginHandler()
    );

    private final FilterConfig filterConfig = new FilterConfig(
            new HttpLoggingFilter(),
            new CharSetFilter("UTF-8"),
            new AcceptHeaderFilter(),
            new LogicFilter(httpHandler)
    );

    public HttpResponse doRun(HttpRequest request) {

        final FilterChain filterChain = new FilterChainImpl(filterConfig);

        HttpResponse response = HttpResponse.of(HttpStatus.INTERNAL_SERVER_ERROR);
        filterChain.doFilter(request, response);
        return response;
    }
}
