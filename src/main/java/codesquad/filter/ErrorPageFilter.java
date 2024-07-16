package codesquad.filter;

import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import codesquad.router.handler.HttpHandler;
import codesquad.router.table.ErrorPageTable;
import codesquad.util.scan.Solo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Solo
public class ErrorPageFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(ErrorPageFilter.class);
    private final ErrorPageTable errorPageTable;

    public ErrorPageFilter(ErrorPageTable errorPageTable) {
        this.errorPageTable = errorPageTable;
    }

    @Override
    public void postHandle(HttpRequest request, HttpResponse response) {
        HttpStatus status = response.getStatus();
        HttpHandler handler = errorPageTable.findHandler(status);

        if (handler != null) {
            logger.info("render error page : status={} path={}", status, request.path);
            response.update(handler.doRun(request));
        }
    }
}
