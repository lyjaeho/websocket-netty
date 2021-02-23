package xstartup.websocket.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import xstartup.websocket.data.WsData;

public class WsServerHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

//    private static final Logger LOGGER = LoggerFactory.getLogger(WsServerHandler.class);
//
//    @Autowired
//    private ChatService chatService;
    private WsServer wsServer;

    public WsServerHandler(WsServer wsServer) {
        this.wsServer = wsServer;
    }

    /**
     * 描述：读取完连接的消息后，对消息进行处理。
     *      这里主要是处理WebSocket请求
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
        handlerWebSocketFrame(ctx, msg);
    }

    /**
     * 描述：处理WebSocketFrame
     * @param ctx
     * @param frame
     * @throws Exception
     */
    private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        if (frame instanceof CloseWebSocketFrame) {
            // 关闭请求
            System.out.println(String.format("client close channel:%s", ctx.channel().id().asLongText()));
            this.wsServer.getClientInstanceCollection().removeByChannel(ctx.channel());
            return;
        } else if (frame instanceof PingWebSocketFrame) {
            // ping请求
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        } else if (!(frame instanceof TextWebSocketFrame)) {
            // 只支持文本格式，不支持二进制消息
            System.out.println("仅支持文本(Text)格式，不支持二进制消息");
        } else {
            // 客服端发送过来的消息
            String request = ((TextWebSocketFrame) frame).text();
            if (request != null && WsData.HEARTBEAT.equals(request)) {
                System.out.println(request);
                return;
            }
            System.out.println("receive:" + request);
            WsData wsData = WsData.fromJson(request);
            this.wsServer.resolve(ctx.channel(), wsData);
        }
    }

    /**
     * 描述：客户端断开连接
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //chatService.remove(ctx);
        System.out.println(String.format("client disconnect channel:%s", ctx.channel().id().asLongText()));
        this.wsServer.getClientInstanceCollection().removeByChannel(ctx.channel());
    }

    /**
     * 异常处理：关闭channel
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();

        ctx.close();
    }
}