package codesquad;

import codesquad.http.ContentType;
import codesquad.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
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
            Socket clientSocket = serverSocket.accept();

            executorService.submit(() -> {
                final Logger logger = LoggerFactory.getLogger(Thread.currentThread().getName());
                try {
                    HttpRequest request = new HttpRequest(clientSocket.getInputStream());
                    logger.info("Request[method={}, host={}, path={}, headers={}, bod={}]",
                            request.method,
                            request.getHeader("Host"),
                            request.path,
                            request.getHeaders(),
                            request.getBody()
                    );

                    File resource = new File("src/main/resources/static");
                    File file = new File(resource, request.path);

                    if (file.exists()) {
                        // extract file extension
                        String fileName = file.getName();
                        int dotIndex = fileName.lastIndexOf('.');
                        String extension = fileName.substring(dotIndex + 1);
                        ContentType contentType = switch (dotIndex) {
                            case -1 -> ContentType.TEXT_PLAIN;
                            default -> ContentType.of(extension);
                        };

                        var output = clientSocket.getOutputStream();
                        BufferedReader reader = new BufferedReader(new FileReader(file));

                        try {
                            String line;

                            if (!accept(request.getHeader("Accept"), contentType)) {
                                output.write("HTTP/1.1 406 Not Acceptable\r\n".getBytes());
                                logger.error("Not Acceptable");
                            } else {
                                logger.debug("Reading from file and writing to output");
                                output.write(("HTTP/1.1 200 OK\r\n".getBytes()));

                                // header
                                output.write(("Content-Type: " + contentType.fullType + "\r\n").getBytes());
                                output.write("\r\n".getBytes());
                                // body
                                while ((line = reader.readLine()) != null) {
                                    output.write(line.getBytes());
                                }
                            }
                            output.flush();
                        } catch (IOException e) {
                            logger.error("Error reading from file or writing to output: " + e);
                            throw new RuntimeException(e);
                        }
                    } else {
                        // HTTP 응답을 생성합니다.
                        OutputStream clientOutput = clientSocket.getOutputStream();
                        clientOutput.write("HTTP/1.1 200 OK\r\n".getBytes());
                        clientOutput.write("Content-Type: text/html\r\n".getBytes());
                        clientOutput.write("\r\n".getBytes());
                        clientOutput.write(("<h1>OK</h1>\r\n").getBytes());
                        clientOutput.flush();
                    }
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
