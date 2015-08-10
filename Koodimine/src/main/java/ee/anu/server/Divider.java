package ee.anu.server;

import ee.anu.server.RequestHandler;
import ee.anu.server.Response;

import java.io.IOException;
import java.util.*;

public class Divider implements RequestHandler {
    private final Comparator<Tuple> comparator = new StringLengthComparator();
    private final List<Tuple> wildHandlers = new ArrayList<>(); //list tuleb sortida
    private final Map<String, RequestHandler> fixedHandlers = new HashMap<>();

    private RequestHandler whichHandlerToUse(String requestURI) throws IOException {
        RequestHandler requestHandler = null;

        RequestHandler fixHandler = fixedHandlers.get(requestURI);
        if (fixHandler != null) {
            requestHandler = fixHandler;
        } else {
            for (Tuple wildHandler : wildHandlers) {
                //otsin sama requestURIga tuplit:
                if (requestURI.startsWith(wildHandler.getAttribute())) {
                    requestHandler = wildHandler.getHandler();
                    break;
                }
            }
        }
        return requestHandler;

    }

    @Override
    public Response handleRequest(String requestURI, Map<String, String> requestParametersMap, byte[] requestData, String method) throws IOException {
        return whichHandlerToUse(requestURI).handleRequest(requestURI, requestParametersMap, requestData, method);
    }

    public void addFixedHandler(String attribute, RequestHandler requestHandler) {
        fixedHandlers.put(attribute, requestHandler);
    }

    public void addWildHandler(String attribute, RequestHandler requestHandler) {
        wildHandlers.add(new Tuple(attribute, requestHandler));
        wildHandlers.sort(comparator);  //kuidas saab kõige pikem string ette?
    }
}


//kasutaja võib teha oma mainhandleri ja suvalised teised handlerid. Mainhandleril on meetod addHandler().
//mainhandleris on Map(requestUri: HandlerToUse). Vastavalt võtmele kasutatakse sobivat handlerit,

//patterns - suurem ja pikem pattern on täpsem