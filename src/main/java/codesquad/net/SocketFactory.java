package codesquad.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketFactory {


    public Socket acceptSocket(ServerSocket serverSocket) throws IOException {
        return serverSocket.accept();
    }
}
