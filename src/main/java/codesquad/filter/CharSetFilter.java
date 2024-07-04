package codesquad.filter;

import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 응답의 Content-Type이 text인 경우 charset=utf-8을 추가하는 필터입니다.
 */
public class CharSetFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(CharSetFilter.class);
    private final String charSet;

    public CharSetFilter(String charSet) {
        this.charSet = charSet;
    }

    @Override
    public void postHandle(HttpRequest request, HttpResponse response) {
        String contentType = response.getHeader("Content-Type");
        if (contentType == null) {
            logger.debug("Content-Type is null");
            return;
        }
        if (contentType.startsWith("text")) {
            response.addHeader("Content-Type", contentType + "; charset=" + charSet);
        }
    }
}
