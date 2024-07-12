package codesquad.util.collections;

import java.util.Optional;

public class Tries<T> {

    TriesNode<T> root = new TriesNode<>();

    public void insert(String url, T value) {
        if (url == null) return;

        TriesNode<T> node = this.root;

        String[] components = url.split("/");

        for (String component : components) {
            node = node.getChildren().computeIfAbsent(component, key -> new TriesNode<>());
        }
        node.setValue(value);
    }

    public Optional<T> search(String url) {
        if (url == null) return Optional.empty();
        TriesNode<T> node = this.root;

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
