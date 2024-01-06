package wwz.handler;


import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import wwz.core.ResultFuture;
import wwz.param.Response;


public class SimpleClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if ("ping".equals(msg)) {
            ctx.channel().writeAndFlush("ping\r\n");
            return;
        }

        Response response = JSONObject.parseObject(msg.toString(), Response.class);
        ResultFuture.receive(response);
        //ctx.channel().attr(AttributeKey.valueOf("message")).set(msg);
        //ctx.channel().close();
    }
}
