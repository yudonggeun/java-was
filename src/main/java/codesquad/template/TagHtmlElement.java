package codesquad.template;

import java.util.*;

public class TagHtmlElement implements HtmlElement {

    private String tag;
    private Map<String, String> attributes;
    private List<HtmlElement> children;

    private TagHtmlElement() {
    }

    @Override
    public void applyModel(Model model) {
        // woowa-if
        if (attributes.containsKey("woowa-if")) {
            String command = attributes.get("woowa-if");
            if (command != null) {
                // command는 현재 a op b 형태의 연산만 지원한다.
                String[] args = command.split(" ");
                if (args.length == 3) {
                    Object a = model.getAttribute(args[0]);
                    Object b = model.getAttribute(args[2]);
                    Operator op = Operator.of(args[1]);

                    boolean isMatch = op.match(a, b);
                    System.out.println(command + ": view=" + isMatch);
                    if (!isMatch) {
                        System.out.println(tag + " is removed " + this.getAttribute("woowa-if"));
                        this.tag = null;
                        this.children = List.of();
                        return;
                    }
                }
            }
        }
        // woowa-value
        if (attributes.containsKey("woowa-value")) {
            String key = attributes.get("woowa-value");
            if (key != null) {
                Object value = model.getAttribute(key);
                if (value != null) {
                    children = List.of(HtmlElement.create(value.toString()).build());
                }
            }
        }
        for (HtmlElement child : children) {
            child.applyModel(model);
        }
    }

    @Override
    public String toHtml() {
        // render
        if (tag == null) return "";
        StringBuilder sb = new StringBuilder();
        sb.append("<").append(tag);
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            sb.append(" ").append(entry.getKey());
            if (entry.getValue() != null) {
                sb.append("=\"").append(entry.getValue()).append("\"");
            }
        }
        sb.append(">");
        for (HtmlElement child : children) {
            sb.append(child.toHtml());
        }
        sb.append("</").append(tag).append(">");
        return sb.toString();
    }

    public List<HtmlElement> getChildren() {
        return children;
    }

    @Override
    public String getAttribute(String s) {
        return attributes.get(s);
    }

    @Override
    public void addChild(HtmlElement element) {
        this.children = new LinkedList<>(this.children);
        this.children.add(element);
    }

    public static class ElementBuilder implements HtmlElement.Builder {
        private String tag;
        private final Map<String, String> attributes = new HashMap<>();
        private final List<HtmlElement.Builder> children = new ArrayList<>();
        private boolean isOpen;

        public ElementBuilder() {
        }

        public HtmlElement.Builder setLine(String line) {
            if (line.startsWith("<") && line.endsWith("/>")) {
                line = line.substring(1, line.length() - 2);
                isOpen = false;
            } else {
                line = line.substring(1, line.length() - 1);
                isOpen = true;
            }
            String[] attrs = line.split("\\s+(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
            this.tag = attrs[0];
            if (tag.equals("br")) isOpen = false;
            if (tag.equals("!DOCTYPE")) isOpen = false;
            for (int i = 1; i < attrs.length; i++) {
                int index = attrs[i].indexOf('=');
                if (index != -1) {
                    String key = attrs[i].substring(0, index);
                    String value = attrs[i].substring(index + 1).replace("\"", "");
                    this.attributes.put(key, value);
                } else {
                    this.attributes.put(attrs[i], null);
                }
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
