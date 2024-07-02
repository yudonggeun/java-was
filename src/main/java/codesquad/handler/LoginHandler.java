package codesquad.handler;

import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;

public class LoginHandler implements HttpHandler {

    @Override
    public boolean match(HttpRequest request) {
        return true;
    }

    @Override
    public HttpResponse doRun(HttpRequest request) {
        if (request.path.equals("/registration")) {
            return login(request);
        }
        return null;
    }

    // get /registration
    public HttpResponse login(HttpRequest request) {
        HttpResponse response = HttpResponse.of(HttpStatus.MOVED_PERMANENTLY);
        response.addHeader("Location", "/registration/index.html");
        return response;
    }
}
