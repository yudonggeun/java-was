package codesquad.application.repository;

import codesquad.application.domain.Comment;

import java.util.List;

public interface CommentRepository {

    List<Comment> findByArticleId(String articleId, Pageable pageable);

    Comment save(Comment comment);

    void deleteById(String commentId);
}
