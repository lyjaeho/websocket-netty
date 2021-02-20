package xstartup.websocket.data;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WsClientInstanceCollection {
    private List<WsClientInstance> clients = new ArrayList<WsClientInstance>();

    public List<WsClientInstance> getClients(){
        return clients;
    }

    public WsClientInstance findClient(String id){
        for (WsClientInstance client : clients) {
            if(client.getId() != null && client.getId().equals(id)){
                return client;
            }
        }
        return null;
    }

    public synchronized WsClientInstance add(String id, Channel channel){
        for (WsClientInstance client : clients) {
            if(client.getId() != null && client.getId().equals(id)){
                client.setChannel(channel);
                return client;
            }
        }
        WsClientInstance client = new WsClientInstance(id, channel.id().asLongText(), channel);
        clients.add(client);
        return client;
    }

    public synchronized  void removeByChannel(Channel channel) {
        Iterator<WsClientInstance> iterator = clients.iterator();
        while(iterator.hasNext()) {
            WsClientInstance client = iterator.next();
            if(client.getChannel().equals(channel)) {
                iterator.remove();
                System.out.println(String.format("remove client by channel:%s", channel.id().asLongText()));
            }
        }
    }
}