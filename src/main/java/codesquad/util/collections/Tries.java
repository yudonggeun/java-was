package codesquad.util.collections;

import java.util.Optional;
import java.util.regex.Pattern;

public class Tries<T> {

    TriesNode<T> root = new TriesNode<>();

    public void insert(String url, T value) {
        if (url == null) return;

        TriesNode<T> node = this.root;

        String[] components = url.split("/");

        Pattern pattern = Pattern.compile("\\{[^}]*\\}");
        for (String component : components) {
            if (pattern.matcher(component).matches()) {
                component = "*";
            }
            node = node.getChildren().computeIfAbsent(component, key -> new TriesNode<>());
        }
        node.setValue(value);
    }

    public Optional<T> search(String url) {
        if (url == null) return Optional.empty();
        TriesNode<T> node = this.root;

        String[] components = url.split("/");

        for (String component : components) {
            TriesNode<T> nextNode = node.getChildren().getOrDefault(component, null);
            if (nextNode == null) {
                nextNode = node.getChildren().getOrDefault("*", null);
                if (nextNode == null) {
                    return Optional.empty();
                }
            }
            node = nextNode;
        }

        if (node.getValue() == null) {
            return Optional.empty();
        }
        return Optional.of(node.getValue());
    }
}
