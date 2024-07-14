package codesquad.router.rule;

import codesquad.http.ContentType;
import codesquad.http.HttpRequest;

public enum RouteRules implements RouteRule {

    MULTIPART_REQUEST_RULE(request -> {
        String contentType = request.getHeader("Content-Type");
        return contentType.contains(ContentType.MULTIPART_FILE.fullType);
    });

    private final RouteRule routeRule;

    RouteRules(RouteRule routeRule) {
        this.routeRule = routeRule;
    }

    @Override
    public boolean isSatisfied(HttpRequest request) {
        return routeRule.isSatisfied(request);
    }
}
