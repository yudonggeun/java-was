package codesquad.filter;

import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;

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
