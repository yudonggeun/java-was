package codesquad.template;

import java.util.List;

public interface HtmlElement {

    static HtmlElementBuilder create(String line) {
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

    void addChild(HtmlElement element);
}
