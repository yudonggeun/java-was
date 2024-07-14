package codesquad;

import codesquad.config.FilterConfig;
import codesquad.container.MyContainer;
import codesquad.context.ApplicationContext;
import codesquad.net.EndPoint;
import codesquad.net.SocketFactory;

import java.io.IOException;
import java.net.ServerSocket;

public class Main {

    public static void main(String[] args) throws IOException {
        // init
        ApplicationContext context = new ApplicationContext();
        context.scan("codesquad");

        ServerSocket serverSocket = new ServerSocket(8080);
        MyContainer container = new MyContainer(context.getSoloObject(FilterConfig.class));
        SocketFactory socketFactory = new SocketFactory();

        EndPoint endPoint = new EndPoint(serverSocket, socketFactory, container);
        endPoint.start();
    }
}
