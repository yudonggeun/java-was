package codesquad;

import codesquad.filter.AcceptHeaderFilter;
import codesquad.filter.Filter;
import codesquad.handler.HttpHandler;
import codesquad.handler.StaticResourceHandler;
import codesquad.http.ContentType;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.*;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final Set<Filter> filters = new TreeSet<>();
    private static final Set<HttpHandler> httpHandlers = new LinkedHashSet<>();

    public static void main(String[] args) throws IOException {
        // handler 등록
        httpHandlers.add(new StaticResourceHandler());
        // filter 등록
        filters.add(new AcceptHeaderFilter());

        int serverPort = 8080;
        ServerSocket serverSocket = new ServerSocket(serverPort);
        log.info("Listening for connection on port {} ....", serverPort);

        int corePoolSize = 1;
        int maximumPoolSize = 200;
        long keepAliveTime = 5000;
        TimeUnit unit = TimeUnit.MILLISECONDS;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();

        ExecutorService executorService = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);

        while (true) {
            var clientSocket = serverSocket.accept();
            executorService.submit(() -> {

                final Logger logger = LoggerFactory.getLogger(Thread.currentThread().getName());
                try (OutputStream output = clientSocket.getOutputStream()) {
                    HttpRequest request = new HttpRequest(clientSocket.getInputStream());
                    HttpResponse response = null;

                    logger.info("Request[method={}, host={}, path={}, headers={}, body={}]",
                            request.method,
                            request.getHeader("Host"),
                            request.path,
                            request.getHeaders(),
                            request.getBody()
                    );

                    // pre filter
                    for (Filter filter : filters) {
                        filter.preHandle(request);
                    }
                    // handler 실행
                    for (HttpHandler httpHandler : httpHandlers) {
                        if (httpHandler.match(request)) {
                            response = httpHandler.doRun(request);
                            break;
                        } else {
                            response = HttpResponse.of(HttpStatus.NOT_FOUND);
                        }
                    }
                    // post filter
                    for (Filter filter : filters) {
                        filter.postHandle(request, response);
                    }
                    logger.info("Response[status={}, headers={}, body={}]", response.getStatus(), response.getHeaderString(), response.getBody());

                    output.write(String.format("%s %d %s\r\n", response.getVersion(), response.getStatus().getCode(), response.getStatus().getStatus()).getBytes());
                    if (!response.getHeaderString().isEmpty()) {
                        output.write(response.getHeaderString().getBytes());
                        output.write("\r\n".getBytes());
                    }
                    if (!response.getBody().isEmpty()) {
                        output.write(response.getBody().getBytes());
                    }
                    output.flush();
                } catch (IOException e) {
                    logger.error("Error reading HTTP request: " + e);
                    throw new RuntimeException(e);
                } finally {
                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                        logger.error("Error closing client socket: " + e);
                    }
                }
            });
        }
    }

    public static boolean accept(String acceptHeaderValue, ContentType contentType) {
        String[] mimeTypes = acceptHeaderValue.split(",");
        for (String mimeType : mimeTypes) {
            String[] types = mimeType.trim().split("/");
            String type = types[0];
            String subType = types[1];

            boolean isMatchType = type.equals("*") || type.equals(contentType.type);
            boolean isMatchSubType = subType.equals("*") || subType.equals(contentType.subType);

            if (isMatchType && isMatchSubType) {
                return true;
            }
        }
        return false;
    }
}
