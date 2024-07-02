package codesquad.filter;

import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;

public class FilterChainImpl implements FilterChain {

    private final Filter[] filters;
    private int index = 0;

    public FilterChainImpl(FilterConfig config) {
        this.filters = config.getFilters();
    }

    @Override
    public void doFilter(HttpRequest request, HttpResponse response) {
        if (index == filters.length) return;
        filters[index++].doFilter(request, response, this);
    }
}
