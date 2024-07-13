package codesquad.net;

import java.io.IOException;
import java.net.ServerSocket;

public class SocketFactory {

    public SocketWrapper acceptSocket(ServerSocket serverSocket) throws IOException {
        return new SocketWrapper(serverSocket.accept());
    }
}
