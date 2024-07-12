package codesquad.util.collections;

import java.util.HashMap;
import java.util.Map;

public class TriesNode<T> {

    private final Map<String, TriesNode<T>> children = new HashMap<>();
    private T value;

    public Map<String, TriesNode<T>> getChildren() {
        return children;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
