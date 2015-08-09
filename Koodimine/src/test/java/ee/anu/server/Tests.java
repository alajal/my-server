package ee.anu.server;

import org.junit.Test;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class Tests {

    @Test
    public void testBasicResponse() throws Exception {
        String req = "GET / HTTP/1.1\r\n\r\n"; //inputsreamis
        String resp = "HTTP/1.1 200 OK\r\n\r\nOK"; //outputstreamis

        RequestHandler handler = new RequestHandler() {

            @Override
            public Response handleRequest(String requestURI, Map<String, String> requestParametersMap, byte[] requestData, String method) throws IOException {
                return new Response(Collections.emptyMap(), "OK".getBytes(), Status.OK);
            }

        };

        MockSocket mock = new MockSocket(req);
        MyServer.processRequestAndRespond(mock, handler);
        assertArrayEquals(resp.getBytes(), mock.getResult());
    }

    @Test
    public void testParameterEncoding() throws Exception {
        String fuzz = " =?+ +?= ";
        String req = "GET /test?fuzz=" + URLEncoder.encode(fuzz, "UTF-8") + " HTTP/1.1\r\n\r\n";
        String resp = "HTTP/1.1 200 OK\r\n\r\n" + fuzz;

        RequestHandler handler = new RequestHandler() {
            @Override
            public Response handleRequest(String requestURI, Map<String, String> requestParametersMap, byte[] requestData, String method) throws IOException {
                return new Response(Collections.emptyMap(), requestParametersMap.get("fuzz").getBytes(), Status.OK);
            }

        };

        MockSocket mock = new MockSocket(req);
        MyServer.processRequestAndRespond(mock, handler);
        assertArrayEquals(resp.getBytes(), mock.getResult());
    }

    @Test
    public void testFixedBodyEcho() throws Exception {
        String req = "POST /test HTTP/1.1\r\nContent-Length: 3\r\n\r\nabc";
        String resp = "HTTP/1.1 200 OK\r\n\r\nabc";

        RequestHandler handler = new RequestHandler() {
            @Override
            public Response handleRequest(String requestURI, Map<String, String> requestParametersMap, byte[] requestData, String method) throws IOException {
                return new Response(Collections.emptyMap(), requestData, Status.OK);
            }


        };

        MockSocket mock = new MockSocket(req);
        MyServer.processRequestAndRespond(mock, handler);
        assertArrayEquals(resp.getBytes(), mock.getResult());
    }

    @Test
    public void testInputStreamContigous() throws Exception {
        MockSocket socket = new MockSocket("abc");
        assertEquals("a", String.valueOf((char) socket.getInputStream().read()));
        assertEquals("b", String.valueOf((char) socket.getInputStream().read()));
        assertEquals("c", String.valueOf((char) socket.getInputStream().read()));
        assertEquals(-1, socket.getInputStream().read());
    }
}
