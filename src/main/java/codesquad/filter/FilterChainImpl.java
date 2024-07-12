package codesquad.filter;

import codesquad.config.FilterConfig;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;

public class FilterChainImpl implements FilterChain {

    private final Filter[] filters;
    private final Router router;
    private int index = 0;

    public FilterChainImpl(FilterConfig config) {
        this.filters = config.getFilters();
        this.router = config.getRouter();
    }

    @Override
    public void doFilter(HttpRequest request, HttpResponse response) {
        if (index == filters.length) {
            index++;
            router.handle(request, response, this);
        } else if (index < filters.length) {
            filters[index++].doFilter(request, response, this);
        }
    }
}
