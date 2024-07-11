package codesquad.handler;

import codesquad.http.Method;

public class HandlerBuilder {

    private Method[] methods;
    private String urlTemplate;
    private HttpHandler logic;

    private HandlerBuilder() {
    }

    public static HandlerBuilder method(Method... methods) {
        HandlerBuilder handlerBuilder = new HandlerBuilder();
        handlerBuilder.methods = methods;
        return handlerBuilder;
    }

    public static HandlerBuilder get(String urlTemplate) {
        return method(Method.GET).url(urlTemplate);
    }

    public static HandlerBuilder post(String urlTemplate) {
        return method(Method.POST).url(urlTemplate);
    }

    public RouteEntry logic(HttpHandler handler) {
        this.logic = handler;
        return new RouteEntry(URLMatcher.method(methods).urlTemplate(urlTemplate).build(), logic);
    }

    public HandlerBuilder url(String urlTemplate) {
        this.urlTemplate = urlTemplate;
        return this;
    }
}
