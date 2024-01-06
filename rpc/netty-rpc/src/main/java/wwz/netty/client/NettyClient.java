package wwz.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import wwz.netty.handler.SimpleClientHandler;

public class NettyClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 18080;
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstap = new Bootstrap();
            bootstap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
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
            ChannelFuture future = bootstap.connect(host, port).sync();
//            future.channel().writeAndFlush("hello server");
//            future.channel().writeAndFlush("\r\n");
            future.channel().closeFuture().sync();
//            Object message = future.channel().attr(AttributeKey.valueOf("message"));
//            System.out.println("服务端返回的数据" + message);
            //长连接，从handler中获取数据，并保证多线程并发安全
        } catch (InterruptedException e) {
            e.printStackTrace();
            workerGroup.shutdownGracefully();
        }
    }
}
