package xstartup.websocket.data;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Date;

public class WsClientInstance {
    private String id;

    private String sessionId;

    private Channel channel;

    private Date latestActiveTime;

    /**
     * 获取客户端最后一次活跃时间
     * @return
     */
    public Date getLatestActiveTime(){
        return latestActiveTime;
    }

    /**
     * 判断客户端是否是激活状态
     * @return
     */
    public boolean isActived(){
        if(this.channel!=null && this.channel.isActive() && this.channel.isOpen()){
            return true;
        }
        return false;
    }

    public WsClientInstance(String id, String sessionId, Channel channel) {
        this.id = id;
        this.sessionId = sessionId;
        this.channel = channel;
        this.latestActiveTime = new Date();
    }

    /**
     * @return the userId
     */
    public String getId() {
        return id;
    }

    public String getSessionId() {
        return sessionId;
    }

    /**
     * @return the channel
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * 获取客户端ip
     * @return
     */
    public String getIp() {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) this.channel.remoteAddress();
        if (inetSocketAddress == null) {
            return "0.0.0.0";
        }
        return inetSocketAddress.getAddress().getHostAddress();
    }

    /**
     * @param channel the channel to set
     */
    public void setChannel(Channel channel) {
        this.channel = channel;
        this.sessionId = this.channel.id().asLongText();
        this.latestActiveTime = new Date();
    }

//    private XRtRequestCollection clientRequestCollection = new XRtRequestCollection();
//
//    public XRtRequest request(XContext context, XRtData data) {
//        XRtRequest clientRequest = null;
//        clientRequest = clientRequestCollection.findRequestAndRemoveCompleted(data);
//        if (clientRequest != null) {
//            return clientRequest;
//        }
//        clientRequest = new XRtRequest(data);
//        clientRequestCollection.addRequest(clientRequest);
//        clientRequest.begin();
//
//        byte[] bytes = XRtData.getPackageBuffer(context, data);
//        ByteBuf byteBuf = Unpooled.buffer(bytes.length);
//        byteBuf.writeBytes(bytes);
//        this.channel.writeAndFlush(byteBuf);
//        this.latestActiveTime = new Date();
//        return clientRequest;
//    }
//
//
//    public void requestOneway(XContext context, XRtData data) {
//        byte[] bytes = XRtData.getPackageBuffer(context, data);
//        ByteBuf byteBuf = Unpooled.buffer(bytes.length);
//        byteBuf.writeBytes(bytes);
//        this.channel.writeAndFlush(byteBuf);
//        this.latestActiveTime = new Date();
//    }
//
//    public boolean response(XRtData data) {
//        if(data.getReplyHeader() == null){
//            return false;
//        }
//        XRtRequest clientRequest = clientRequestCollection.findReplayRequest(data.getReplyHeader());
//        if (clientRequest == null) {
//            //logger.info("data is time out client userId:" + data.getOuCode());
//            return false;
//        } else {
//            //logger.info("找到客户端clientRequest");
//            this.latestActiveTime = new Date();
//            clientRequest.response(XRtRequestStatus.OK, data);
//            return true;
//        }
//    }
}