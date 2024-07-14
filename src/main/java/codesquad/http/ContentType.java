package codesquad.http;

/**
 * http response message의 content type을 정의합니다.
 */
public enum ContentType {

    TEXT_PLAIN("text/plain", "text", "plain", "UTF-8"),
    TEXT_HTML("text/html", "text", "html", "UTF-8"),
    TEXT_CSS("text/css", "text", "css", "UTF-8"),
    TEXT_JS("text/javascript", "text", "javascript", "UTF-8"),
    IMAGE_SVG("image/svg+xml", "image", "svg+xml", null),
    IMAGE_PNG("image/png", "image", "png", null),
    IMAGE_ICON("image/x-icon", "image", "x-icon", null),
    IMAGE_JPG("image/jpg", "image", "jpg", null),
    APPLICATION_JSON("application/json", "application", "json", null);

    public final String fullType;
    public final String type;
    public final String subType;
    public final String charset;

    ContentType(String fullType, String type, String subType, String charset) {
        this.fullType = fullType;
        this.type = type;
        this.subType = subType;
        this.charset = charset;
    }

    public static ContentType of(String extension) {
        return switch (extension) {
            case "html" -> TEXT_HTML;
            case "css" -> TEXT_CSS;
            case "js" -> TEXT_JS;
            case "ico" -> IMAGE_ICON;
            case "png" -> IMAGE_PNG;
            case "svg" -> IMAGE_SVG;
            case "jpg" -> IMAGE_JPG;
            case "json" -> APPLICATION_JSON;
            default -> TEXT_PLAIN;
        };
    }
}
