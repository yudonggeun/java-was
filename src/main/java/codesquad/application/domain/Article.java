package codesquad.application.domain;

public record Article(
        String id,
        String writer,
        String title,
        String content,
        String imagePath
) {
}
