package codesquad;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080); // 8080 포트에서 서버를 엽니다.
        System.out.println("Listening for connection on port 8080 ....");

        ExecutorService executorService = Executors.newFixedThreadPool(10); // 10개의 스레드를 가진 스레드 풀을 생성합니다.

        while (true) { // 무한 루프를 돌며 클라이언트의 연결을 기다립니다.
            Socket clientSocket = serverSocket.accept(); // 클라이언트 연결을 수락합니다.

            executorService.submit(() -> { // 클라이언트 연결을 스레드 풀에 제출합니다.
                try {
                    // HTTP 응답을 생성합니다.
                    OutputStream clientOutput = clientSocket.getOutputStream();
                    clientOutput.write("HTTP/1.1 200 OK\r\n".getBytes());
                    clientOutput.write("Content-Type: text/html\r\n".getBytes());
                    clientOutput.write("\r\n".getBytes());
                    clientOutput.write(("<h1>Hello</h1>\r\n").getBytes());
                    clientOutput.flush();
                } catch (IOException e) {
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
