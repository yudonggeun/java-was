package codesquad.router;

import codesquad.config.RouterConfig;
import codesquad.context.SessionContextManager;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import codesquad.template.HtmlManager;
import codesquad.util.scan.Solo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static codesquad.router.RouteTableRow.get;

@Solo
public class WriteTable {


    private final Logger logger = LoggerFactory.getLogger(WriteTable.class);
    private final HtmlManager htmlManager;
    private final SessionContextManager sessionContextManager;

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
            })
    );

    public WriteTable(RouterConfig config, HtmlManager htmlManager, SessionContextManager sessionContextManager) {
        this.htmlManager = htmlManager;
        this.sessionContextManager = sessionContextManager;
        config.addRouteTable(table);
    }

    private String readSingleBodyParam(HttpRequest request, String attr) {
        Object bodyParam = request.getBodyParam(attr);
        if (bodyParam instanceof String) return (String) bodyParam;
        return null;
    }

    private SessionContextManager sessionContextManager() {
        return sessionContextManager;
    }
}
