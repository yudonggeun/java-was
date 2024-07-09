package codesquad.http;

public class StringHtmlElement implements HtmlElement {

    private final String value;

    private StringHtmlElement(String value) {
        this.value = value;
    }

    @Override
    public String toHtml() {
        return value;
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
