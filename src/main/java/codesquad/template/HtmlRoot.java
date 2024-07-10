package codesquad.template;

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

    public void applyModel(Model model) {
        for (HtmlElement element : elementList) {
            element.applyModel(model);
        }
    }

    public HtmlElement findById(String id) {
        for (HtmlElement htmlElement : elementList) {
            HtmlElement result = findById(id, htmlElement);
            if (result != null) return result;
        }
        return null;
    }

    private HtmlElement findById(String id, HtmlElement element) {
        String elementId = element.getAttribute("id");
        if (elementId != null && elementId.equals(id)) {
            return element;
        }
        for (HtmlElement child : element.getChildren()) {
            HtmlElement result = findById(id, child);
            if (result != null) return result;
        }
        return null;
    }
}
