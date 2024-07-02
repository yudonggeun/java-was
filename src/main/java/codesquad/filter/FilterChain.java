package codesquad.filter;

import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;

public interface FilterChain {

    void doFilter(HttpRequest request, HttpResponse response);
}
