package ee.anu.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class MyServer {

    public static void serve(RequestHandler requestHandler) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            try {
                processRequestAndRespond(clientSocket, requestHandler);
            } catch (IOException se) {
                se.printStackTrace();
            }
            clientSocket.close();
        }
    }

    public static void processRequestAndRespond(Socket clientSocket, RequestHandler requestHandler) throws IOException {
        System.out.println("Connection successful");
        Request bytes = inputStreamToByteArray(clientSocket.getInputStream());
        if (bytes.getHeaders().length < 1) {
            return;
        }
        //read bytes and turn to string till "\r\n"
        String allHeaders = new String(bytes.getHeaders(), 0, bytes.getHeaders().length, "UTF-8");
        //find request URI
        String[] headersArray = allHeaders.split("\r\n");
        String s = headersArray[0];
        String req = s.substring(s.indexOf(" ") + 1, s.lastIndexOf(" "));
        String method = s.substring(0, s.indexOf(" "));
        String requestURI = java.net.URLDecoder.decode(req, "UTF-8");

        Response responseData = requestHandler.handleRequest(requestURI, parseParameters(req), bytes.getRequestBody(),method);
        processRequest(responseData, clientSocket);
    }

    private static Request inputStreamToByteArray(InputStream stream) throws IOException {

        ByteArrayOutputStream headeriteVoolikuOts = new ByteArrayOutputStream();
        ByteArrayOutputStream bodyVoolikuOts = new ByteArrayOutputStream();

        int readFromStream;

        List<Byte> emptyLine = new LinkedList<>();
        readFromStream = stream.read();
        if (readFromStream != -1) {
            headeriteVoolikuOts.write(readFromStream);
            emptyLine.add((byte) readFromStream);

            for (int i = 0; i < 2; i++){
                readFromStream = stream.read();
                headeriteVoolikuOts.write(readFromStream);
                emptyLine.add((byte) readFromStream);
            }

            while (true) {
                if (headeriteVoolikuOts.size() > 100000) {
                    System.out.println("Too much data.");
                }

                readFromStream = stream.read();
                headeriteVoolikuOts.write(readFromStream);
                emptyLine.add((byte) readFromStream);

                boolean bodyExists = false;
                if (emptyLine.get(0) == 13 && emptyLine.get(1) == 10 && emptyLine.get(2) == 13 && emptyLine.get(3) == 10) {
                    System.out.println("Oled jõudnud headerite lõppu");
                    String len = null;
                    byte[] headersBytes = headeriteVoolikuOts.toByteArray();
                    String allHeaders = new String(headersBytes, 0, headersBytes.length, "UTF-8");
                    String[] headersArray = allHeaders.split("\r\n");
                    for (String aHeadersArray : headersArray) {
                        if (aHeadersArray.contains("Content-Length")) {
                            System.out.println("Päringu body on olemas.");
                            String[] conLenHeader = aHeadersArray.split(" ");
                            len = conLenHeader[1];
                            bodyExists = true;
                        }
                    }
                    if (bodyExists) {
                        for (int i = 0; i < Integer.parseInt(len); i++) {
                            readFromStream = stream.read();
                            bodyVoolikuOts.write(readFromStream);
                        }
                    }
                    break;
                }
                emptyLine.remove(0);
            }
            //transfer encoding
        }

        return new Request(headeriteVoolikuOts.toByteArray(), bodyVoolikuOts.toByteArray());
    }

    private static void processRequest(Response responseData, Socket clientSocket) throws IOException {
        serverResponse(clientSocket.getOutputStream(), responseData.status, responseData.getHeaders(), responseData.getData());
    }

    private static Map<String, String> parseParameters(String requestURI) throws UnsupportedEncodingException {
        Map<String, String> requestParametersMap = new HashMap<>();

        if (requestURI.contains("?")) {
            String[] requestURISplitted = requestURI.split("\\?");

            String[] requestParametersSplitted = requestURISplitted[1].split("&");
            for (String keyValuePair : requestParametersSplitted) {
                String[] keyAndValue = keyValuePair.split("=");

                for (int i = 0; i < keyAndValue.length; i++) {
                    if (keyAndValue[i].contains("+")) {
                        keyAndValue[i] = java.net.URLDecoder.decode(keyAndValue[i], "UTF-8");
                    }
                }
                String key = keyAndValue[0];
                String value = keyAndValue[1];

                requestParametersMap.put(key, value);
            }

        }
        return requestParametersMap;
    }

    private static void serverResponse(OutputStream outputStream, Status status, Map<String, String> headers, byte[] dataBytes) throws IOException {
        outputStream.write(("HTTP/1.1 " + status.getCode() + " " + status.getMessage() + "\r\n").getBytes("UTF-8"));
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            outputStream.write((entry.getKey() + ": " + entry.getValue() + "\r\n").getBytes("UTF-8"));
        }
        outputStream.write("\r\n".getBytes("UTF-8"));
        outputStream.write(dataBytes);
        outputStream.close();
    }

}
