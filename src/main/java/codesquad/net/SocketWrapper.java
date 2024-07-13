package codesquad.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketWrapper {

    private final Socket socket;

    public SocketWrapper(Socket socket) {
        this.socket = socket;
    }

    public InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    public void setKeepAlive(boolean on) {
        try {
            socket.setKeepAlive(on);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void close() throws IOException {
        socket.close();
    }
}
