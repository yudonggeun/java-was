package codesquad.filter;

import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;

/**
 * 응답의 Content-Type이 text인 경우 charset=utf-8을 추가하는 필터입니다.
 */
public class CharSetFilter implements Filter {

    private final String charSet;

    public CharSetFilter(String charSet) {
        this.charSet = charSet;
    }

    @Override
    public void postHandle(HttpRequest request, HttpResponse response) {
        String contentType = response.getHeader("Content-Type");
        if (contentType == null) {
            throw new IllegalArgumentException("Content-Type이 없습니다.");
        }
        if (contentType.startsWith("text")) {
            response.addHeader("Content-Type", contentType + "; charset=" + charSet);
        }
    }
}
