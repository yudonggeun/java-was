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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class EndPoint {

    private final int maxConnections = 5000;
    private final ConcurrentLinkedQueue<SocketWrapper> socketQueue = new ConcurrentLinkedQueue<>();
    // connection pool config
    private final int corePoolSize = 10;
    private final int maximumPoolSize = 200;

    private final Logger logger = LoggerFactory.getLogger(EndPoint.class);

    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    private final SocketFactory socketFactory;
    private final MyContainer container;
    private final boolean isRunning = true;
    private final long keepAliveTime = 5000;
    private final AtomicInteger currentThreadCount = new AtomicInteger(0);

    public EndPoint(ServerSocket serverSocket, SocketFactory socketFactory, MyContainer container) {
        this.serverSocket = serverSocket;
        this.executorService = getExecutorService();
        this.socketFactory = socketFactory;
        this.container = container;
    }

    public void start() {
        logger.info("Listening for connection on port {} ....", serverSocket.getLocalPort());
        currentThreadCount.incrementAndGet();
        executorService.submit(new Acceptor());
        executorService.submit(new Poller());
    }

    private SocketWrapper pollSocket() {
        SocketWrapper socket = socketQueue.poll();
        if (socket != null) {
            currentThreadCount.decrementAndGet();
        }
        return socket;
    }

    private void pushSocket(SocketWrapper socketWrapper) {
        synchronized (socketQueue) {
            if (maxConnections >= socketQueue.size()) {
                socketQueue.add(socketWrapper);
            }
        }
    }

    private ExecutorService getExecutorService() {
        TimeUnit unit = TimeUnit.MILLISECONDS;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();

        ExecutorService executorService = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        return executorService;
    }

    private class Acceptor implements Runnable {
        @Override
        public void run() {
            if (serverSocket == null) {
                return;
            }

            while (isRunning) {
                try {
                    SocketWrapper socket = socketFactory.acceptSocket(serverSocket);
                    pushSocket(socket);
                } catch (IOException ioe) {
                    logger.error("Failed to accept socket", ioe);
                    return;
                }
            }
        }
    }

    private class Poller implements Runnable {

        @Override
        public void run() {
            while (isRunning) {
                SocketWrapper socket = socketQueue.peek();
                if (socket == null) continue;
                try {
                    if (socket.isClosed()) {
                        pollSocket();
                    } else if (socket.isTimeout()) {
                        socket = pollSocket();
                        socket.close();
                    } else if (socket.isOpen() || socket.getInputStream().available() > 0) {
                        executorService.submit(new Processor(pollSocket()));
                        currentThreadCount.incrementAndGet();
                    } else {
                        pushSocket(pollSocket());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private class Processor implements Runnable {

        private final SocketWrapper socket;

        public Processor(SocketWrapper socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                InputStream input = socket.getInputStream();
                OutputStream output = socket.getOutputStream();

                // read request
                HttpRequest request = new HttpRequest(input);

                if (request.version.equals("HTTP/1.1") ||
                    request.getHeader("Connection").equals("keep-alive")) {
                    socket.setKeepAliveTimeout(5000);
                }

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

                socket.setStatus(SocketStatus.LONG);

                if (!socket.isTimeout()) {
                    System.out.println(request.path + ": " + socket.getPort());
                    pushSocket(socket);
                } else {
                    socket.close();
                }
            } catch (Exception e) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                logger.error("Error reading HTTP request: " + e);
                e.printStackTrace();
            }
        }
    }
}
