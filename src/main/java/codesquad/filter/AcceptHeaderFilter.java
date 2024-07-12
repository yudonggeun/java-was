package codesquad.filter;

import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import codesquad.util.scan.Solo;

/**
 * Accept 헤더를 검사하여 요청을 처리할 수 있는지를 판단하는 필터입니다.
 * Accept 헤더가 없거나 요청을 처리할 수 없는 경우 406 Not Acceptable 상태코드를 반환합니다.
 */
@Solo
public class AcceptHeaderFilter implements Filter {

    @Override
    public void postHandle(HttpRequest request, HttpResponse response) {
        String acceptHeaderValue = request.getHeader("Accept");
        String responseMIME = response.getHeader("Content-Type");

        // accept header가 없거나 responseMIME이 없으면 아무것도 하지 않습니다.
        if (acceptHeaderValue == null || responseMIME == null) {
            return;
        }
        if (!accept(acceptHeaderValue, responseMIME)) {
            response.clear();
            response.setStatus(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    private boolean accept(String acceptHeaderValue, String responseMIME) {
        String[] responseTypes = responseMIME.split("/");
        String responseMIMEType = responseTypes[0];
        String responseMIMESubType = responseTypes[1];

        String[] mimeTypes = acceptHeaderValue.split(",");
        for (String mimeType : mimeTypes) {
            String[] types = mimeType.trim().split("/");
            String type = types[0];
            String subType = types[1];

            boolean isMatchType = type.equals("*") || type.equals(responseMIMEType);
            boolean isMatchSubType = subType.equals("*") || subType.equals(responseMIMESubType);

            if (isMatchType && isMatchSubType) {
                return true;
            }
        }
        return false;
    }
}
