package codesquad.handler;

import codesquad.http.ContentType;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;

public class StaticResourceHandler implements HttpHandler {

    private final byte[] file;
    private final ContentType contentType;

    public StaticResourceHandler(String filename, byte[] file) {
        this.contentType = getContentType(filename);
        this.file = file;
    }

    @Override
    public HttpResponse doRun(HttpRequest request) {
        HttpResponse response = HttpResponse.of(HttpStatus.OK);
        response.setContentType(contentType);
        response.setBody(file);
        return response;
    }

    private ContentType getContentType(String fileName) {
        // extract file extension
        int dotIndex = fileName.lastIndexOf('.');
        String extension = fileName.substring(dotIndex + 1);

        // content type
        return switch (dotIndex) {
            case -1 -> ContentType.TEXT_PLAIN;
            default -> ContentType.of(extension);
        };
    }
}
