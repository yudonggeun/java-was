package codesquad.router;

import codesquad.application.domain.Article;
import codesquad.application.repository.ArticleRepository;
import codesquad.application.repository.MockArticleRepository;
import codesquad.config.RouterConfig;
import codesquad.context.SessionContext;
import codesquad.context.SessionContextManager;
import codesquad.http.ContentType;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import codesquad.template.HtmlManager;
import codesquad.template.HtmlRoot;
import codesquad.template.Model;
import codesquad.util.file.ResourceFileManager;
import codesquad.util.scan.Solo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static codesquad.router.RouteTableRow.get;
import static codesquad.router.RouteTableRow.post;

@Solo
public class ArticleTable {

    private final Logger logger = LoggerFactory.getLogger(ArticleTable.class);
    private final HtmlManager htmlManager;
    private final SessionContextManager sessionContextManager;
    private final ArticleRepository articleRepository;

    private final List<RouteTableRow> table = List.of(
            get("/article").handle(request -> {
                if (sessionContextManager().getSession(request) == null) {
                    HttpResponse response = HttpResponse.of(HttpStatus.SEE_OTHER);
                    response.addHeader("Location", "/login/index.html");
                    return response;
                }
                HttpResponse response = HttpResponse.of(HttpStatus.SEE_OTHER);
                response.addHeader("Location", "/article/index.html");
                return response;
            }),

            post("/article").handle(request -> {
                if (sessionContextManager().getSession(request) == null) {
                    HttpResponse response = HttpResponse.of(HttpStatus.SEE_OTHER);
                    response.addHeader("Location", "/login/index.html");
                    return response;
                }

                HttpResponse response = HttpResponse.of(HttpStatus.SEE_OTHER);
                return response;
            }),

            get("/index.html").handle(request -> {
                SessionContext session = sessionContextManager().getSession(request);

                try (InputStream inputStream = ResourceFileManager.getInputStream("templates/index.html")) {
                    byte[] file = inputStream.readAllBytes();
                    HtmlRoot root = htmlManager().create(new String(file));

                    // find article
                    Article article = articleRepository().findOne();

                    Model model = new Model();
                    model.setSession(session);
                    model.addAttribute("article", article);

                    root.applyModel(model);

                    HttpResponse response = HttpResponse.of(HttpStatus.OK);
                    response.setContentType(ContentType.TEXT_HTML);
                    response.setBody(root.toHtml().getBytes());
                    return response;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            })
    );

    public ArticleTable(RouterConfig config, HtmlManager htmlManager, SessionContextManager sessionContextManager, MockArticleRepository articleRepository) {
        this.htmlManager = htmlManager;
        this.sessionContextManager = sessionContextManager;
        this.articleRepository = articleRepository;
        config.addRouteTable(table);
    }

    private String readSingleBodyParam(HttpRequest request, String attr) {
        Object bodyParam = request.getBodyParam(attr);
        if (bodyParam instanceof String) return (String) bodyParam;
        return null;
    }

    private ArticleRepository articleRepository() {
        return articleRepository;
    }

    private SessionContextManager sessionContextManager() {
        return sessionContextManager;
    }

    private HtmlManager htmlManager() {
        return htmlManager;
    }
}
