package codesquad.handler;

import codesquad.http.ContentType;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class StaticResourceHandler implements HttpHandler {

    private final Logger logger = LoggerFactory.getLogger(StaticResourceHandler.class);

    @Override
    public boolean match(HttpRequest request) {
        URL resource = this.getClass().getResource("/static" + request.path);
        return resource != null && resource.getPath().contains(".");
    }

    @Override
    public HttpResponse doRun(HttpRequest request) {
        HttpResponse response = HttpResponse.of(HttpStatus.OK);
        ContentType contentType = getContentType(request.path);
        response.addHeader("Content-Type", contentType.fullType);
        writeFileToBody(request.path, response);
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

    private void writeFileToBody(String path, HttpResponse response) {

        try (InputStream in = this.getClass().getResourceAsStream("/static" + path)) {
            byte[] fileContentBytes = in.readAllBytes();
            response.setBody(fileContentBytes);
        } catch (FileNotFoundException e) {
            logger.error("File not found: {}", path);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("Error reading from binary file: {}", path);
            throw new RuntimeException(e);
        }
    }
}
