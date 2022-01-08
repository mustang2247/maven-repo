package com.blockgames.skeleton.arch;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;

@Slf4j
public class ExceptionHandler {

    //private static final Logger log = LoggerFactory.getLogger( ExceptionHandler.class );

    public static void handle(ChannelHandlerContext ctx, Throwable cause) {

        if (cause instanceof ClosedChannelException) {
            log.info("remote peer {} closed", ctx.channel().remoteAddress());
            ctx.close();
            return;
        } else if (cause instanceof IOException) {
            log.error(cause.getMessage());
            ctx.close();
            return;
        }
        log.error(cause.getMessage(), cause);
    }

}
