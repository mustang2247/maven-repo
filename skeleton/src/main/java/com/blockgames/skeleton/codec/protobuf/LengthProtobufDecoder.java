package com.blockgames.skeleton.codec.protobuf;

import com.blockgames.skeleton.dispatch.CommandProperties;
import com.blockgames.skeleton.dispatch.CommandRegistry;
import com.blockgames.skeleton.dispatch.NetMessage;
import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author mustangkong
 */
@Slf4j
public class LengthProtobufDecoder extends ByteToMessageDecoder {

    //private static final Logger log = LoggerFactory.getLogger( LengthProtobufDecoder.class );

    private CommandRegistry registry;

    public LengthProtobufDecoder(CommandRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf msg,
                       List<Object> out) throws Exception {

        int length = msg.readInt();
        int kindId = msg.readInt();
        int msgId = msg.readInt();
        CommandProperties properties = registry.get(kindId);
        if (properties == null) {
            msg.skipBytes(length - 8);
            log.error("[LengthProtobufDecoder::decode] Can't find cmd {}", kindId);
            return;
        }
        Object message = ProtobufUtil.decode(msg, (MessageLite) properties.remoteObjectInst);
        out.add(new NetMessage(kindId, msgId, message));
    }

    @Override
    public void decodeLast(ChannelHandlerContext ctx, ByteBuf msg,
                           List<Object> out) throws Exception {
        // do nothing
    }

}
