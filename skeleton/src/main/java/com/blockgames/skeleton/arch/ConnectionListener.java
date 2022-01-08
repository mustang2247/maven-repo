package com.blockgames.skeleton.arch;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

/**
 * @author mustangkong
 */
@Slf4j
public class ConnectionListener implements ChannelFutureListener {

    //private static final Logger log = LoggerFactory.getLogger( ConnectionListener.class );

    private GameClient client;
    private boolean retry;

    public ConnectionListener(GameClient client, boolean retry) {
        this.client = client;
        this.retry = retry;
    }

    @Override
    public void operationComplete(ChannelFuture channelFuture)
            throws Exception {
        if (channelFuture.isSuccess()) {
            log.info("GameClient-{} connect to server OK", client.getName());
        } else {
            log.error("GameClient-{} connect to server FAIL", client.getName());
            if (retry) {
                log.info("GameClient-{} retry connect to server", client.getName());
                Thread.sleep(1000L);
                client.reconnect();
            }
        }
    }
}
