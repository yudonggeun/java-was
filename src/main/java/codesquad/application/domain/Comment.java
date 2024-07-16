package codesquad.application.domain;

public class Comment {
    private final String id;
    private final String userId;
    private final String nickname;
    private final String articleId;
    private final String contents;

    public Comment(String id, String userId, String nickname, String articleId, String contents) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.articleId = articleId;
        this.contents = contents;
    }

    public String getId() {
        return id;
    }

    public String getArticleId() {
        return articleId;
    }

    public String getContents() {
        return contents;
    }

    public String getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }
}
