package codesquad.router.handler;

import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;

@FunctionalInterface
public interface HttpHandler {

    /**
     * handler가 처리할 수 있는 요청을 처리합니다.
     *
     * @param request 클라이언트로부터 받은 요청
     */
    HttpResponse doRun(HttpRequest request);
}
