package codesquad.application.domain;

public class User {

    private final String userId;
    private final String password;
    private final String nickname;

    public User(String userId, String password, String nickname) {
        this.userId = userId;
        this.password = password;
        this.nickname = nickname;
    }
}
