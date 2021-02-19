//package xstartup.websocket.data;
//
//import io.netty.channel.Channel;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class XRtClientInstanceCollection {
//    private List<XRtClientInstance> clients = new ArrayList<XRtClientInstance>();
//
//    public List<XRtClientInstance> getClients(){
//        return clients;
//    }
//
//    public XRtClientInstance getClient(String id){
//        for (XRtClientInstance client : clients) {
//            if(client.getId() != null && client.getId().equals(id)){
//                return client;
//            }
//        }
//        return null;
//    }
//
//    public void saveClient(String id, Channel channel){
//        for (XRtClientInstance client : clients) {
//            if(client.getId() != null && client.getId().equals(id)){
//                client.setChannel(channel);
//                return;
//            }
//        }
//        clients.add(new XRtClientInstance(id, channel));
//    }
//}