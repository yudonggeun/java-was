package codesquad.handler;

import codesquad.http.Method;

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

    public static Builder method(Method... methods) {
        return new Builder(methods);
    }

    public static Builder get(String urlTemplate) {
        return new Builder(Method.GET).url(urlTemplate);
    }

    public static Builder post(String urlTemplate) {
        return new Builder(Method.POST).url(urlTemplate);
    }

    public static Builder put(String urlTemplate) {
        return new Builder(Method.PUT).url(urlTemplate);
    }

    public static Builder delete(String urlTemplate) {
        return new Builder(Method.DELETE).url(urlTemplate);
    }

    //---------------------------------------------------------------------------
    public String getUrlTemplate() {
        return urlTemplate;
    }

    public Method[] getMethods() {
        return methods;
    }

    public static class Builder {

        private final Method[] methods;
        private String urlTemplate;

        private Builder(Method... methods) {
            this.methods = methods;
        }

        public Builder url(String urlTemplate) {
            this.urlTemplate = urlTemplate;
            return this;
        }

        public URLMatcher build() {
            return new URLMatcher(methods, urlTemplate);
        }
    }

}
