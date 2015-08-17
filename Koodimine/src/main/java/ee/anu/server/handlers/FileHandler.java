package ee.anu.server.handlers;

import ee.anu.server.RequestHandler;
import ee.anu.server.Response;
import ee.anu.server.Status;

import java.io.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class FileHandler implements RequestHandler {
    final String root;
    final Properties types;
    final String mapPattern;


    @Override
    public Response handleRequest(String requestURI, Map<String, String> requestParametersMap, byte[] requestData, String method) throws IOException {
        String path;
        String mappedString = requestURI.substring(mapPattern.length());

        if (root.substring(root.length() - 1).equals("/") || Objects.equals(Character.toString(mappedString.charAt(0)), "/")){
            path = root + mappedString;
        } else {
            path = root + "/" + mappedString;
        }

        InputStream in = FileHandler.class.getClassLoader().getResourceAsStream(path);
        Map<String, String> headers = new HashMap<>();
        byte[] data;
        if (in != null){
            int readFromStream = 0;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            while (readFromStream != -1) {
                readFromStream = in.read();
                if (readFromStream != -1){
                    out.write(readFromStream);
                }
            }
            data = out.toByteArray();

            if (requestURI.contains(".")){
                String possibleType = requestURI.substring(requestURI.lastIndexOf(".") + 1);
                String fileType = types.getProperty(possibleType);
                if (fileType != null) {
                    headers.put("Content-Type", fileType);
                } else {
                    headers.put("Content-Type", "application/octet-stream");
                }
            } else {
                headers.put("Content-Type", "application/octet-stream");
            }

            headers.put("Content-Length", String.valueOf(data.length));
            return new Response(headers, data, Status.OK);
        } else {
            data = new byte[0];
            headers.put("Content-Length", String.valueOf(data.length));
            return new Response(headers, data, Status.NotFound);
        }
    }

    public FileHandler(String root, String mapPatern) throws IOException {
        this.mapPattern = mapPatern;
        this.root = root;
        this.types = new Properties();
        try (InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("content-types.properties")) {  //autoclosable
            types.load(resourceAsStream);
        }
    }

}
