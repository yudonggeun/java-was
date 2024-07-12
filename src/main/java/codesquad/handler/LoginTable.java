package codesquad.handler;

import codesquad.application.domain.User;
import codesquad.application.repository.MyRepository;
import codesquad.config.RouterConfig;
import codesquad.context.SessionContext;
import codesquad.context.SessionContextManager;
import codesquad.http.*;
import codesquad.template.HtmlElement;
import codesquad.template.HtmlManager;
import codesquad.template.HtmlRoot;
import codesquad.template.Model;
import codesquad.util.scan.Solo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static codesquad.handler.URLMatcher.get;
import static codesquad.handler.URLMatcher.post;
import static codesquad.http.Method.POST;

@Solo
public class LoginTable {

    private final Logger logger = LoggerFactory.getLogger(LoginTable.class);
    private final HtmlManager htmlManager;
    private final SessionContextManager sessionContextManager;

    private final Map<URLMatcher, HttpHandler> table = Map.of(

            get("/").build(),
            request -> {
                HttpResponse response = HttpResponse.of(HttpStatus.MOVED_PERMANENTLY);
                response.addHeader("Location", "/index.html");
                return response;
            },

            get("/registration").build(),
            request -> {
                HttpResponse response = HttpResponse.of(HttpStatus.MOVED_PERMANENTLY);
                response.addHeader("Location", "/registration/index.html");
                return response;
            },

            get("/login").build(),
            request -> {
                HttpResponse response = HttpResponse.of(HttpStatus.MOVED_PERMANENTLY);
                response.addHeader("Location", "/login/index.html");
                return response;
            },

            get("/logout").build(),
            request -> {
                SessionContext session = sessionContextManager().getSession(request);
                if (session != null) {
                    sessionContextManager().clearContext(request);
                }
                HttpResponse response = HttpResponse.of(HttpStatus.SEE_OTHER);
                response.addHeader("Location", "/index.html");
                response.addHeader("Set-Cookie", "SID=; Max-Age=0");
                return response;
            },

            get("/user/list").build(),
            request -> {
                try (InputStream input = this.getClass().getResourceAsStream("/templates/user/list.html")) {

                    HttpResponse response;

                    String html = new String(input.readAllBytes());
                    HtmlRoot root = htmlManager().create(html);

                    SessionContext session = sessionContextManager().getSession(request);
                    if (session == null) {
                        response = HttpResponse.of(HttpStatus.FOUND);
                        response.addHeader("Location", "/login/index.html");
                        return response;
                    }

                    Model model = new Model();
                    model.setSession(session);

                    List<User> users = MyRepository.source.findAllUser();

                    HtmlElement userTableElement = root.findById("user-table");

                    for (User user : users) {
                        userTableElement.addChild(htmlManager().createElement(String.format("""
                                <tr class="myclass">
                                    <td>%s</td>
                                    <td>%s</td>
                                </tr>
                                """, user.getUserId(), user.getNickname())));
                    }

                    root.applyModel(model);

                    response = HttpResponse.of(HttpStatus.OK);
                    response.setContentType(ContentType.TEXT_HTML);
                    response.setBody(root.toHtml().getBytes());

                    return response;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            },

            // post
            post("/user/create").build(),
            request -> {
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
                MyRepository.source.save(userId, user);

                response = HttpResponse.of(HttpStatus.SEE_OTHER);
                response.addHeader("Location", "/index.html");

                logger.debug("User created: {}", user);
                return response;
            },

            post("/signin").build(),
            request -> {
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

                User user = MyRepository.source.findUser(userId);
                if (user != null && user.getPassword().equals(password)) {

                    String sid = sessionContextManager().createContext();
                    SessionContext context = sessionContextManager().getContext(sid);
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
    );

    public LoginTable(RouterConfig config, HtmlManager htmlManager, SessionContextManager sessionContextManager) {
        this.htmlManager = htmlManager;
        this.sessionContextManager = sessionContextManager;
        config.setRoute(table);
    }

    private HtmlManager htmlManager() {
        return htmlManager;
    }

    private SessionContextManager sessionContextManager() {
        return sessionContextManager;
    }

    private String readSingleBodyParam(HttpRequest request, String attr) {
        Object bodyParam = request.getBodyParam(attr);
        if (bodyParam instanceof String) return (String) bodyParam;
        return null;
    }
}
