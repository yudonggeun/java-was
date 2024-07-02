package codesquad.handler;

import codesquad.filter.Filter;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;

import java.net.Socket;
import java.util.SortedSet;
import java.util.TreeSet;

public class HttpDefaultHandler implements HttpHandler {

    private final Socket clientSocket;
    private final SortedSet<Filter> filters = new TreeSet<>();

    public HttpDefaultHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void addFilter(Filter filter) {
        filters.add(filter);
    }

    @Override
    public HttpResponse doRun(HttpRequest request) {
        return null;
    }
}
