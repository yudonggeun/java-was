package codesquad.http;

import java.util.ArrayList;
import java.util.List;

public class HtmlRoot {

    private final List<HtmlElement> elementList = new ArrayList<>();

    public String toHtml() {
        StringBuilder sb = new StringBuilder();
        for (var element : elementList) {
            sb.append(element.toHtml());
        }
        return sb.toString();
    }

    public void addElement(HtmlElement element) {
        elementList.add(element);
    }
}
