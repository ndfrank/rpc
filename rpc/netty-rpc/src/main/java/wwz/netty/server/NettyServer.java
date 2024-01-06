package wwz.netty.server;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateHandler;
import wwz.netty.constant.Constants;
import wwz.netty.factory.ZookeeperFactory;
import wwz.netty.handler.SimpleServerHandler;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class NettyServer {

    public static void main(String[] args) {
        EventLoopGroup parentGroup = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(parentGroup,childGroup)
                    .option(ChannelOption.SO_BACKLOG, 128)//可连接队列数
                    .childOption(ChannelOption.SO_KEEPALIVE, false)//心跳检测包
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline().addLast(new DelimiterBasedFrameDecoder(65535,Delimiters.lineDelimiter()[0]));
                            channel.pipeline().addLast(new StringDecoder());
                            channel.pipeline().addLast(new StringEncoder());
                            channel.pipeline().addLast(new IdleStateHandler(30, 15, 5, TimeUnit.SECONDS));//空闲状态
                            channel.pipeline().addLast(new SimpleServerHandler());
                        }
                    });
            int port  = 8082;
            ChannelFuture f = bootstrap.bind(port).sync();
            CuratorFramework client = ZookeeperFactory.create();
            InetAddress netAddress = InetAddress.getLocalHost();
            client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(Constants.SERVER_PATH + "/" + netAddress.getHostAddress() + "#" + port + "#");
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }
}
