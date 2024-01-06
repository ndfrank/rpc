package wwz.zk;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;
import wwz.core.ChannelManager;
import wwz.core.TcpClient;

import java.util.HashSet;
import java.util.List;

public class ServerWatcher implements CuratorWatcher {
    @Override
    public void process(WatchedEvent event) throws Exception {
        CuratorFramework client = ZookeeperFactory.getClient();
        String path = event.getPath();
        client.getChildren().usingWatcher(this).forPath(path);
        List<String> serverPaths = client.getChildren().forPath(path);
        ChannelManager.clear();
        for (String serverPath : serverPaths) {
            String[] str = serverPath.split("#");
            ChannelManager.realServerPath.add(str[0] + "#" + str[1]);

        }

        ChannelManager.clear();
        for (String serverPath : ChannelManager.realServerPath) {
            String[] str = serverPath.split("#");
            ChannelFuture channelFuture = TcpClient.bootstrap.connect(str[0], Integer.valueOf(str[1]));
            ChannelManager.add(channelFuture);
        }
     }
}
