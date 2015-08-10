package ee.anu.server;

import ee.anu.server.RequestHandler;

public class Tuple {
    public final String attribute;
    public final RequestHandler handler;

    public Tuple(String attribute, RequestHandler handler) {
        this.attribute = attribute;
        this.handler = handler;
    }

    public String getAttribute() {
        return attribute;
    }

    public RequestHandler getHandler() {
        return handler;
    }
}
