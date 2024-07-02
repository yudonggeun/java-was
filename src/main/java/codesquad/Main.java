package codesquad;

import codesquad.filter.AcceptHeaderFilter;
import codesquad.filter.Filter;
import codesquad.filter.HttpLoggingFilter;
import codesquad.handler.HttpHandler;
import codesquad.handler.StaticResourceHandler;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.*;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final List<Filter> filters = new ArrayList<>();
    private static final Set<HttpHandler> httpHandlers = new LinkedHashSet<>();

    public static void main(String[] args) throws IOException {
        // init start
        // handler 등록
        httpHandlers.add(new StaticResourceHandler());
        // filter 등록
        filters.add(new AcceptHeaderFilter());
        filters.add(new HttpLoggingFilter());
        Collections.sort(filters);

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
        // init end

        while (true) {
            var clientSocket = serverSocket.accept();
            executorService.submit(() -> {

                final Logger logger = LoggerFactory.getLogger(Thread.currentThread().getName());
                try (
                        InputStream input = clientSocket.getInputStream();
                        OutputStream output = clientSocket.getOutputStream()
                ) {
                    HttpRequest request = new HttpRequest(input);
                    HttpResponse response = null;

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
                    for (int i = filters.size() - 1; i >= 0; i--) {
                        filters.get(i).postHandle(request, response);
                    }

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
}
