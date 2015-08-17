package ee.anu.server;

import java.io.IOException;
import java.util.*;

public class Divider implements RequestHandler {
    private final Comparator<Tuple> comparator = new StringLengthComparator();
    private final List<Tuple> wildHandlers = new ArrayList<>(); //list tuleb sortida
    private final Map<String, RequestHandler> fixedHandlers = new HashMap<>();

    private RequestHandler findHandlerToUse(String requestURI) {
        RequestHandler fixHandler = fixedHandlers.get(requestURI);
        if (fixHandler != null) {
            return fixHandler;
        }

        for (Tuple wildHandler : wildHandlers) {
            //otsin sama requestURIga tuplit:
            if (requestURI.startsWith(wildHandler.getAttribute())) {
                return wildHandler.getHandler();
            }
        }

        return null;
    }

    @Override
    public Response handleRequest(String requestURI, Map<String, String> requestParametersMap, byte[] requestData, String method) throws IOException {
        return findHandlerToUse(requestURI).handleRequest(requestURI, requestParametersMap, requestData, method);
    }

    public void addFixedHandler(String attribute, RequestHandler requestHandler) {
        fixedHandlers.put(attribute, requestHandler);
    }

    public void addWildHandler(String attribute, RequestHandler requestHandler) {
        wildHandlers.add(new Tuple(attribute, requestHandler));
        wildHandlers.sort(comparator);  //kuidas saab kõige pikem string ette?
    }

    private static class StringLengthComparator implements Comparator<Tuple> {
        @Override
        public int compare(Tuple o1, Tuple o2) {
            return -Integer.compare(
                    o1.getAttribute().length(),
                    o2.getAttribute().length());
        }
    }

    private static class Tuple {
        private final String attribute;
        private final RequestHandler handler;

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
}
