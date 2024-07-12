package codesquad.util.collections;

import java.util.Optional;

public class StringTries {

    TriesNode<String> root = new TriesNode<>();

    void insert(String url, String value) {
        if (url == null) return;

        TriesNode<String> node = this.root;

        String[] components = url.split("/");

        for (String component : components) {
            node = node.getChildren().computeIfAbsent(component, key -> new TriesNode<>());
        }
        node.setValue(value);
    }

    public Optional<String> search(String url) {
        if (url == null) return Optional.empty();
        TriesNode<String> node = this.root;

        String[] components = url.split("/");

        for (String component : components) {
            node = node.getChildren().getOrDefault(component, null);
            if (node == null) {
                return Optional.empty();
            }
        }

        return Optional.of(node.getValue());
    }
}
