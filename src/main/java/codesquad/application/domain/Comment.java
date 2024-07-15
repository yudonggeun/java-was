package codesquad.application.domain;

public class Comment {
    private final User user;
    private final String articleId;
    private final String contents;

    public Comment(User user, String articleId, String contents) {
        this.user = user;
        this.articleId = articleId;
        this.contents = contents;
    }

    public User getUser() {
        return user;
    }

    public String getArticleId() {
        return articleId;
    }

    public String getContents() {
        return contents;
    }

}
