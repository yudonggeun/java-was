package codesquad.router.table;

import codesquad.application.domain.Article;
import codesquad.application.domain.Comment;
import codesquad.application.domain.User;
import codesquad.application.repository.ArticleRepository;
import codesquad.application.repository.CommentRepository;
import codesquad.application.repository.H2ArticleRepository;
import codesquad.application.repository.H2CommentRepository;
import codesquad.config.RouterConfig;
import codesquad.context.SessionContext;
import codesquad.context.SessionContextManager;
import codesquad.http.ContentType;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import codesquad.http.MultipartContent;
import codesquad.router.RouteTableRow;
import codesquad.template.HtmlElement;
import codesquad.template.HtmlManager;
import codesquad.template.HtmlRoot;
import codesquad.template.Model;
import codesquad.util.file.ResourceFileManager;
import codesquad.util.scan.Solo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static codesquad.router.RouteTableRow.get;
import static codesquad.router.RouteTableRow.post;
import static codesquad.router.rule.RouteRules.MULTIPART_REQUEST_RULE;

@Solo
public class ArticleTable {

    private final Logger logger = LoggerFactory.getLogger(ArticleTable.class);
    private final HtmlManager htmlManager;
    private final SessionContextManager sessionContextManager;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    public ArticleTable(RouterConfig config, HtmlManager htmlManager, SessionContextManager sessionContextManager, H2ArticleRepository articleRepository, H2CommentRepository commentRepository) {
        this.htmlManager = htmlManager;
        this.sessionContextManager = sessionContextManager;
        this.articleRepository = articleRepository;
        this.commentRepository = commentRepository;
        config.addRouteTable(table());
    }

