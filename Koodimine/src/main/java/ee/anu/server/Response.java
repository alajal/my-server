package ee.anu.server;

import java.util.Map;

public class Response {
    Map<String, String> headers;
    byte[] data;
    Status status;

    public Response(Map<String, String> headers, byte[] data, Status status) {
        this.headers = headers;
        this.data = data;
        this.status = status;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getData() {
        return data;
    }
}
