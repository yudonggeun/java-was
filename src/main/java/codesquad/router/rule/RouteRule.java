package codesquad.router.rule;

import codesquad.http.HttpRequest;

public interface RouteRule {

    boolean isSatisfied(HttpRequest request);
}
