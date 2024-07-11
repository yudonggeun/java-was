package codesquad.handler;

public class HandlerBuilder {

    private String urlTemplate;
    private HttpHandler logic;

    private HandlerBuilder() {
    }

    public static HandlerBuilder url(String urlTemplate) {
        HandlerBuilder handlerBuilder = new HandlerBuilder();
        handlerBuilder.urlTemplate = urlTemplate;
        return handlerBuilder;
    }

    public String url() {
        return urlTemplate;
    }

    public HttpHandler handler() {
        return logic;
    }

    public HandlerBuilder logic(HttpHandler handler) {
        this.logic = handler;
        return this;
    }
}
