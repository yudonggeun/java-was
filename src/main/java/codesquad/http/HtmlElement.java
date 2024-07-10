package codesquad.http;

import codesquad.template.Model;

import java.util.List;

public interface HtmlElement {

    static HtmlElement.Builder create(String line) {
        if (line.startsWith("<") && line.endsWith("/>")) {
            return new TagHtmlElement.ElementBuilder()
                    .setLine(line);
        } else if (line.startsWith("<") && line.endsWith(">")) {
            return new TagHtmlElement.ElementBuilder()
                    .setLine(line);
        } else {
            return new StringHtmlElement.ElementBuilder()
                    .setLine(line);
        }
    }

    void applyModel(Model model);

    String toHtml();

    List<HtmlElement> getChildren();

    String getAttribute(String s);

    interface Builder {

        HtmlElement.Builder setLine(String line);

        boolean isOpen();

        String getTag();

        HtmlElement.Builder addChildren(Builder... builder);

        HtmlElement.Builder setClose();

        HtmlElement build();
    }
}
