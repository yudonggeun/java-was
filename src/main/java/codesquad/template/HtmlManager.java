package codesquad.template;

import codesquad.util.scan.Solo;

import java.util.Deque;
import java.util.LinkedList;

@Solo
public class HtmlManager {

    public HtmlRoot create(String html) {
        HtmlRoot root = new HtmlRoot();
        Deque<String> tags = new LinkedList<>();
        StringBuilder tag = new StringBuilder();
        char[] templates = html.toCharArray();
        for (int i = 0; i < html.length(); i++) {
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
            HtmlElementBuilder elementBuilder = extractElement(tags);
            root.addElement(elementBuilder.build());
        }

        return root;
    }

    public HtmlElement createElement(String html) {
        Deque<String> tags = new LinkedList<>();
        StringBuilder tag = new StringBuilder();
        char[] templates = html.toCharArray();
        for (int i = 0; i < html.length(); i++) {
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

        return extractElement(tags).build();
    }

    private HtmlElementBuilder extractElement(Deque<String> tags) {
        String firstTags = tags.pollFirst();
        HtmlElementBuilder elementBuilder = HtmlElement.create(firstTags);

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
