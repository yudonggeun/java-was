package codesquad.container;

import codesquad.context.ApplicationContext;
import codesquad.filter.FilterChain;
import codesquad.filter.FilterChainImpl;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;

public class MyContainer {

    private final ApplicationContext context = ApplicationContext.context;

    public HttpResponse doRun(HttpRequest request) {

        final FilterChain filterChain = new FilterChainImpl(context.getFilterConfig());

        HttpResponse response = HttpResponse.of(HttpStatus.INTERNAL_SERVER_ERROR);
        filterChain.doFilter(request, response);
        return response;
    }
}
