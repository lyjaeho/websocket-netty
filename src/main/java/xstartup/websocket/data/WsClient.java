package xstartup.websocket.data;

import io.netty.channel.socket.ServerSocketChannel;

public class WsClient {
    private String id;
    private ServerSocketChannel channel;

    public WsClient(String id, ServerSocketChannel channel) {
        this.id = id;
        this.channel = channel;
    }

    public String getId() {
        return id;
    }

    public ServerSocketChannel getChannel() {
        return channel;
    }
}
