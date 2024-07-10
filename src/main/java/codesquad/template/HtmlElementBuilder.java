package codesquad.template;

public interface HtmlElementBuilder {

    HtmlElementBuilder setLine(String line);

    boolean isOpen();

    String getTag();

    HtmlElementBuilder addChildren(HtmlElementBuilder... builder);

    HtmlElementBuilder setClose();

    HtmlElement build();
}
