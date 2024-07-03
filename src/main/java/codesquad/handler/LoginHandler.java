package codesquad.handler;

import codesquad.application.domain.User;
import codesquad.application.repository.MyRepository;
import codesquad.http.ContentType;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHandler implements HttpHandler {

    private final MyRepository repository = MyRepository.source;
    private final Logger logger = LoggerFactory.getLogger(LoginHandler.class);

    @Override
    public boolean match(HttpRequest request) {
        return true;
    }

    @Override
    public HttpResponse doRun(HttpRequest request) {
        if (request.path.equals("/registration")) {
            return login(request);
        } else if (request.path.equals("/user/create")) {
            return createUser(request);
        }
        return null;
    }

    // get /registration
    public HttpResponse login(HttpRequest request) {
        HttpResponse response = HttpResponse.of(HttpStatus.MOVED_PERMANENTLY);
        response.addHeader("Location", "/registration/index.html");
        return response;
    }

    public HttpResponse createUser(HttpRequest request) {
        String userId = request.getParam("userId");
        String password = request.getParam("password");
        String nickname = request.getParam("nickname");

        // save user
        User user = new User(userId, password, nickname);
        repository.save(userId, user);

        HttpResponse response = HttpResponse.of(HttpStatus.OK);
        response.setContentType(ContentType.TEXT_HTML);
        response.setBody("""
                <p>
                회원가입 완료!
                </p>
                """);
        logger.debug("User created: {}", user);
        return response;
    }
}
