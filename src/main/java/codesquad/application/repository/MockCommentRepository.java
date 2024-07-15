package codesquad.application.repository;

import codesquad.application.domain.Comment;
import codesquad.application.domain.User;
import codesquad.util.scan.Solo;

import java.util.List;

@Solo
public class MockCommentRepository implements CommentRepository {

    @Override
    public List<Comment> findByArticleId(String articleId, Pageable pageable) {
        return List.of(
                new Comment(new User("1", "user", "페페123"), "1", "가짜 댓글 입니다.1"),
                new Comment(new User("2", "user", "짱구는 못말려"), "1", "가짜 댓글 입니다.2")
        );
    }

    @Override
    public void save(Comment comment) {

    }

    @Override
    public void deleteById(String commentId) {

    }
}
