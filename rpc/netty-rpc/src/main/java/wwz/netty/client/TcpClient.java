package wwz.netty.client;

import com.alibaba.fastjson.JSONObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import wwz.netty.handler.SimpleClientHandler;
import wwz.netty.util.Response;


public class TcpClient {
    static final Bootstrap bootstrap = new Bootstrap();
    static ChannelFuture future = null;
    static {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        //只需要保证解码器在入站handler前，编码在出站handler前
                        channel.pipeline().addLast(new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()[0]));
                        channel.pipeline().addLast(new StringDecoder());
                        channel.pipeline().addLast(new StringEncoder());
                        channel.pipeline().addLast(new SimpleClientHandler());
                    }
                });
        String host = "localhost";
        int port = 8081;
        try {
            future = bootstrap.connect(host, port).sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
            workerGroup.shutdownGracefully();
        }
//       finally {
//
//        }
    }

    public static Response send(ClientRequest request) {
        future.channel().writeAndFlush(JSONObject.toJSONString(request));
        future.channel().writeAndFlush("\r\n");
        ResultFuture df = new ResultFuture(request);
        return df.get();
    }
}
