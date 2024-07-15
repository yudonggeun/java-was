package codesquad.application.repository;

import codesquad.application.domain.Article;

public interface ArticleRepository {

    Article findById(String id);

    Article findOne();

    void save(Article article);

    void deleteById(String id);
}
