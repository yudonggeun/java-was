package codesquad.filter;

import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;

/**
 * <p>
 * 필터는 HTTP 요청과 응답에 대한 사전&사후 처리를 담당하는 인터페이스입니다. <br>
 * 필터의 적용 순서는 정의된 우선순위에 따라 결정됩니다. 높은 우선순위를 가지는 필터의 사전 처리는 먼저 수행되고, 사후 처리는 나중에 수행됩니다. <br>
 * 우선순위는 0 ~ Integer.MAX_VALUE 사이의 값으로 설정할 수 있으며 낮은 숫자가 더 높은 우선 순위를 뜻합니다.<br>
 * </p>
 *
 * @author yudonggeun
 */
public interface Filter extends Comparable<Filter> {

    /**
     * <p>
     * 필터의 적용 우선순위를 반환합니다.
     * 필터의 우선 순위가 없다면 기본 우선순위 값은 1000 입니다.
     * </p>
     *
     * @return 0 ~ Integer.MAX_VALUE 사이의 값
     */
    default int getOrder() {
        return 1000;
    }

    default void doFilter(HttpRequest request, HttpResponse response, FilterChain chain) {
        preHandle(request);
        chain.doFilter(request, response);
        postHandle(request, response);
    }

    /**
     * 필터의 사전 처리 로직을 구현하는 메서드입니다.
     *
     * @param request HTTP 요청
     */
    default void preHandle(HttpRequest request) {
    }

    /**
     * 필터의 사후 처리 로직을 구현하는 메서드입니다.
     * @param request HTTP 요청
     * @param response HTTP 응답
     */
    default void postHandle(HttpRequest request, HttpResponse response) {
    }

    @Override
    default int compareTo(Filter filter) {
        return Integer.compare(this.getOrder(), filter.getOrder());
    }
}