package ee.anu.server;

public class Request {
    byte[] headers;
    byte[] RequestBody;

    public Request(byte[] headers, byte[] requestBody) {
        this.headers = headers;
        RequestBody = requestBody;
    }

    public byte[] getHeaders() {
        return headers;
    }

    public byte[] getRequestBody() {
        return RequestBody;
    }
}
