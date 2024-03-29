package wwz.netty.handler;


import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import wwz.netty.util.Response;
import wwz.netty.handler.param.ServerRequest;
import wwz.netty.medium.Media;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class ServerHandler extends ChannelInboundHandlerAdapter {
private static final Executor exec = Executors.newFixedThreadPool(10);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //ctx.channel().writeAndFlush("is ok\r\n");
//        ServerRequest request = JSONObject.parseObject(msg.toString(), ServerRequest.class);

//        Media media = Media.newInstance();
//        Response result = media.process(request);
//
//        ctx.channel().writeAndFlush(JSONObject.toJSONString(result));
//        ctx.channel().writeAndFlush("\r\n");
        exec.execute(new Runnable() {
            @Override
            public void run() {
                ServerRequest serverRequest = JSONObject.parseObject(msg.toString(), ServerRequest.class);
//                System.out.println(serverRequest.getCommand());
                Media media = Media.newInstance();//生成中介者模式
                Response response = media.process(serverRequest);
                //向客户端发送Resonse
                ctx.channel().writeAndFlush(JSONObject.toJSONString(response) + "\r\n");
            }
        });
    }

//    @Override
//    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        if (evt instanceof IdleStateEvent) {
//            IdleStateEvent event = (IdleStateEvent) evt;
//            if (event.state().equals(IdleState.READER_IDLE)) {
//                System.out.println("读空闲");
//                ctx.channel().close();
//            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
//                System.out.println("写空闲");
//            } else if (event.state().equals(IdleState.ALL_IDLE)) {
//                System.out.println("读写空闲");
//                ctx.channel().writeAndFlush("ping\r\n");
//            }
//        }
//    }
}
