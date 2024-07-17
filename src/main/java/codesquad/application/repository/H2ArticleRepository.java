package codesquad.application.repository;

import codesquad.application.domain.Article;
import codesquad.config.DBConfig;
import codesquad.util.scan.Solo;

import java.sql.*;
import java.util.UUID;

@Solo
public class H2ArticleRepository implements ArticleRepository {

    private final DBConfig dbConfig;

    // init
    public H2ArticleRepository(DBConfig dbConfig) {
        this.dbConfig = dbConfig;
        try (Connection conn = DriverManager.getConnection(dbConfig.url, dbConfig.username, dbConfig.password);
             Statement stmt = conn.createStatement()
        ) {
            stmt.execute("CREATE TABLE IF NOT EXISTS ARTICLE (ID VARCHAR(255), WRITER VARCHAR(255), TITLE VARCHAR(255), CONTENT VARCHAR(255), IMAGE_PATH VARCHAR(255))");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Article findById(String id) {
        try (Connection conn = DriverManager.getConnection(dbConfig.url, dbConfig.username, dbConfig.password);
             Statement stmt = conn.createStatement()
        ) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM ARTICLE WHERE id='" + id + "'");
            while (rs.next()) {
                return new Article(
                        rs.getString("ID"),
                        rs.getString("WRITER"),
                        rs.getString("TITLE"),
                        rs.getString("CONTENT"),
                        rs.getString("IMAGE_PATH")
                );
            }
            // Extract data from result set
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Article findOne() {
        try (Connection conn = DriverManager.getConnection(dbConfig.url, dbConfig.username, dbConfig.password);
             Statement stmt = conn.createStatement()
        ) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM ARTICLE LIMIT 1");
            while (rs.next()) {
                return new Article(
                        rs.getString("ID"),
                        rs.getString("WRITER"),
                        rs.getString("TITLE"),
                        rs.getString("CONTENT"),
                        rs.getString("IMAGE_PATH")
                );
            }
            // Extract data from result set
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Article save(Article article) {
        try (Connection conn = DriverManager.getConnection(dbConfig.url, dbConfig.username, dbConfig.password);
             Statement stmt = conn.createStatement()
        ) {
            String id = UUID.randomUUID().toString();
            stmt.execute(String.format("INSERT INTO ARTICLE (ID, WRITER, TITLE, CONTENT, IMAGE_PATH) VALUES ('%s', '%s', '%s', '%s', '%s')", id, article.writer(), article.title(), article.content(), article.imagePath()));
            return new Article(id, article.writer(), article.title(), article.content(), article.imagePath());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteById(String id) {
        try (Connection conn = DriverManager.getConnection(dbConfig.url, dbConfig.username, dbConfig.password);
             Statement stmt = conn.createStatement()
        ) {
            stmt.execute("DELETE FROM ARTICLE WHERE id=" + id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
