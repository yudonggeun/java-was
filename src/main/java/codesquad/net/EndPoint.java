package codesquad.net;

import codesquad.container.MyContainer;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class EndPoint {

    private final Logger logger = LoggerFactory.getLogger(EndPoint.class);

    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    private final SocketFactory socketFactory;
    private final MyContainer container;
    private final boolean isRunning = true;

    public EndPoint(ServerSocket serverSocket, ExecutorService executorService, SocketFactory socketFactory, MyContainer container) {
        this.serverSocket = serverSocket;
        this.executorService = executorService;
        this.socketFactory = socketFactory;
        this.container = container;
    }

    public void start() {
        logger.info("Listening for connection on port {} ....", serverSocket.getLocalPort());
        executorService.submit(new Acceptor());
    }

    private class Acceptor implements Runnable {
        @Override
        public void run() {
            if (serverSocket == null) {
                return;
            }

            while (isRunning) {
                try {
                    Socket socket = socketFactory.acceptSocket(serverSocket);
                    executorService.submit(new Processor(socket));
                } catch (IOException ioe) {
                    logger.error("Failed to accept socket", ioe);
                    return;
                }
            }
        }
    }

    public class Processor implements Runnable {

        private final Socket socket;

        public Processor(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    InputStream input = socket.getInputStream();
                    OutputStream output = socket.getOutputStream()
            ) {
                // read request
                HttpRequest request = new HttpRequest(input);

                // run container
                HttpResponse response = container.doRun(request);

                // write response
                output.write(String.format("%s %d %s\r\n", response.getVersion(), response.getStatus().getCode(), response.getStatus().getStatus()).getBytes());
                if (!response.getHeaderString().isEmpty()) {
                    output.write(response.getHeaderString().getBytes());
                    output.write("\r\n".getBytes());
                }
                if (response.getBody() != null) {
                    output.write(response.getBody());
                }
                output.flush();
            } catch (IOException e) {
                logger.error("Error reading HTTP request: " + e);
                e.printStackTrace();
            } catch (Exception e) {
                logger.error("Error processing HTTP request: " + e);
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.error("Error closing client socket: " + e);
                }
            }
        }
    }
}
