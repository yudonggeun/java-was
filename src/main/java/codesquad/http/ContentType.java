package codesquad.http;

/**
 * http response message의 content type을 정의합니다.
 */
public enum ContentType {

    TEXT_PLAIN("text/plain", "text", "plain"),
    TEXT_HTML("text/html", "text", "html"),
    TEXT_CSS("text/css", "text", "css"),
    TEXT_JS("text/javascript", "text", "javascript"),
    IMAGE_SVG("image/svg+xml", "image", "svg+xml"),
    IMAGE_PNG("image/png", "image", "png"),
    IMAGE_ICON("image/x-icon", "image", "x-icon"),
    IMAGE_JPG("image/jpg", "image", "jpg"),
    APPLICATION_JSON("application/json", "application", "json");

    public final String fullType;
    public final String type;
    public final String subType;

    ContentType(String fullType, String type, String subType) {
        this.fullType = fullType;
        this.type = type;
        this.subType = subType;
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
