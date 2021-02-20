package test;

import xstartup.websocket.data.WsClientInstance;
import xstartup.websocket.netty.WsServer;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class DemoApplication {
    public static void main(String[] args) throws IOException {
        System.out.println("testdata");

        new Thread(()-> {
            WsServer.get(18800).start();
        }).start();

        while(System.in.read() != 'e') {
            List<WsClientInstance> clients = WsServer.get(18800).getClientInstanceCollection().getClients();
            for(WsClientInstance client : clients) {
                try {
                    WsServer.get(18800).requestOneway(client.getId(), 333, "test mesage" + new Date().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
