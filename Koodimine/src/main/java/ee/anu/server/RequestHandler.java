package ee.anu.server;

import java.io.IOException;
import java.util.Map;

public interface RequestHandler {

    Response handleRequest(String requestURI, Map<String, String> requestParametersMap, byte[] requestData, String method) throws IOException;

}
