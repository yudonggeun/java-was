package codesquad.filter;

import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;

public interface Filter extends Comparable<Filter> {

    /**
     * 필터의 적용 우선순위를 반환합니다.
     * 필터의 우선 순위가 없다면 기본 우선순위 값은 1000 입니다.
     *
     * @return 0 ~ Integer.MAX_VALUE 사이의 값
     */
    default int getOrder() {
        return 1000;
    }

    @Override
    default int compareTo(Filter filter) {
        return Integer.compare(this.getOrder(), filter.getOrder());
    }

    default void doFilter(HttpRequest request, HttpResponse response, FilterChain chain) {
        preHandle(request);
        chain.doFilter(request, response);
        postHandle(request, response);
    }

    default void preHandle(HttpRequest request) {
    }

    default void postHandle(HttpRequest request, HttpResponse response) {
    }
}