package codesquad.handler;

import codesquad.context.SessionContext;
import codesquad.context.SessionContextManager;
import codesquad.http.ContentType;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import codesquad.template.HtmlManager;
import codesquad.template.HtmlRoot;
import codesquad.template.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class TemplateResourceHandler implements HttpHandler {

    private final Logger logger = LoggerFactory.getLogger(StaticResourceHandler.class);
    private final HtmlManager htmlManager;
    private final ContentType contentType;
    private final byte[] file;

    public TemplateResourceHandler(ContentType contentType, byte[] file, HtmlManager htmlManager) {
        this.contentType = contentType;
        this.file = file;
        this.htmlManager = htmlManager;
    }

    @Override
    public HttpResponse doRun(HttpRequest request) {
        HttpResponse response = HttpResponse.of(HttpStatus.OK);
        response.setContentType(contentType);
        response.setBody(file);
        convertToHtml(request.path, request, response);
        return response;
    }

    private void convertToHtml(String path, HttpRequest request, HttpResponse response) {
        try (InputStream in = this.getClass().getResourceAsStream("/templates" + path)) {
            String template = new String(in.readAllBytes());

            HtmlRoot root = htmlManager.create(template);

            SessionContext session = SessionContextManager.getSession(request);

            Model model = new Model();
            model.setSession(session);
            root.applyModel(model);

            response.setBody(root.toHtml().getBytes());
        } catch (IOException e) {
            logger.error("Error reading from binary file: {}", path);
            throw new RuntimeException(e);
        }
    }

}
