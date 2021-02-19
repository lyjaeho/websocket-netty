package xstartup.websocket.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.Future;

public class WsServer {
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    private int port;
    private WsChannelInitializer channelInitializer = new WsChannelInitializer();
    private ChannelFuture serverChannelFuture;

    public void start(int port) {
        this.port = port;
        try {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(592048))//配置固定长度接收缓存区分配器
                    .childHandler(channelInitializer);

            //logger.info("Netty Websocket服务器启动完成已绑定端口:" + port);

            serverChannelFuture = serverBootstrap.bind(port).sync();
        } catch (Exception e) {
            //logger.info(e.getMessage());
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            e.printStackTrace();
        }
    }


    public void close(){
        serverChannelFuture.channel().close();
        Future<?> bossGroupFuture = bossGroup.shutdownGracefully();
        Future<?> workerGroupFuture = workerGroup.shutdownGracefully();
//
//        try {
//            bossGroupFuture.get;
//            workerGroupFuture.await();
//        } catch (InterruptedException ignore) {
//            ignore.printStackTrace();
//        }
    }
}
