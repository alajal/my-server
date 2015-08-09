package ee.anu.server;

import java.io.*;
import java.net.Socket;

public class MockSocket extends Socket {

    final ByteArrayOutputStream out;
    final ByteArrayInputStream in;

    public MockSocket(String req) throws UnsupportedEncodingException {
        this.out = new ByteArrayOutputStream();
        this.in = new ByteArrayInputStream(req.getBytes("UTF-8"));
    }

    @Override
    public synchronized void close() throws IOException {
        super.close();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return out;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return in;
    }

    public byte[] getResult() {
        return out.toByteArray();
    }
}
