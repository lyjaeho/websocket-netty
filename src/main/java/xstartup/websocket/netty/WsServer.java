package xstartup.websocket.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import xstartup.websocket.data.WsClientInstance;
import xstartup.websocket.data.WsClientInstanceCollection;
import xstartup.websocket.data.WsData;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

public class WsServer {
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    private int port;
    private boolean started = false;
    private WsChannelInitializer channelInitializer;
    private ChannelFuture serverChannelFuture;
    private ServerSocketChannel serverSocketChannel;

    private WsClientInstanceCollection clientInstanceCollection = new WsClientInstanceCollection();

    public WsClientInstanceCollection getClientInstanceCollection() {
        return clientInstanceCollection;
    }

    private static Map<Integer, WsServer> wsServerMap = new HashMap<>();

    public static WsServer get(int port) {
        for(Map.Entry<Integer, WsServer> entry : wsServerMap.entrySet()) {
            if (entry.getKey().equals(port)) {
                return entry.getValue();
            }
        }
        WsServer wsServer = new WsServer(port);
        wsServerMap.put(port, wsServer);
        return wsServer;
    }

    private WsServer(int port) {
        this.port = port;
    }

    public void start() {
        if(this.started) {
            return;
        }
        this.started = true;
        try {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            channelInitializer = new WsChannelInitializer(this);

            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(592048))//配置固定长度接收缓存区分配器
                    .childHandler(channelInitializer);

            //logger.info("Netty Websocket服务器启动完成已绑定端口:" + port);
            System.out.println("Netty Websocket服务器启动完成已绑定端口:" + port);
            serverChannelFuture = serverBootstrap.bind(port).sync();
            if (serverChannelFuture.isSuccess()) {
                serverSocketChannel = (ServerSocketChannel) serverChannelFuture.channel();
                System.out.println("服务端开启成功");
                //logger.info("服务端开启成功");
            } else {
                //logger.info("服务端开启失败");
                System.out.println("服务端开启失败");
            }

            //等待服务监听端口关闭,就是由于这里会将线程阻塞，导致无法发送信息，所以我这里开了线程
            serverChannelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            //logger.info(e.getMessage());
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            e.printStackTrace();
            this.started = false;
        }
    }

    public void close(){
        serverChannelFuture.channel().close();
        Future<?> bossGroupFuture = bossGroup.shutdownGracefully();
        Future<?> workerGroupFuture = workerGroup.shutdownGracefully();

        this.started = false;
        //
//        try {
//            bossGroupFuture.get;
//            workerGroupFuture.await();
//        } catch (InterruptedException ignore) {
//            ignore.printStackTrace();
//        }
    }

    public boolean isStarted() {
        return started;
    }

    public void requestOneway(String id, Integer command, String content) throws Exception {
        WsClientInstance wsClient = this.clientInstanceCollection.findClient(id);
        if(wsClient == null) {
            throw new Exception("未发现客户端");
        }

        WsData wsData = WsData.build(id, wsClient.getSessionId(), command, content);

        wsClient.getChannel().writeAndFlush(new TextWebSocketFrame(WsData.toJson(wsData)));
    }

    public void resolve(Channel channel, WsData data) {
        if(data.getHeader().getCommand().equals(WsData.SYS_COMMAND_LOGIN_BEGIN)) {
            String id = data.getContent();
            String sessionId = channel.id().asLongText();
            if(id == null || id.length() == 0) {
                System.out.println(String.format("登录失败,未发现id", sessionId));
                WsData wsData = WsData.build(id, sessionId, WsData.SYS_COMMAND_LOGIN_ERROR, "未发现id");
                try {
                    channel.writeAndFlush(new TextWebSocketFrame(WsData.toJson(wsData)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                WsClientInstance wsClient = this.clientInstanceCollection.findClient(id);
                if (wsClient != null && wsClient.isActived()) {
                    System.out.println(String.format("登录失败,已存在客户端%s", wsClient.getId(), wsClient.getSessionId()));
                    WsData wsData = WsData.build(id, sessionId, WsData.SYS_COMMAND_LOGIN_ERROR, "已存在客户端");
                    try {
                        channel.writeAndFlush(new TextWebSocketFrame(WsData.toJson(wsData)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    wsClient = this.clientInstanceCollection.add(data.getHeader().getId(), channel);

                    WsData wsData = WsData.build(data.getContent(), wsClient.getSessionId(), WsData.SYS_COMMAND_LOGIN_SUCCESS, "登录成功");
                    try {
                        channel.writeAndFlush(new TextWebSocketFrame(WsData.toJson(wsData)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
