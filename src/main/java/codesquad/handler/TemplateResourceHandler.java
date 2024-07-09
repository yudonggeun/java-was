package codesquad.handler;

import codesquad.http.*;
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
        writeFileToBody(request.path, response);
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

    public HtmlRoot convertToHtml(String template) {
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

        System.out.println(tags);
        while (!tags.isEmpty()) {
            Deque<String> nextTags = new LinkedList<>();
            HtmlElement.Builder elementBuilder = extractElement(tags);
            System.out.println("loop : " + elementBuilder.build().toHtml());
            root.addElement(elementBuilder.build());

            tags = nextTags;
        }
        System.out.println(root.toHtml());
        return root;
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
