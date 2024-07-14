package codesquad.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public class SocketWrapper {

    private final Socket socket;
    private SocketStatus status;
    private long deadline;

    public SocketWrapper(Socket socket, SocketStatus status) {
        this.socket = socket;
        this.status = status;
        this.deadline = 0;
    }

    public void setStatus(SocketStatus status) {
        this.status = status;
    }

    public void setKeepAliveTimeout(long timeout) {
        this.deadline = System.currentTimeMillis() + timeout;
    }

    public InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    public boolean isKeepAlive() {
        try {
            return socket.getKeepAlive();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() throws IOException {
        socket.close();
        this.status = SocketStatus.CLOSE;
    }

    public boolean isTimeout() {
        return this.status == SocketStatus.LONG && System.currentTimeMillis() > deadline;
    }

    public InetAddress getInetAddress() {
        return socket.getInetAddress();
    }

    public int getPort() {
        return socket.getPort();
    }

    public boolean isClosed() {
        return this.status == SocketStatus.CLOSE;
    }

    public boolean isOpen() {
        return this.status == SocketStatus.OPEN;
    }
}
