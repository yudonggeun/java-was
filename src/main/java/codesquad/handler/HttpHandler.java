package codesquad.handler;

import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;

public interface HttpHandler {

    /**
     * handler가 처리할 수 있는 요청을 처리합니다.
     *
     * @param request 클라이언트로부터 받은 요청
     */
    HttpResponse doRun(HttpRequest request);

    /**
     * handler가 처리할 수 있는 요청인지를 검사합니다.
     *
     * @param request 클라이언트로부터 받은 요청
     * @return 요청을 처리할 수 있는지 여부
     */
    default boolean match(HttpRequest request) {
        return false;
    }
}
