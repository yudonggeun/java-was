package codesquad.config;

import codesquad.application.domain.User;
import codesquad.application.repository.MyRepository;
import codesquad.context.SessionContext;
import codesquad.context.SessionContextManager;
import codesquad.handler.HttpHandler;
import codesquad.handler.StaticResourceHandler;
import codesquad.handler.URLMatcher;
import codesquad.http.*;
import codesquad.template.HtmlElement;
import codesquad.template.HtmlManager;
import codesquad.template.HtmlRoot;
import codesquad.template.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static codesquad.http.Method.GET;
import static codesquad.http.Method.POST;

/**
 * http 요청을 매핑 설정입니다.
 */
public class RouterConfig {

    private final Logger logger = LoggerFactory.getLogger(StaticResourceHandler.class);
    private final HtmlManager htmlManager = new HtmlManager();

    public Map<URLMatcher, HttpHandler> getHandlerMap() {
        Map<URLMatcher, HttpHandler> handlerMap = new HashMap<>();
        handlerMap.putAll(staticResourceHandlerMap());
        handlerMap.putAll(apiHandlerMap());
        return handlerMap;
    }

    private Map<URLMatcher, HttpHandler> apiHandlerMap() {
        Map<URLMatcher, HttpHandler> map = new HashMap<>();

        map.put(
                URLMatcher.method(GET).urlTemplate("/registration").build(), request -> {
                    HttpResponse response = HttpResponse.of(HttpStatus.MOVED_PERMANENTLY);
                    response.addHeader("Location", "/registration/index.html");
                    return response;
                }
        );

        map.put(
                URLMatcher.method(POST).urlTemplate("/user/create").build(), request -> {
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
                }
        );

        map.put(
                URLMatcher.method(GET).urlTemplate("/login").build(), request -> {
                    HttpResponse response = HttpResponse.of(HttpStatus.MOVED_PERMANENTLY);
                    response.addHeader("Location", "/login/index.html");
                    return response;
                }
        );

        map.put(
                URLMatcher.method(GET).urlTemplate("/logout").build(), request -> {
                    SessionContext session = SessionContextManager.getSession(request);
                    if (session != null) {
                        SessionContextManager.clearContext(request);
                    }
                    HttpResponse response = HttpResponse.of(HttpStatus.SEE_OTHER);
                    response.addHeader("Location", "/index.html");
                    response.addHeader("Set-Cookie", "SID=; Max-Age=0");
                    return response;
                }
        );

        map.put(
                URLMatcher.method(POST).urlTemplate("/signin").build(), request -> {
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
        );

        map.put(
                URLMatcher.method(GET).urlTemplate("/user/list").build(), request -> {
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

                        List<User> users = MyRepository.source.findAllUser();

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
        );

        return map;
    }

    private Map<URLMatcher, HttpHandler> staticResourceHandlerMap() {
        Map<URLMatcher, HttpHandler> staticResourceHandlerMap = new HashMap<>();
        boolean isJar = getClass().getClassLoader().getResource("static").toString()
                .startsWith("jar");
        if (isJar) {
            try {
                String jarPath = getClass().getProtectionDomain().getCodeSource().getLocation().toURI()
                        .getPath();
                JarFile jarFile = new JarFile(jarPath);
                Enumeration<JarEntry> entries = jarFile.entries();

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    if (entryName.startsWith("static/") && !entry.isDirectory()) {
                        try (InputStream inputStream = jarFile.getInputStream(entry)) {
                            byte[] bytes = inputStream.readAllBytes();

                            String urlTemplate = entryName.replaceFirst("static", "");
                            URLMatcher urlMatcher = URLMatcher.method(GET).urlTemplate(urlTemplate).build();
                            HttpHandler httpHandler = new StaticResourceHandler(urlTemplate, bytes);
                            staticResourceHandlerMap.put(urlMatcher, httpHandler);
                        }
                    }
                }
                jarFile.close();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        } else {
            try {
                Enumeration<URL> resources = getClass().getClassLoader().getResources("static");
                while (resources.hasMoreElements()) {
                    URL resource = resources.nextElement();
                    try {
                        provideFile(new File(resource.getFile()), staticResourceHandlerMap);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return staticResourceHandlerMap;
    }

    private void provideFile(File file, Map<URLMatcher, HttpHandler> map) {

        if (!file.exists()) {
            return;
        }

        if (file.isDirectory()) {
            for (File subFile : Objects.requireNonNull(file.listFiles())) {
                provideFile(subFile, map);
            }
        } else {
            String path = file.getPath().split("static")[1];
            try (InputStream inputStream = new FileInputStream(file)) {
                byte[] bytes = inputStream.readAllBytes();

                String urlTemplate = path;
                URLMatcher urlMatcher = URLMatcher.method(GET).urlTemplate(urlTemplate).build();
                HttpHandler httpHandler = new StaticResourceHandler(urlTemplate, bytes);
                map.put(urlMatcher, httpHandler);
            } catch (IOException e) {
                logger.error("Error reading from binary file: {}", path);
                throw new RuntimeException(e);
            }
        }
    }

    private String readSingleBodyParam(HttpRequest request, String attr) {
        Object bodyParam = request.getBodyParam(attr);
        if (bodyParam instanceof String) return (String) bodyParam;
        return null;
    }

}
