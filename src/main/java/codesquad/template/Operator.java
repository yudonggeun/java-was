package codesquad.template;

import java.util.function.BiFunction;

public enum Operator {

    eq("==", (a, b) -> {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }),

    ne("!=", (a, b) -> {
        if (a == null && b == null) return false;
        if (a == null || b == null) return true;
        return !a.equals(b);
    }),

    gt(">", (a, b) -> {
        if (a == null || b == null) return false;
        return (int) a > (int) b;
    }),

    lt("<", (a, b) -> {
        if (a == null || b == null) return false;
        return (int) a < (int) b;
    }),
    ;

    private final String operator;
    private final BiFunction<Object, Object, Boolean> biFunction;

    Operator(String operator, BiFunction<Object, Object, Boolean> biFunction) {
        this.operator = operator;
        this.biFunction = biFunction;
    }

    public static Operator of(String operator) {
        return switch (operator) {
            case "==" -> eq;
            case "!=" -> ne;
            case ">" -> gt;
            case "<" -> lt;
            default -> throw new IllegalArgumentException("지원하지 않는 연산자입니다.");
        };
    }

    public boolean match(Object a, Object b) {
        return biFunction.apply(a, b);
    }
}