    private List<RouteTableRow> table() {
        return List.of(
                // 게시글 작성 페이지 조회
                get("/article").handle(request -> {
                    if (sessionContextManager.getSession(request) == null) {
                        HttpResponse response = HttpResponse.of(HttpStatus.SEE_OTHER);
                        response.addHeader("Location", "/login/index.html");
                        return response;
                    }
                    HttpResponse response = HttpResponse.of(HttpStatus.SEE_OTHER);
                    response.addHeader("Location", "/article/index.html");
                    return response;
                }),

                // 게시글 조회
                get("/article/{articleId}").handle(request -> {
                    String articleId = request.path.split("/")[2];
                    SessionContext session = sessionContextManager.getSession(request);

                    try (InputStream inputStream = ResourceFileManager.getInputStream("templates/index.html")) {
                        byte[] file = inputStream.readAllBytes();
                        HtmlRoot root = htmlManager.create(new String(file));

                        Article article = articleRepository.findById(articleId);
                        List<Comment> comments = commentRepository.findByArticleId(articleId, null);

                        Model model = new Model();
                        model.setSession(session);
                        model.addAttribute("article", article);

                        root.applyModel(model);

                        HtmlElement element = root.findById("comment-list");

                        if (element != null) {
                            for (Comment comment : comments) {
                                element.addChild(htmlManager.createElement(String.format("""
                                        <li class="comment__item">
                                            <div class="comment__item__user">
                                                <img class="comment__item__user__img"/>
                                                <p class="comment__item__user__nickname">%s</p>
                                            </div>
                                            <p class="comment__item__article">
                                                %s
                                            </p>
                                        </li>
                                        """, comment.getNickname(), comment.getContents()))
                                );
                            }
                        }

                        HttpResponse response = HttpResponse.of(HttpStatus.OK);
                        response.setContentType(ContentType.TEXT_HTML);
                        response.setBody(root.toHtml().getBytes());
                        return response;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }),

                // 게시글 작성
                post("/article").rules(MULTIPART_REQUEST_RULE).handle(request -> {
                    SessionContext session = sessionContextManager.getSession(request);
                    if (session == null) {
                        HttpResponse response = HttpResponse.of(HttpStatus.SEE_OTHER);
                        response.addHeader("Location", "/login/index.html");
                        return response;
                    }

                    Optional<String> optionalBoundary = request.getHeaders("Content-Type").stream()
                            .filter(attr -> attr.startsWith("boundary="))
                            .map(attr -> attr.substring(9))
                            .findAny();

                    if (optionalBoundary.isEmpty()) {
                        return HttpResponse.of(HttpStatus.BAD_REQUEST);
                    }

                    String boundary = optionalBoundary.get();

                    List<MultipartContent> multipartContents = MultipartContent.parse(boundary, request.getByteBody());

                    User user = (User) session.getAttribute("user");
                    String title = null;
                    String content = null;
                    byte[] file = null;
                    for (MultipartContent data : multipartContents) {
                        if (data.name.equals("title")) {
                            title = new String(data.getContent());
                        } else if (data.name.equals("content")) {
                            content = new String(data.getContent());
                        } else if (data.name.equals("image")) {
                            file = data.getContent();
                        }
                    }

                    if (title == null || content == null || file == null) {
                        return HttpResponse.of(HttpStatus.BAD_REQUEST);
                    }

                    articleRepository.save(new Article(UUID.randomUUID().toString(), user.getNickname(), title, content));

                    HttpResponse response = HttpResponse.of(HttpStatus.SEE_OTHER);
                    response.addHeader("Location", "/index.html");
                    return response;
                }),

                get("/index.html").handle(request -> {
                    SessionContext session = sessionContextManager.getSession(request);

                    try (InputStream inputStream = ResourceFileManager.getInputStream("templates/index.html")) {
                        byte[] file = inputStream.readAllBytes();
                        HtmlRoot root = htmlManager.create(new String(file));

                        // find article
                        Article article = articleRepository.findOne();

                        if (article != null) {
                            List<Comment> comments = commentRepository.findByArticleId(article.id(), null);

                            HtmlElement element = root.findById("comment-list");

                            if (element != null) {
                                for (Comment comment : comments) {
                                    element.addChild(htmlManager.createElement(String.format("""
                                            <li class="comment__item">
                                                <div class="comment__item__user">
                                                    <img class="comment__item__user__img"/>
                                                    <p class="comment__item__user__nickname">%s</p>
                                                </div>
                                                <p class="comment__item__article">
                                                    %s
                                                </p>
                                            </li>
                                            """, comment.getNickname(), comment.getContents()))
                                    );
                                }
                            }
                        }

                        Model model = new Model();
                        model.setSession(session);
                        model.addAttribute("article", article);

                        root.applyModel(model);


                        HttpResponse response = HttpResponse.of(HttpStatus.OK);
                        response.setContentType(ContentType.TEXT_HTML);
                        response.setBody(root.toHtml().getBytes());
                        return response;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }),

                get("/{articleId}/comment").handle(request -> {
                    String articleId = request.path.split("/")[1];
                    try (InputStream inputStream = ResourceFileManager.getInputStream("templates/comment/index.html")) {
                        byte[] file = inputStream.readAllBytes();

                        Article article = articleRepository.findById(articleId);

                        if (article == null) {
                            return HttpResponse.of(HttpStatus.NOT_FOUND);
                        }

                        Model model = new Model();
                        model.addAttribute("article", article);

                        HtmlRoot root = htmlManager.create(new String(file));
                        root.applyModel(model);

                        HttpResponse response = HttpResponse.of(HttpStatus.OK);
                        response.setContentType(ContentType.TEXT_HTML);
                        response.setBody(root.toHtml().getBytes());
                        return response;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }),

                post("/{articleId}/comment").handle(request -> {
                            SessionContext session = sessionContextManager.getSession(request);

                            if (session == null) {
                                HttpResponse response = HttpResponse.of(HttpStatus.SEE_OTHER);
                                response.addHeader("Location", "/login/index.html");
                                return response;
                            }

                            User user = (User) session.getAttribute("user");
                            String articleId = request.path.split("/")[1];

                            String contents = (String) request.getBodyParam("content");

                    Comment comment = new Comment(null, user.getUserId(), user.getNickname(), articleId, contents);

                            commentRepository.save(comment);

                            HttpResponse response = HttpResponse.of(HttpStatus.SEE_OTHER);
                            response.addHeader("Location", String.format("/article/%s", articleId));
                            return response;
                        }
                )
        );
    }
}
