package codesquad.router.table;

import codesquad.http.ContentType;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import codesquad.router.handler.HttpHandler;
import codesquad.util.file.ResourceFileManager;
import codesquad.util.scan.Solo;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static codesquad.http.HttpStatus.*;

@Solo
public class ErrorPageTable {

    private final Map<HttpStatus, byte[]> htmls = new HashMap<>();
    private final Map<HttpStatus, HttpHandler> table;

    public ErrorPageTable() {
        readErrorPage();
        table = table();
    }

    public HttpHandler findHandler(HttpStatus status) {
        return table.get(status);
    }

    private Map<HttpStatus, HttpHandler> table() {
        return Map.of(
                NOT_FOUND,
                request -> {
                    HttpResponse response = HttpResponse.of(NOT_FOUND);
                    response.setContentType(ContentType.TEXT_HTML);
                    response.setBody(htmls.get(NOT_FOUND));
                    return response;
                },

                METHOD_NOT_ALLOWED,
                request -> {
                    HttpResponse response = HttpResponse.of(METHOD_NOT_ALLOWED);
                    response.setContentType(ContentType.TEXT_HTML);
                    response.setBody(htmls.get(METHOD_NOT_ALLOWED));
                    return response;
                },

                INTERNAL_SERVER_ERROR,
                request -> {
                    HttpResponse response = HttpResponse.of(INTERNAL_SERVER_ERROR);
                    response.setContentType(ContentType.TEXT_HTML);
                    response.setBody(htmls.get(INTERNAL_SERVER_ERROR));
                    return response;
                },

                SERVICE_NOT_AVAILABLE,
                request -> {
                    HttpResponse response = HttpResponse.of(SERVICE_NOT_AVAILABLE);
                    response.setContentType(ContentType.TEXT_HTML);
                    response.setBody(htmls.get(SERVICE_NOT_AVAILABLE));
                    return response;
                }
        );
    }

    private void readErrorPage() {
        Map<HttpStatus, String> urls = Map.of(
                NOT_FOUND, "static/error/404.html",
                METHOD_NOT_ALLOWED, "static/error/405.html",
                INTERNAL_SERVER_ERROR, "static/error/500.html",
                SERVICE_NOT_AVAILABLE, "static/error/503.html"
        );

        for (var entry : urls.entrySet()) {
            HttpStatus key = entry.getKey();
            String url = entry.getValue();
            try (InputStream input = ResourceFileManager.getInputStream(url)) {
                htmls.put(key, input.readAllBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
