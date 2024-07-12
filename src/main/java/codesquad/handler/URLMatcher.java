package codesquad.handler;

import codesquad.http.HttpRequest;
import codesquad.http.Method;

import java.util.Arrays;

/**
 * http request의 요청과 대응하는지 검사하는 클래스입니다.
 */
public class URLMatcher {

    private final Method[] methods;
    private final String urlTemplate;

    private URLMatcher(Method[] methods, String urlTemplate) {
        this.methods = methods;
        this.urlTemplate = urlTemplate;
    }

    // builder
    public static Builder method(Method... methods) {
        return new Builder().methods(methods);
    }

    public boolean isMatch(HttpRequest request) {
        return isMethodMatch(request.method) && isURLMatch(request.path);
    }
    // builder end

    private boolean isURLMatch(String path) {
        return urlTemplate.equals(path);
    }

    private boolean isMethodMatch(Method method) {
        return Arrays.asList(methods).contains(method);
    }

    public String getUrlTemplate() {
        return urlTemplate;
    }

    public Method[] getMethods() {
        return methods;
    }

    public static class Builder {
        private Method[] methods;
        private String urlTemplate;

        public Builder methods(Method... methods) {
            this.methods = methods;
            return this;
        }

        public Builder urlTemplate(String urlTemplate) {
            this.urlTemplate = urlTemplate;
            return this;
        }

        public URLMatcher build() {
            return new URLMatcher(methods, urlTemplate);
        }
    }
}
