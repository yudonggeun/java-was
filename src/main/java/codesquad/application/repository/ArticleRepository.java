package codesquad.application.repository;

import codesquad.application.domain.Article;

public interface ArticleRepository {

    Article findById(String id);

    Article findOne();

    Article save(Article article);

    void deleteById(String id);
}
