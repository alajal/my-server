package ee.anu.server;

import java.net.Socket;

public class BadRequestApp {

    public static void main(String[] args) throws Exception {
        Socket s = new Socket("localhost", 8080);
        s.getOutputStream().close();
        Thread.sleep(100);
        s.close();
    }

}
