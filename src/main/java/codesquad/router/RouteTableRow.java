package codesquad.router;

import codesquad.http.Method;

public class RouteTableRow {

    private final Method[] methods;
    private final String urlTemplate;
    private final HttpHandler handler;

    private RouteTableRow(Method[] methods, String urlTemplate, HttpHandler handler) {
        this.methods = methods;
        this.urlTemplate = urlTemplate;
        this.handler = handler;
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
     * Builder class
     */
    public static class Builder {

        private final Method[] methods;
        private String urlTemplate;

        private Builder(Method... methods) {
            this.methods = methods;
        }

        public Builder url(String urlTemplate) {
            this.urlTemplate = urlTemplate;
            return this;
        }

        public RouteTableRow handle(HttpHandler handler) {
            return new RouteTableRow(methods, urlTemplate, handler);
        }
    }

}
