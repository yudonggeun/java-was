package codesquad;

import codesquad.container.MyContainer;
import codesquad.net.EndPoint;
import codesquad.net.SocketFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws IOException {
        // init
        ServerSocket serverSocket = new ServerSocket(8080);
        ExecutorService executorService = getExecutorService();
        MyContainer container = new MyContainer();
        SocketFactory socketFactory = new SocketFactory();

        EndPoint endPoint = new EndPoint(serverSocket, executorService, socketFactory, container);
        endPoint.start();
    }

    private static ExecutorService getExecutorService() {
        int corePoolSize = 10;
        int maximumPoolSize = 200;
        long keepAliveTime = 5000;
        TimeUnit unit = TimeUnit.MILLISECONDS;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();

        ExecutorService executorService = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        return executorService;
    }
}
