package codesquad.router.handler;

import codesquad.http.ContentType;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;

public class StaticResourceHandler implements HttpHandler {

    private final byte[] file;
    private final ContentType contentType;

    public StaticResourceHandler(ContentType contentType, byte[] file) {
        this.contentType = contentType;
        this.file = file;
    }

    @Override
    public HttpResponse doRun(HttpRequest request) {
        HttpResponse response = HttpResponse.of(HttpStatus.OK);
        response.setContentType(contentType);
        response.setBody(file);
        return response;
    }
}
