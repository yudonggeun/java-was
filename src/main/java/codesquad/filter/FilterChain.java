package codesquad.filter;

import codesquad.config.FilterConfig;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.router.Router;

/**
 * 필터 체인은 필터의 실행 순서를 제어하는 책임을 가지고 있습니다.
 */
public class FilterChain {

    private final Filter[] filters;
    private final Router router;
    private int index = 0;

    public FilterChain(FilterConfig config) {
        this.filters = config.getFilters();
        this.router = config.getRouter();
    }

    /**
     * 필터 체인에서 관리되는 필터를 정해진 순서에 따라서 실행하는 메서드입니다.
     *
     * @param request  HTTP 요청
     * @param response HTTP 응답
     */
    public void doFilter(HttpRequest request, HttpResponse response) {
        if (index == filters.length) {
            index++;
            router.handle(request, response, this);
        } else if (index < filters.length) {
            filters[index++].doFilter(request, response, this);
        }
    }
}
