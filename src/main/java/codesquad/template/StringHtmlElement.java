package codesquad.template;

import java.util.List;

public class StringHtmlElement implements HtmlElement {

    private final String value;

    private StringHtmlElement(String value) {
        this.value = value;
    }

    @Override
    public void applyModel(Model model) {
        // do nothing
    }

    @Override
    public String toHtml() {
        return value;
    }

    @Override
    public List<HtmlElement> getChildren() {
        return List.of();
    }

    @Override
    public String getAttribute(String s) {
        return "";
    }

    @Override
    public void addChild(HtmlElement element) {
        // todo HtmlElement 인터페이스 리텍토링
        throw new UnsupportedOperationException();
    }

    public static class ElementBuilder implements HtmlElement.Builder {

        private String value;

        @Override
        public HtmlElement.Builder setLine(String line) {
            this.value = line;
            return this;
        }

        @Override
        public HtmlElement build() {
            return new StringHtmlElement(value);
        }

        @Override
        public boolean isOpen() {
            return false;
        }

        @Override
        public String getTag() {
            return "text";
        }

        @Override
        public Builder addChildren(Builder... builder) {
            //todo
            return null;
        }

        @Override
        public Builder setClose() {
            return null;
        }
    }
}
