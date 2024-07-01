package codesquad;

import codesquad.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Listening for connection on port 8080 ....");

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        while (true) {
            Socket clientSocket = serverSocket.accept();

            executorService.submit(() -> {
                final Logger logger = LoggerFactory.getLogger(Thread.currentThread().getName());
                try {
                    HttpRequest request = new HttpRequest(clientSocket.getInputStream());

                    if (request.path.equals("/index.html")) {
                        File file = new File("./src/main/resources/static/index.html");
                        var output = clientSocket.getOutputStream();
                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        try {
                            String line;
                            logger.debug("Reading from file and writing to output");
                            output.write("""
                                    HTTP/1.1 200 OK\r
                                    Content-Type: text/html\r
                                    \r
                                    """.getBytes());
                            while ((line = reader.readLine()) != null) {
                                output.write(line.getBytes());
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
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
