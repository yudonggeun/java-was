package codesquad.router;

import codesquad.http.HttpRequest;
import codesquad.http.Method;
import codesquad.router.handler.HttpHandler;
import codesquad.router.rule.RouteRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RouteTableRow {

    private final Method[] methods;
    private final String urlTemplate;
    private final HttpHandler handler;
    private final List<RouteRule> routeRules;

    private RouteTableRow(Method[] methods, String urlTemplate, HttpHandler handler, List<RouteRule> routeRules) {
        this.methods = methods;
        this.urlTemplate = urlTemplate;
        this.handler = handler;
        this.routeRules = routeRules;
    }

    /*
     * Builder method
     */
    public static Builder method(Method... methods) {
        return new Builder(methods);
    }

    public static Builder get(String urlTemplate) {
        return new Builder(Method.GET).url(urlTemplate);
    }

    public static Builder post(String urlTemplate) {
        return new Builder(Method.POST).url(urlTemplate);
    }

    public static Builder put(String urlTemplate) {
        return new Builder(Method.PUT).url(urlTemplate);
    }

    public static Builder delete(String urlTemplate) {
        return new Builder(Method.DELETE).url(urlTemplate);
    }

    /*
     * Getter methods
     */

    public String getUrlTemplate() {
        return urlTemplate;
    }

    public Method[] getMethods() {
        return methods;
    }

    public HttpHandler getHandler() {
        return handler;
    }

    /*
     * logic method
     */
    public boolean isMatch(HttpRequest request) {
        for (RouteRule rule : routeRules) {
            if (!rule.isSatisfied(request)) {
                return false;
            }
        }
        return true;
    }

    /*
     * Builder class
     */
    public static class Builder {

        private final Method[] methods;
        private String urlTemplate;
        private final List<RouteRule> routeRules = new ArrayList<>();

        private Builder(Method... methods) {
            this.methods = methods;
        }

        public Builder url(String urlTemplate) {
            this.urlTemplate = urlTemplate;
            return this;
        }

        public Builder rules(RouteRule... rules) {
            Collections.addAll(routeRules, rules);
            return this;
        }

        public RouteTableRow handle(HttpHandler handler) {
            return new RouteTableRow(methods, urlTemplate, handler, routeRules);
        }
    }

}
