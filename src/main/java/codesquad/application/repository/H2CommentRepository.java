package codesquad.application.repository;

import codesquad.application.domain.Comment;
import codesquad.util.scan.Solo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Solo
public class H2CommentRepository implements CommentRepository {

    private final String DB_URL = "jdbc:h2:tcp://localhost/~/test";
    private final String USER = "sa";
    private final String PASSWORD = "";

    // init
    public H2CommentRepository() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()
        ) {
            stmt.execute("CREATE TABLE IF NOT EXISTS COMMENT (" +
                         "ID VARCHAR(255), " +
                         "USER_ID VARCHAR(255), " +
                         "NICKNAME VARCHAR(255), " +
                         "ARTICLE_ID VARCHAR(255), " +
                         "CONTENT VARCHAR(255))");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<Comment> findByArticleId(String articleId, Pageable pageable) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()
        ) {
            List<Comment> result = new ArrayList<>();
            String sql = "SELECT * FROM COMMENT WHERE article_id='" + articleId + "'";
            if (pageable != null) {
                sql += " LIMIT " + pageable.size() + " OFFSET " + pageable.size() * pageable.page();
            }
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                result.add(new Comment(
                        rs.getString("ID"),
                        rs.getString("USER_ID"),
                        rs.getString("NICKNAME"),
                        rs.getString("ARTICLE_ID"),
                        rs.getString("CONTENT")
                ));
            }

            return result;
            // Extract data from result set
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return List.of();
    }

    @Override
    public Comment save(Comment comment) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()
        ) {
            String id = UUID.randomUUID().toString();
            stmt.execute(String.format("INSERT INTO COMMENT (ID, USER_ID, NICKNAME, ARTICLE_ID, CONTENT) VALUES ('%s', '%s', '%s', '%s', '%s')",
                    id, comment.getUserId(), comment.getNickname(), comment.getArticleId(), comment.getContents()));
            return new Comment(id, comment.getUserId(), comment.getNickname(), comment.getArticleId(), comment.getContents());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteById(String commentId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()
        ) {
            stmt.execute("DELETE FROM COMMENT WHERE id='" + commentId + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
