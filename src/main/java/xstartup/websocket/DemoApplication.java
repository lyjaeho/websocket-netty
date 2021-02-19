package xstartup.websocket;

import xstartup.websocket.netty.WsServer;

import java.io.IOException;

public class DemoApplication {
    public static void main(String[] args) throws IOException {
        System.out.println("testdata");

        new Thread(()-> {
            new WsServer().start();
        }).start();

        System.in.read();
    }
}
