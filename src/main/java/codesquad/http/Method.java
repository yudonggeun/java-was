package codesquad.http;

public enum Method {
    GET, POST, PUT, DELETE, HEAD, OPTIONS, TRACE, CONNECT, PATCH;

    /**
     * GET, POST와 같은 문자열을 enum 객체로 변환합니다.
     *
     * @param method HTTP 요청 메소드
     * @return HTTP 요청 메소드에 해당하는 enum 객체
     */
    public static Method of(String method) {
        for (Method m : values()) {
            if (m.name().equalsIgnoreCase(method)) {
                return m;
            }
        }
        throw new IllegalArgumentException("요청 메소드가 잘못되었습니다.");
    }
}
