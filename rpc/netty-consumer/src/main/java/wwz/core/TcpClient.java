package wwz.core;

import com.alibaba.fastjson.JSONObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.curator.framework.CuratorFramework;

import org.apache.curator.framework.api.CuratorWatcher;
import wwz.constant.Constants;
import wwz.handler.SimpleClientHandler;
import wwz.param.ClientRequest;
import wwz.param.Response;
import wwz.zk.ServerWatcher;
import wwz.zk.ZookeeperFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TcpClient {
    //public static Set<String> realServerPath = new HashSet<>();//去重and去序列号
    public static final Bootstrap bootstrap = new Bootstrap();
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
        CuratorFramework client = ZookeeperFactory.getClient();
        String host = "localhost";
        int port = 8081;
        try {
            List<String> serverPaths = client.getChildren().forPath(Constants.SERVER_PATH);
            CuratorWatcher watcher = new ServerWatcher();
            client.getChildren().usingWatcher(watcher).forPath(Constants.SERVER_PATH);

            for (String serverPath : serverPaths) {
                String[] str = serverPath.split("#");
                ChannelManager.realServerPath.add(str[0] + "#" + str[1]);
                ChannelFuture channelFuture = TcpClient.bootstrap.connect(str[0], Integer.valueOf(str[1]));
                ChannelManager.add(channelFuture);
            }
            if (ChannelManager.realServerPath.size() > 0) {
                String[] hostAndPort = ChannelManager.realServerPath.toArray()[0].toString().split("#");
                host = hostAndPort[0];
                port = Integer.valueOf(hostAndPort[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


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
        future = ChannelManager.get(ChannelManager.position);
        future.channel().writeAndFlush(JSONObject.toJSONString(request));
        future.channel().writeAndFlush("\r\n");
        ResultFuture rf = new ResultFuture(request);
        return rf.get();
    }
}
