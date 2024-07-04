package codesquad.net;

import java.util.concurrent.ExecutorService;

public class EndPoint {

    private final ExecutorService executorService;

    public EndPoint(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public static class Acceptor implements Runnable {
        @Override
        public void run() {

        }
    }

    public static class Processor implements Runnable {
        @Override
        public void run() {

        }
    }
}
