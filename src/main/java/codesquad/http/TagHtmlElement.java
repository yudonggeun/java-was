package codesquad.http;

import java.util.*;

public class TagHtmlElement implements HtmlElement {

    private String tag;
    private Map<String, String> attributes;
    private List<HtmlElement> children;

    /* create */
    private TagHtmlElement() {
    }

    public String toHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<").append(tag);
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            sb.append(" ").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
        }
        sb.append(">");
        for (HtmlElement child : children) {
            sb.append(child.toHtml());
        }
        sb.append("</").append(tag).append(">");
        return sb.toString();
    }
    /* create end*/

    public static class ElementBuilder implements HtmlElement.Builder {
        private String tag;
        private final Map<String, String> attributes = new HashMap<>();
        private final List<HtmlElement.Builder> children = new ArrayList<>();
        private boolean isOpen;

        public ElementBuilder() {
        }

        // <tag attr="value"> extract
        public HtmlElement.Builder setLine(String line) {
            if (line.startsWith("<") && line.endsWith("/>")) {
                line = line.substring(1, line.length() - 2);
                String[] attrs = line.split(" ");
                this.tag = attrs[0];
                for (int i = 1; attrs.length > i; i++) {
                    String[] splitAttr = attrs[i].split("=");
                    String key = splitAttr[0];
                    String value = splitAttr.length == 2 ? splitAttr[1].replace("\"", "") : null;
                    this.attributes.put(key, value);
                }
                isOpen = false;
            } else {
                line = line.substring(1, line.length() - 1);
                String[] attrs = line.split(" ");
                this.tag = attrs[0];
                for (int i = 1; attrs.length > i; i++) {
                    String[] splitAttr = attrs[i].split("=");
                    String key = splitAttr[0];
                    String value = splitAttr.length == 2 ? splitAttr[1].replace("\"", "") : null;
                    this.attributes.put(key, value);
                }
                isOpen = true;
            }
            return this;
        }

        @Override
        public HtmlElement.Builder addChildren(Builder... children) {
            Collections.addAll(this.children, children);
            return this;
        }

        @Override
        public TagHtmlElement build() {
            TagHtmlElement element = new TagHtmlElement();
            element.tag = tag;
            element.attributes = attributes;
            element.children = this.children.stream().map(HtmlElement.Builder::build).toList();
            return element;
        }

        @Override
        public HtmlElement.Builder setClose() {
            this.isOpen = false;
            return this;
        }

        @Override
        public boolean isOpen() {
            return isOpen;
        }

        @Override
        public String getTag() {
            return this.tag;
        }

    }
}
