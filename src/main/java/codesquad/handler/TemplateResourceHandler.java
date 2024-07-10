package codesquad.handler;

import codesquad.context.SessionContext;
import codesquad.context.SessionContextManager;
import codesquad.http.*;
import codesquad.template.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Deque;
import java.util.LinkedList;

public class TemplateResourceHandler implements HttpHandler {

    private final Logger logger = LoggerFactory.getLogger(StaticResourceHandler.class);

    @Override
    public boolean match(HttpRequest request) {
        URL resource = this.getClass().getResource("/templates" + request.path);
        return resource != null && resource.getPath().contains(".");
    }

    @Override
    public HttpResponse doRun(HttpRequest request) {
        HttpResponse response = HttpResponse.of(HttpStatus.OK);
        ContentType contentType = getContentType(request.path);
        response.addHeader("Content-Type", contentType.fullType);
        convertToHtml(request.path, request, response);
        return response;
    }

    private ContentType getContentType(String fileName) {
        // extract file extension
        int dotIndex = fileName.lastIndexOf('.');
        String extension = fileName.substring(dotIndex + 1);

        // content type
        return switch (dotIndex) {
            case -1 -> ContentType.TEXT_PLAIN;
            default -> ContentType.of(extension);
        };
    }

    private void writeFileToBody(String path, HttpResponse response) {

        try (InputStream in = this.getClass().getResourceAsStream("/templates" + path)) {
            byte[] fileContentBytes = in.readAllBytes();
            response.setBody(fileContentBytes);
        } catch (FileNotFoundException e) {
            logger.error("File not found: {}", path);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("Error reading from binary file: {}", path);
            throw new RuntimeException(e);
        }
    }

    public void convertToHtml(String path, HttpRequest request, HttpResponse response) {
        try (InputStream in = this.getClass().getResourceAsStream("/templates" + path)) {
            String template = new String(in.readAllBytes());
            HtmlRoot root = new HtmlRoot();
            // split tags
            Deque<String> tags = new LinkedList<>();
            StringBuilder tag = new StringBuilder();
            char[] templates = template.toCharArray();
            for (int i = 0; i < template.length(); i++) {
                if (templates[i] == '<') {
                    if (!tag.isEmpty() && !tag.toString().trim().isEmpty()) tags.add(tag.toString());
                    tag = new StringBuilder();
                    tag.append(templates[i]);
                } else if (templates[i] == '>') {
                    tag.append(templates[i]);
                    if (!tag.isEmpty() && !tag.toString().trim().isEmpty()) tags.add(tag.toString());
                    tag = new StringBuilder();
                } else {
                    tag.append(templates[i]);
                }
            }

            while (!tags.isEmpty()) {
                HtmlElement.Builder elementBuilder = extractElement(tags);
                root.addElement(elementBuilder.build());
            }

            SessionContext session = getSession(request);

            Model model = new Model();
            model.setSession(session);
            root.applyModel(model);

            System.out.println(root.toHtml());
            response.setBody(root.toHtml().getBytes());
        } catch (IOException e) {
            logger.error("Error reading from binary file: {}", path);
            throw new RuntimeException(e);
        }
    }

    private SessionContext getSession(HttpRequest request) {
        String sid = null;
        String cookies = request.getHeader("Cookie");
        if (cookies == null) return null;
        cookies = cookies.replace(";", "");
        for (String cookie : cookies.split(" ")) {
            String[] entry = cookie.split("=");
            String key = entry[0];
            String value = entry[1];
            if (key.equals("SID")) {
                sid = value;
                break;
            }
        }

        SessionContext session = SessionContextManager.getContext(sid);
        return session;
    }

    private HtmlElement.Builder extractElement(Deque<String> tags) {
        String firstTags = tags.pollFirst();
        HtmlElement.Builder elementBuilder = HtmlElement.create(firstTags);

        if (!elementBuilder.isOpen()) {
            return elementBuilder;
        }

        String nextTag = tags.peekFirst();

        while (!tags.isEmpty()) {
            nextTag = tags.peekFirst();
            if (!elementBuilder.isOpen()) {
                break;
            }
            if (nextTag.contains("/" + elementBuilder.getTag())) {
                tags.pollFirst();
                break;
            }
            elementBuilder.addChildren(extractElement(tags));
        }
        if (nextTag != null) {
            elementBuilder.setClose();
        }
        return elementBuilder;
    }
}
