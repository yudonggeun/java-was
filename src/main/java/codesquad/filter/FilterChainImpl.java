package codesquad.filter;

import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;

public class FilterChainImpl implements FilterChain {

    private final Filter[] filters;
    private final Filter hanlderFilter;
    private int index = 0;

    public FilterChainImpl(FilterConfig config) {
        this.filters = config.getFilters();
        this.hanlderFilter = config.getHandlerFilter();
    }

    @Override
    public void doFilter(HttpRequest request, HttpResponse response) {
        if (index == filters.length) {
            hanlderFilter.doFilter(request, response, this);
        } else if (index < filters.length) {
            filters[index].doFilter(request, response, this);
        }
        index++;
    }
}
