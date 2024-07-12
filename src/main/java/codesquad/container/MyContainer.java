package codesquad.container;

import codesquad.config.FilterConfig;
import codesquad.filter.FilterChain;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;

public class MyContainer {

    private final FilterConfig config;

    public MyContainer(FilterConfig config) {
        this.config = config;
    }

    public HttpResponse doRun(HttpRequest request) {

        final FilterChain filterChain = new FilterChain(config);

        HttpResponse response = HttpResponse.of(HttpStatus.INTERNAL_SERVER_ERROR);
        filterChain.doFilter(request, response);
        return response;
    }
}
