package codesquad.application.repository;

import codesquad.application.domain.Article;
import codesquad.util.scan.Solo;

@Solo
public class MockArticleRepository implements ArticleRepository {

    @Override
    public Article findById(String id) {
        return new Article("1", "유동근", "title 제목", "내용입니다.");
    }

    @Override
    public Article findOne() {
        return new Article("1", "유동근", "title 제목", "내용입니다.");
    }

    @Override
    public void save(Article article) {
    }

    @Override
    public void deleteById(String id) {
    }
}
