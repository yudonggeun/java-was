package codesquad.handler;

import codesquad.application.domain.User;
import codesquad.application.repository.MyRepository;
import codesquad.context.SessionContext;
import codesquad.context.SessionContextManager;
import codesquad.http.*;
import codesquad.template.HtmlElement;
import codesquad.template.HtmlManager;
import codesquad.template.HtmlRoot;
import codesquad.template.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static codesquad.http.Method.POST;

public class LoginHandler implements HttpHandler {

    private final MyRepository repository = MyRepository.source;
    private final Logger logger = LoggerFactory.getLogger(LoginHandler.class);
    private final HtmlManager htmlManager = new HtmlManager();
    private final Map<String, Function<HttpRequest, HttpResponse>> handlers = Map.of(
            "/registration", this::registrationPage,
            "/user/create", this::createUser,
            "/login", this::loginPage,
            "/logout", this::logout,
            "/signin", this::login,
            "/user/list", this::getUserList
    );

    private HttpResponse logout(HttpRequest request) {
        SessionContext session = SessionContextManager.getSession(request);
        if (session != null) {
            SessionContextManager.clearContext(request);
        }
        HttpResponse response = HttpResponse.of(HttpStatus.SEE_OTHER);
        response.addHeader("Location", "/index.html");
        response.addHeader("Set-Cookie", "SID=; Max-Age=0");
        return response;
    }

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
    public HttpResponse registrationPage(HttpRequest request) {
        HttpResponse response = HttpResponse.of(HttpStatus.MOVED_PERMANENTLY);
        response.addHeader("Location", "/registration/index.html");
        return response;
    }

    public HttpResponse createUser(HttpRequest request) {

        HttpResponse response;
        if (!(
                request.method.equals(POST) &&
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

    private HttpResponse login(HttpRequest request) {

        HttpResponse response;
        if (!(
                request.method.equals(Method.POST) &&
                request.getHeader("Content-Type").contains("application/x-www-form-urlencoded")
        )) {
            response = HttpResponse.of(HttpStatus.BAD_REQUEST);
            return response;
        }

        String userId = readSingleBodyParam(request, "userId");
        String password = readSingleBodyParam(request, "password");

        User user = repository.findUser(userId);
        if (user != null && user.getPassword().equals(password)) {

            String sid = SessionContextManager.createContext();
            SessionContext context = SessionContextManager.getContext(sid);
            context.setAttributes("user", user);

            response = HttpResponse.of(HttpStatus.SEE_OTHER);
            response.addHeader("Location", "/index.html");
            response.addHeader("Set-Cookie", "SID=" + sid);
        } else {
            response = HttpResponse.of(HttpStatus.SEE_OTHER);
            response.addHeader("Location", "/login/fail.html");
        }

        return response;
    }

    private HttpResponse getUserList(HttpRequest request) {
        try (InputStream input = this.getClass().getResourceAsStream("/templates/user/list.html")) {

            HttpResponse response;

            String html = new String(input.readAllBytes());
            HtmlRoot root = htmlManager.create(html);

            SessionContext session = SessionContextManager.getSession(request);
            if (session == null) {
                response = HttpResponse.of(HttpStatus.FOUND);
                response.addHeader("Location", "/login/index.html");
                return response;
            }

            Model model = new Model();
            model.setSession(session);

            List<User> users = repository.findAllUser();

            HtmlElement userTableElement = root.findById("user-table");

            for (User user : users) {
                userTableElement.addChild(HtmlElement.create("<tr class=\"myclass\">")
                        .addChildren(
                                HtmlElement.create("<td>").addChildren(HtmlElement.create(user.getUserId())),
                                HtmlElement.create("<td>").addChildren(HtmlElement.create(user.getNickname()))
                        )
                        .setClose()
                        .build()
                );
            }

            root.applyModel(model);

            response = HttpResponse.of(HttpStatus.OK);
            response.setContentType(ContentType.TEXT_HTML);
            response.setBody(root.toHtml().getBytes());

            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
