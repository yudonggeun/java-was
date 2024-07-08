package codesquad.handler;

import codesquad.application.domain.User;
import codesquad.application.repository.MyRepository;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Function;

public class LoginHandler implements HttpHandler {

    private final MyRepository repository = MyRepository.source;
    private final Logger logger = LoggerFactory.getLogger(LoginHandler.class);
    private final Map<String, Function<HttpRequest, HttpResponse>> handlers = Map.of(
            "/registration", this::login,
            "/user/create", this::createUser,
            "/login", this::loginPage
    );

    @Override
    public boolean match(HttpRequest request) {
        return handlers.containsKey(request.path);
    }

    @Override
    public HttpResponse doRun(HttpRequest request) {
        if (!handlers.containsKey(request.path)) return null;
        return handlers.get(request.path).apply(request);
    }

    // get /registration
    public HttpResponse login(HttpRequest request) {
        HttpResponse response = HttpResponse.of(HttpStatus.MOVED_PERMANENTLY);
        response.addHeader("Location", "/registration/index.html");
        return response;
    }

    public HttpResponse createUser(HttpRequest request) {

        HttpResponse response;
        if (!(
                request.method.equals("POST") &&
                request.getHeader("Content-Type").contains("application/x-www-form-urlencoded")
        )) {
            response = HttpResponse.of(HttpStatus.BAD_REQUEST);
            return response;
        }

        String userId = readSingleBodyParam(request, "userId");
        String password = readSingleBodyParam(request, "password");
        String nickname = readSingleBodyParam(request, "nickname");

        // save user
        User user = new User(userId, password, nickname);
        repository.save(userId, user);

        response = HttpResponse.of(HttpStatus.SEE_OTHER);
        response.addHeader("Location", "/index.html");

        logger.debug("User created: {}", user);
        return response;
    }

    private String readSingleBodyParam(HttpRequest request, String attr) {
        Object bodyParam = request.getBodyParam(attr);
        if (bodyParam instanceof String) return (String) bodyParam;
        return null;
    }

    private HttpResponse loginPage(HttpRequest request) {
        HttpResponse response = HttpResponse.of(HttpStatus.MOVED_PERMANENTLY);
        response.addHeader("Location", "/login/index.html");
        return response;
    }
}
