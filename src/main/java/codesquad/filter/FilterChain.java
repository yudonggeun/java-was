package codesquad.filter;

import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;

/**
 * 필터 체인은 필터의 실행 순서를 제어하는 책임을 가지고 있습니다.
 */
public interface FilterChain {

    /**
     * 필터 체인에서 관리되는 필터를 정해진 순서에 따라서 실행하는 메서드입니다.
     *
     * @param request  HTTP 요청
     * @param response HTTP 응답
     */
    void doFilter(HttpRequest request, HttpResponse response);
}
