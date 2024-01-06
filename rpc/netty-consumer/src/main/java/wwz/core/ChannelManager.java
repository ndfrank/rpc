package wwz.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ChannelManager {
    public static CopyOnWriteArrayList<String> realServerPath = new CopyOnWriteArrayList<>();
    public static AtomicInteger position = new AtomicInteger(0);
    public static CopyOnWriteArrayList<ChannelFuture> channelFutures = new CopyOnWriteArrayList<>();

    public static void removeChannel(ChannelFuture channel) {
        channelFutures.remove(channel);
    }

    public static void add(ChannelFuture channel) {
        channelFutures.add(channel);
    }

    public static void clear(){
        channelFutures.clear();
    }
// 轮询
    public static ChannelFuture get(AtomicInteger i) {
        ChannelFuture channelFuture = null;
        int size = channelFutures.size();
        if (i.get() >= size) {
            channelFuture = channelFutures.get(0);
            ChannelManager.position = new AtomicInteger(1);
        } else {
            channelFuture = channelFutures.get(i.getAndIncrement());
        }
        return channelFuture;
    }
}
