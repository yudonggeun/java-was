package codesquad;

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
import java.util.concurrent.*;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final Set<HttpHandler> httpHandlers = new LinkedHashSet<>();

    public static void main(String[] args) throws IOException {
        // handler 등록
        httpHandlers.add(new StaticResourceHandler());

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
                try {
                    HttpRequest request = new HttpRequest(clientSocket.getInputStream());
                    HttpResponse response = null;

                    logger.info("Request[method={}, host={}, path={}, headers={}, bod={}]",
                            request.method,
                            request.getHeader("Host"),
                            request.path,
                            request.getHeaders(),
                            request.getBody()
                    );

                    OutputStream output = clientSocket.getOutputStream();
                    // handler 실행
                    for (HttpHandler httpHandler : httpHandlers) {
                        if (httpHandler.match(request)) {
                            response = httpHandler.doRun(request);
                            break;
                        } else {
                            response = HttpResponse.of(HttpStatus.NOT_FOUND);
                        }
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
//                    File resource = new File("src/main/resources/static");
//                    File file = new File(resource, request.path);
//
//                    if (file.exists()) {
//                        // extract file extension
//                        String fileName = file.getName();
//                        int dotIndex = fileName.lastIndexOf('.');
//                        String extension = fileName.substring(dotIndex + 1);
//                        ContentType contentType = switch (dotIndex) {
//                            case -1 -> ContentType.TEXT_PLAIN;
//                            default -> ContentType.of(extension);
//                        };
//
//                        HttpResponse resp = HttpResponse.of(HttpStatus.OK);
//
//                        try (
//                                BufferedReader reader = new BufferedReader(new FileReader(file))
//                        ) {
//                            String line;
//
//                            if (!accept(request.getHeader("Accept"), contentType)) {
//                                output.write("HTTP/1.1 406 Not Acceptable\r\n".getBytes());
//                                logger.error("Not Acceptable");
//                            } else {
//                                logger.debug("Reading from file and writing to output");
//                                output.write(("HTTP/1.1 200 OK\r\n".getBytes()));
//
//                                // header
//                                output.write(("Content-Type: " + contentType.fullType + "\r\n").getBytes());
//                                output.write("\r\n".getBytes());
//                                // body
//                                while ((line = reader.readLine()) != null) {
//                                    output.write(line.getBytes());
//                                }
//                            }
//                            output.write(String.format("%s %d %s\r\n", resp.getVersion(), resp.getStatus().getCode(), resp.getStatus().getStatus()).getBytes());
//                            output.write(resp.getHeaderString().getBytes());
//                            output.write("\r\n".getBytes());
//                            output.write(resp.getBody().getBytes());
//                            output.flush();
//                        } catch (IOException e) {
//                            logger.error("Error reading from file or writing to output: " + e);
//                            throw new RuntimeException(e);
//                        }
//                    } else {
//                        // HTTP 응답을 생성합니다.
//                        OutputStream clientOutput = clientSocket.getOutputStream();
//                        clientOutput.write("HTTP/1.1 200 OK\r\n".getBytes());
//                        clientOutput.write("Content-Type: text/html\r\n".getBytes());
//                        clientOutput.write("\r\n".getBytes());
//                        clientOutput.write(("<h1>OK</h1>\r\n").getBytes());
//                        clientOutput.flush();
//                    }
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
