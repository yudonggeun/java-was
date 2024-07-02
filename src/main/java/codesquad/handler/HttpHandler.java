package codesquad.handler;

public interface HttpHandler extends Runnable {

    @Override
    default void run() {
    }
}
