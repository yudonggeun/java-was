package codesquad.router;

import codesquad.context.SessionContext;
import codesquad.context.SessionContextManager;
import codesquad.http.ContentType;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import codesquad.template.HtmlManager;
import codesquad.template.HtmlRoot;
import codesquad.template.Model;

public class TemplateResourceHandler implements HttpHandler {

    private final HtmlManager htmlManager;
    private final ContentType contentType;
    private final SessionContextManager sessionContextManager;
    private final String html;

    public TemplateResourceHandler(ContentType contentType, String html, HtmlManager htmlManager, SessionContextManager sessionContextManager) {
        this.contentType = contentType;
        this.htmlManager = htmlManager;
        this.html = html;
        this.sessionContextManager = sessionContextManager;
    }

    @Override
    public HttpResponse doRun(HttpRequest request) {
        HttpResponse response = HttpResponse.of(HttpStatus.OK);
        response.setContentType(contentType);
        convertToHtml(request, response);
        return response;
    }

    private void convertToHtml(HttpRequest request, HttpResponse response) {
        HtmlRoot root = htmlManager.create(html);

        SessionContext session = sessionContextManager.getSession(request);

        Model model = new Model();
        model.setSession(session);
        root.applyModel(model);

        response.setBody(root.toHtml().getBytes());
    }

}
