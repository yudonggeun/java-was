package codesquad.http;

/**
 * http response message의 content type을 정의합니다.
 */
public enum ContentType {
    TEXT_PLAIN("text/plain"),
    TEXT_HTML("text/html"),
    TEXT_CSS("text/css"),
    TEXT_JS("text/javascript"),
    IMAGE_SVG("image/svg+xml"),
    IMAGE_PNG("image/png"),
    IMAGE_ICON("image/x-icon"),
    IMAGE_JPG("image/jpg"),
    APPLICATION_JSON("application/json");

    private final String type;

    ContentType(String type) {
        this.type = type;
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

    public String getType() {
        return type;
    }
}
